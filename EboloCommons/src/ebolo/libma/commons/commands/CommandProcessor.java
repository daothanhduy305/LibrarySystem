package ebolo.libma.commons.commands;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is where the commands being processed for client applications
 *
 * @author Ebolo
 * @version 15/06/2017
 * @since 15/06/2017
 */

public class CommandProcessor {
    private static CommandProcessor ourInstance;
    private ExecutorService executorService;
    
    private CommandProcessor() {
        executorService = Executors.newSingleThreadExecutor();
    }
    
    public static CommandProcessor getInstance() {
        if (ourInstance == null)
            ourInstance = new CommandProcessor();
        return ourInstance;
    }
    
    public ExecutorService getExecutorService() {
        return executorService;
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
