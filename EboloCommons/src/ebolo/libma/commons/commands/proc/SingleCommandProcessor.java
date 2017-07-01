package ebolo.libma.commons.commands.proc;

import java.util.concurrent.Executors;

/**
 * Central processing unit class that can handle one command at once
 *
 * @author Ebolo
 * @version 28/06/2017
 * @since 15/06/2017
 */

public class SingleCommandProcessor extends CommandProcessor<String> {
    private static SingleCommandProcessor ourInstance;
    
    private SingleCommandProcessor() {
        executorService = Executors.newSingleThreadExecutor();
    }
    
    public static SingleCommandProcessor getInstance() {
        if (ourInstance == null)
            ourInstance = new SingleCommandProcessor();
        return ourInstance;
    }
}
