package ebolo.libma.stub.commander.commands.kernel;

import com.mongodb.client.MongoCollection;
import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.stub.commander.utils.TransactionUtils;
import ebolo.libma.stub.db.DbPortal;
import ebolo.libma.stub.db.updates.UpdateFactory;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StubCommand for requesting update from database
 *
 * @author Ebolo
 * @version 13/06/2017
 * @see StubCommand
 * @since 13/06/2017
 */

public class UpdateRequestCommand extends StubCommand {
    private long currentVersion;
    private String type;
    
    UpdateRequestCommand(SocketWrapper client, Object[] args) {
        super(client, args);
    }
    
    @Override
    protected void notifyCommandResult() {
    
    }
    
    @Override
    protected boolean checkCorrectness() {
        try {
            if (args.length == 2) {
                currentVersion = (long) args[0];
                type = (String) args[1];
                return type.equals("book") || type.equals("student") || type.equals("transaction");
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected boolean legalAction() throws Exception {
        // Switch to appropriate collection
        MongoCollection<Document> collection;
        switch (type) {
            case "book":
                collection = DbPortal.getInstance().getBookDb();
                break;
            case "student":
                collection = DbPortal.getInstance().getUserDb();
                break;
            case "transaction":
                collection = DbPortal.getInstance().getTransactionDb();
                break;
            default:
                return false;
        }
        // get all available entries that are newer than client's version
        Document query = new Document("last_modified", new Document("$gt", currentVersion));
        if (type.equals("student")) // get students from user database
            query.put("user_mode", "Student");
        List<Document> updates = collection
            .find(query)
            .into(new ArrayList<>());
        if (type.equals("transaction")) {
            // if a student is requiring update then filter out the list to his/her transactions only
            if (client.getUserMode() == MetaInfo.USER_MODE.Student)
                updates = updates.parallelStream()
                    .filter(document -> document.getObjectId("_id").toString().equals(client.getClientId()))
                    .collect(Collectors.toList());
            updates = updates.parallelStream().map(TransactionUtils::toTransactionWrapperDocument).collect(Collectors.toList());
        }
        if (updates != null && updates.size() > 0) {
            client.sendMessage(UpdateFactory.createUpdate(updates, type));
        }
        return true;
    }
    
    
}
