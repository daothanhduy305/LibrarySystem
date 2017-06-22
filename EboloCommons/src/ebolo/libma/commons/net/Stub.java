package ebolo.libma.commons.net;

import org.bson.Document;

import javax.net.SocketFactory;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the representing class for the central stub from the clients' point
 *
 * @author Ebolo
 * @version 08/06/2017
 * @since 08/06/2017
 */

public class Stub {
    private SocketWrapper stubSocket;
    private Thread stubCommunicationThread;
    private String stubAddress;
    private int port;
    private volatile boolean connected;
    
    Stub(String stubAddress, int port) {
        this.stubAddress = stubAddress;
        this.port = port;
    }
    
    void startStubCommunication() {
        if (stubCommunicationThread == null)
            stubCommunicationThread = new Thread(() -> {
                if (stubSocket == null) {
                    
                    // Trying to connect to stub
                    connected = false;
                    while (!connected && !Thread.currentThread().isInterrupted()) {
                        try {
                            stubSocket = new SocketWrapper(SocketFactory.getDefault().createSocket(stubAddress, port));
                            connected = true;
                        } catch (IOException ignored) {
                        }
                    }
                    
                    // Stub connected, now this thread would handle incoming messages
                    while (!Thread.interrupted()) {
                        try {
                            Document received = stubSocket != null ? stubSocket.getMessage() : null;
                            if ((received != null ? received.get("message") : null) != null) {
                                
                                // Categorize messages based on message type and put it into the correct channel
                                if (received.get("message").equals("update")) {
                                    Logger.getLogger("myApp").log(Level.INFO, "New update arrives");
                                    if (received.getString("type").equals("book"))
                                        stubSocket.getBookUpdateBuffer().put(received);
                                    else if (received.getString("type").equals("student"))
                                        stubSocket.getStudentUpdateBuffer().put(received);
                                    else
                                        stubSocket.getTransactionUpdateBuffer().put(received);
                                } else
                                    stubSocket.getMessageBuffer().put(received);
                            }
                        } catch (IOException | NullPointerException | InterruptedException e) {
                            break; // TODO: examine InterruptedException (for message buffer impact)
                        } catch (ClassNotFoundException ignored) {
                        }
                    }
                }
            });
        stubCommunicationThread.start();
    }
    
    void stopStubCommunication() {
        stubCommunicationThread.interrupt();
        if (stubSocket != null) {
            try {
                stubSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean sendMessage(Document message) {
        return stubSocket.sendMessage(message);
    }
    
    public BlockingQueue<Document> getMessageBuffer() {
        return stubSocket.getMessageBuffer();
    }
    
    BlockingQueue<Document> getBookUpdateBuffer() {
        return stubSocket.getBookUpdateBuffer();
    }
    
    BlockingQueue<Document> getStudentUpdateBuffer() {
        return stubSocket.getStudentUpdateBuffer();
    }
    
    BlockingQueue<Document> getTransactionUpdateBuffer() {
        return stubSocket.getTransactionUpdateBuffer();
    }
    
    synchronized public boolean isConnected() {
        return connected;
    }
}
