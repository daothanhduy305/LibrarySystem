package ebolo.libma.management.commander.transaction;

import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.commands.factory.CommandFactory;

/**
 * Set of commands on transaction objects for librarian on client side
 *
 * @author Ebolo
 * @version 05/06/2017
 * @see CommandFactory
 * @since 05/06/2017
 */
public class TransactionCommandFactory implements CommandFactory<String> {
    private static TransactionCommandFactory ourInstance;
    
    private TransactionCommandFactory() {
    }
    
    public static TransactionCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new TransactionCommandFactory();
        return ourInstance;
    }
    
    @Override
    public Command<String> getCommand(String command, Object... args) {
        switch (command) {
            case "finish_transactions":
                return new FinishTransactionsCommand(args);
        }
        return null;
    }
}
