package ebolo.libma.commons.commands.factory;

import ebolo.libma.commons.commands.command.Command;

/**
 * Interface for classes that serve as factory producing internal commands
 *
 * @author Ebolo
 * @version 28/06/2017
 * @since 28/06/2017
 */

public interface CommandFactory<V> {
    /**
     * This method would fetch the appropriate command based on the arguments
     *
     * @param command command keyword
     * @param args    command arguments
     * @return the appropriate internal command
     */
    Command<V> getCommand(String command, Object... args);
}
