package ebolo.libma.commons.commands.proc;

import ebolo.libma.commons.commands.command.Command;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central processing unit abstract class for processing commands
 *
 * @author Ebolo
 * @version 28/06/2017
 * @since 28/06/2017
 */

public abstract class CommandProcessor<V> {
    ExecutorService executorService;
    
    @SuppressWarnings("unchecked")
    public Future<V> submitCommand(Command command) {
        return executorService.submit(command);
    }
    
    public void shutdown() {
        if (executorService != null) {
            try {
                Logger.getLogger("myApp").log(Level.INFO, "attempt to shutdown executor");
                executorService.shutdown();
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Logger.getLogger("myApp").log(Level.SEVERE, "tasks interrupted");
            } finally {
                if (!executorService.isTerminated()) {
                    Logger.getLogger("myApp").log(Level.SEVERE, "cancel non-finished tasks");
                }
                executorService.shutdownNow();
                Logger.getLogger("myApp").log(Level.INFO, "shutdown finished");
            }
        }
    }
}
