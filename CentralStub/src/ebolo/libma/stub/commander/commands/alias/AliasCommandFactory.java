package ebolo.libma.stub.commander.commands.alias;

import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.stub.commander.commands.Command;
import ebolo.libma.stub.commander.commands.CommandFactory;

/**
 * Alias command factory manages available set of generic internal commands which are used by Alias
 *
 * @author Ebolo
 * @version 26/06/2017
 * @see CommandFactory
 * @since 26/06/2017
 */

public class AliasCommandFactory implements CommandFactory {
    private static AliasCommandFactory ourInstance;
    
    private AliasCommandFactory() {
    }
    
    public static AliasCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new AliasCommandFactory();
        return ourInstance;
    }
    
    @Override
    public Command getCommand(SocketWrapper client, String command, Object[] args) {
        switch (command) {
            case "search":
                return new SearchBookCommand(client, args);
        }
        return null;
    }
}
