package ebolo.libma.stub.commander.commands;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Central processing unit class to process all the incoming internal commands
 *
 * @author Ebolo
 * @version 07/06/2017
 * @since 06/06/2017
 */

public class CommandProcessor {
    private static CommandProcessor ourInstance;
    private BlockingQueue<Command> commandQueue;
    private Thread reception;
    
    private CommandProcessor() {
        commandQueue = new ArrayBlockingQueue<>(1000);
        reception = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    // get command from queue
                    final Command command = commandQueue.take();
                    // then process
                    new Thread(command).start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public static CommandProcessor getInstance() {
        if (ourInstance == null)
            ourInstance = new CommandProcessor();
        return ourInstance;
    }
    
    /**
     * Add new command into command queue
     *
     * @param command incoming internal command
     */
    void addNewCommand(Command command) {
        try {
            commandQueue.put(command);
        } catch (InterruptedException ignored) {
        }
    }
    
    public void startProcessing() {
        reception.start();
    }
    
    public void stopProcessing() {
        reception.interrupt();
    }
}
