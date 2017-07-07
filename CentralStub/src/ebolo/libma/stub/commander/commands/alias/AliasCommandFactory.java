package ebolo.libma.stub.commander.commands.alias;

import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.commands.factory.StubCommandFactory;
import ebolo.libma.commons.net.SocketWrapper;

/**
 * Alias command factory manages available set of generic internal commands which are used by Alias
 *
 * @author Ebolo
 * @version 26/06/2017
 * @see StubCommandFactory
 * @since 26/06/2017
 */

public class AliasCommandFactory implements StubCommandFactory {
    private static AliasCommandFactory ourInstance;
    
    private AliasCommandFactory() {
    }
    
    public static AliasCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new AliasCommandFactory();
        return ourInstance;
    }
    
    @Override
    public StubCommand getCommand(SocketWrapper client, String command, Object... args) {
        switch (command) {
            case "search":
                return new SearchBookCommand(client, args);
        }
        return null;
    }
}
