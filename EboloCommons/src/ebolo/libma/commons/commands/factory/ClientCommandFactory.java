package ebolo.libma.commons.commands.factory;

import java.util.concurrent.Future;

/**
 * Interface for classes that serve as factory producing internal commands for clients
 *
 * @author Ebolo
 * @version 28/06/2017
 * @since 28/06/2017
 */

public interface ClientCommandFactory extends CommandFactory<String> {
    /**
     * Run and return future result
     *
     * @param command command name
     * @param args    command arguments
     * @return result in future
     */
    Future<String> run(String command, Object... args);
}
