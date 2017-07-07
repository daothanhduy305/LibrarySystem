package ebolo.libma.management.commander;

import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.commands.factory.ClientCommandFactory;
import ebolo.libma.commons.commands.factory.CommandFactory;
import ebolo.libma.commons.commands.proc.SingleCommandProcessor;
import ebolo.libma.management.commander.book.BookCommandFactory;
import ebolo.libma.management.commander.student.StudentCommandFactory;
import ebolo.libma.management.commander.transaction.TransactionCommandFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Central command manager for management application
 *
 * @author Ebolo
 * @version 2//06/2017
 * @see CommandFactory
 * @since 28/06/2017
 */

public class CentralCommandFactory implements ClientCommandFactory {
    private static CentralCommandFactory ourInstance;
    private Map<String, CommandFactory<String>> commandFactoryMap;
    
    private CentralCommandFactory() {
        commandFactoryMap = new HashMap<>(2);
        commandFactoryMap.put("book", BookCommandFactory.getInstance());
        commandFactoryMap.put("student", StudentCommandFactory.getInstance());
        commandFactoryMap.put("transaction", TransactionCommandFactory.getInstance());
    }
    
    public static CentralCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new CentralCommandFactory();
        return ourInstance;
    }
    
    @Override
    public Command<String> getCommand(String command, Object... args) {
        String[] unmarshalledCommand = command.split("\\.");
        if (unmarshalledCommand.length > 1) {
            return commandFactoryMap
                .get(unmarshalledCommand[0])
                .getCommand(unmarshalledCommand[1], args);
        }
        return null;
    }
    
    @Override
    public Future<String> run(String command, Object... args) {
        return SingleCommandProcessor.getInstance().submitCommand(getCommand(command, args));
    }
}
