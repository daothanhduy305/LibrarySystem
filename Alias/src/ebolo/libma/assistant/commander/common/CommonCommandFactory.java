package ebolo.libma.assistant.commander.common;

import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.commands.factory.CommandFactory;

/**
 * Command factory manages common commands for Alias
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see CommandFactory
 * @since 28/06/2017
 */

public class CommonCommandFactory implements CommandFactory<String> {
    private static CommonCommandFactory ourInstance;
    
    public static CommonCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new CommonCommandFactory();
        return ourInstance;
    }
    
    @Override
    public Command<String> getCommand(String command, Object... args) {
        switch (command) {
            case "search_book":
                return new SearchBookCommand(args);
        }
        return null;
    }
}
