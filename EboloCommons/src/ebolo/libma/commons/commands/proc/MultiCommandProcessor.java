package ebolo.libma.commons.commands.proc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Central processing unit class that can handle multiple commands at once
 *
 * @author Ebolo
 * @version 28/06/2017
 * @since 06/06/2017
 */

public class MultiCommandProcessor extends CommandProcessor<String> {
    private static MultiCommandProcessor ourInstance;
    private BlockingQueue<Runnable> commandQueue;
    
    private MultiCommandProcessor() {
        commandQueue = new ArrayBlockingQueue<>(1000);
        executorService = new ThreadPoolExecutor(
            3, 1000, 0L,
            TimeUnit.MILLISECONDS,
            commandQueue
        );
    }
    
    public static MultiCommandProcessor getInstance() {
        if (ourInstance == null)
            ourInstance = new MultiCommandProcessor();
        return ourInstance;
    }
}
