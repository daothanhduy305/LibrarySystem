package ebolo.libma.assistant.commander;

import ebolo.libma.assistant.commander.common.CommonCommandFactory;
import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.commands.factory.ClientCommandFactory;
import ebolo.libma.commons.commands.factory.CommandFactory;
import ebolo.libma.commons.commands.proc.SingleCommandProcessor;

import java.util.concurrent.Future;

/**
 * Central command manager for Alias
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see ClientCommandFactory
 * @since 28/06/2017
 */

public class CentralCommandFactory implements ClientCommandFactory {
    private static CentralCommandFactory ourInstance;
    /**
     * This could be considered as a plug in for Alias
     */
    private ClientCommandFactory extendedFactory;
    
    private CentralCommandFactory() {
    }
    
    public static CentralCommandFactory getInstance() {
        if (ourInstance == null)
            ourInstance = new CentralCommandFactory();
        return ourInstance;
    }
    
    @Override
    public Future<String> run(String command, Object... args) {
        return SingleCommandProcessor.getInstance().submitCommand(getCommand(command, args));
    }
    
    @Override
    public Command<String> getCommand(String command, Object... args) {
        CommandFactory<String> commandFactory = command.contains(".") ?
            extendedFactory : CommonCommandFactory.getInstance();
        return commandFactory.getCommand(
            command.contains(".") ? command.substring(command.indexOf('.') + 1) : command,
            args
        );
    }
    
    public void setExtendedFactory(ClientCommandFactory extendedFactory) {
        this.extendedFactory = extendedFactory;
    }
}
