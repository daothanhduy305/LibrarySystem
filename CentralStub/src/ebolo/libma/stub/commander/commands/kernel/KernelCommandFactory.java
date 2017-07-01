package ebolo.libma.stub.commander.commands.kernel;

import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.commands.factory.StubCommandFactory;
import ebolo.libma.commons.net.SocketWrapper;

/**
 * Kernel command factory manages available set of generic internal commands which are used by either students or librarian
 *
 * @author Ebolo
 * @version 07/06/2017
 * @see StubCommandFactory
 * @since 06/06/2017
 */

public class KernelCommandFactory implements StubCommandFactory {
    private static KernelCommandFactory ourInstance;
    
    private KernelCommandFactory() {
    }
    
    public static KernelCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new KernelCommandFactory();
        return ourInstance;
    }
    
    @Override
    public StubCommand getCommand(SocketWrapper client, String command, Object[] args) {
        switch (command) {
            case "auth":
                return new AuthenticationCommand(client, args);
            case "log_out":
                return new LogOutCommand(client, args);
            case "request_update":
                return new UpdateRequestCommand(client, args);
            case "request_id":
                return new ClientIdReq(client, args);
        }
        return null;
    }
}
