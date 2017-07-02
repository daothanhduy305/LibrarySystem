package ebolo.libma.client.commander;

import ebolo.libma.client.commander.commands.ReserveBookCommand;
import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.commands.factory.ClientCommandFactory;
import ebolo.libma.commons.commands.proc.SingleCommandProcessor;

import java.util.concurrent.Future;

/**
 * Set of commands for student on client side
 *
 * @author Ebolo
 * @version 13/06/2017
 * @see ClientCommandFactory
 * @since 13/06/2017
 */

public class StudentCommandFactory implements ClientCommandFactory {
    private static StudentCommandFactory ourInstance;
    
    private StudentCommandFactory() {
    }
    
    public static StudentCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new StudentCommandFactory();
        return ourInstance;
    }
    
    @Override
    public Command<String> getCommand(String command, Object[] args) {
        command = command.split("\\.")[1];
        switch (command) {
            case "reserve_book":
                return new ReserveBookCommand(args);
        }
        return null;
    }
    
    @Override
    public Future<String> run(String command, Object... args) {
        return SingleCommandProcessor.getInstance().submitCommand(getCommand(command, args));
    }
}
