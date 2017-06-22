package ebolo.libma.stub.commander.commands.kernel;

import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.stub.commander.commands.Command;
import ebolo.libma.stub.db.DbPortal;
import ebolo.libma.stub.net.managers.ActiveUserManager;
import org.bson.Document;

/**
 * Command for a user requiring a log out
 *
 * @author Ebolo
 * @version 07/06/2017
 * @see Command
 * @since 07/06/2017
 */

public class AuthenticationCommand extends Command {
    private String[] translatedArgs;
    
    AuthenticationCommand(SocketWrapper client, Object[] args) {
        super(client, args);
    }
    
    @Override
    protected void notifyCommandResult() {
    
    }
    
    @Override
    protected boolean checkCorrectness() {
        try {
            if (args.length == 2) {
                translatedArgs = (String[]) args;
                return true;
            }
            throw new Exception();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected boolean legalAction() throws Exception {
        // check if username exists
        Document userInfo = DbPortal.getInstance()
            .getUserDb()
            .find(new Document("auth.username", translatedArgs[0]))
            .first();
        if (userInfo != null) {
            // retrieve stored hashed password
            String storedHashedPassword = (String) ((Document) userInfo.get("auth")).get("password");
            
            // check if user type is correct
            if (!translatedArgs[1].equals(userInfo.getString("user_mode"))) {
                failedReason = "You do not have sufficient permission to continue!";
                return false;
            }
            
            client.sendMessage(Message.messageGenerate("auth", storedHashedPassword));
            // send hashed password and retrieve the hashed one from client
            String hashedPassword = client.getMessageBuffer().take().getString("package");
            if (storedHashedPassword.equals(hashedPassword)) {
                // if they match then register the user to the system
                client.setClientId(userInfo.getObjectId("_id").toString());
                client.setUserMode(MetaInfo.USER_MODE.valueOf(translatedArgs[1]));
                ActiveUserManager.getInstance().registerUser(
                    client.getClientId(),
                    client
                );
                return true;
            }
            failedReason = "Wrong credential information!";
            return false;
        }
        failedReason = "User not found";
        return false;
    }
}
