package ebolo.libma.commons.commands.factory;

import com.sun.istack.internal.Nullable;
import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.net.SocketWrapper;

/**
 * Interface for classes that serve as factory producing internal commands for stub
 *
 * @author Ebolo
 * @version 07/06/2017
 * @since 06/06/2017
 */

public interface StubCommandFactory {
    /**
     * This method would fetch the appropriate command based on the arguments
     *
     * @param client  command source
     * @param command command keyword
     * @param args    command arguments
     * @return the appropriate internal command
     */
    StubCommand getCommand(@Nullable SocketWrapper client, String command, Object... args);
}
