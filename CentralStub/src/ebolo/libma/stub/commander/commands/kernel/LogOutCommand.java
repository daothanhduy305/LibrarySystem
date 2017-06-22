package ebolo.libma.stub.commander.commands.kernel;

import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.stub.commander.commands.Command;
import ebolo.libma.stub.net.managers.ActiveUserManager;

/**
 * Command for a user requiring a log out
 *
 * @author Ebolo
 * @version 14/06/2017
 * @see Command
 * @since 14/06/2017
 */

public class LogOutCommand extends Command {
    LogOutCommand(SocketWrapper client, Object[] args) {
        super(client, args);
    }
    
    @Override
    protected void notifyCommandResult() {
    
    }
    
    @Override
    protected boolean checkCorrectness() {
        return true;
    }
    
    @Override
    protected boolean legalAction() throws Exception {
        ActiveUserManager.getInstance().deregisterUser(client.getClientId());
        return true;
    }
}
