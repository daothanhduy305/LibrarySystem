package ebolo.libma.stub.net.listeners;

import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.stub.net.connections.IncomingConnection;
import ebolo.libma.stub.utils.configs.StubConfigurations;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class that listens to all incoming connections and spawn children threads to handle them intuitively
 *
 * @author Ebolo
 * @version 07/06/2017
 * @since 06/06/2017
 */

public class IncomingListener {
    private static IncomingListener ourInstance;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private ExecutorService clientService;
    
    private IncomingListener() {
        clientService = Executors.newFixedThreadPool(100);
    }
    
    public static IncomingListener getInstance() {
        if (ourInstance == null)
            ourInstance = new IncomingListener();
        return ourInstance;
    }
    
    public boolean startListening() {
        try {
            // set up server
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(StubConfigurations.getServicePort());
            serverThread = new Thread(() -> {
                while (!Thread.interrupted()) {
                    try {
                        // submit new client to the thread-pool to handle
                        clientService.submit(new IncomingConnection(new SocketWrapper(serverSocket.accept())));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Stopping Listening");
                    }
                }
            });
            serverThread.start();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public void stopListening() throws IOException {
        if (serverSocket != null)
            serverSocket.close();
        if (serverThread != null && serverThread.isAlive())
            serverThread.interrupt();
        try {
            System.out.println("attempt to shutdown executor");
            clientService.shutdown();
            clientService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        } finally {
            if (!clientService.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            clientService.shutdownNow();
            System.out.println("shutdown finished");
        }
    }
}
