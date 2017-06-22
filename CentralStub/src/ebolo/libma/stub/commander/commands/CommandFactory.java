package ebolo.libma.stub.commander.commands;

import com.sun.istack.internal.Nullable;
import ebolo.libma.commons.net.SocketWrapper;

/**
 * Interface for classes that serve as factory producing internal commands
 *
 * @author Ebolo
 * @version 07/06/2017
 * @since 06/06/2017
 */

public interface CommandFactory {
    /**
     * This method would fetch the appropriate command based on the arguments
     *
     * @param client  command source
     * @param command command keyword
     * @param args    command arguments
     * @return the appropriate internal command
     */
    Command getCommand(@Nullable SocketWrapper client, String command, Object[] args);
}
