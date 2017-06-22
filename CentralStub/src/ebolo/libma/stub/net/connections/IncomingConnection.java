package ebolo.libma.stub.net.connections;

import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.stub.commander.commands.CommandCenter;
import ebolo.libma.stub.net.managers.ActiveUserManager;
import org.bson.Document;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

/**
 * Thread job to handle an active connection
 *
 * @author Ebolo
 * @version 07/06/2017
 * @since 06/06/2017
 */

public class IncomingConnection implements Runnable {
    private SocketWrapper client;
    
    public IncomingConnection(SocketWrapper client) {
        this.client = client;
    }
    
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                // Read incoming message from client
                Document received = client.getMessage();
                
                // Categorize message to appropriate handling channel
                if (received.getString("command") != null)
                    CommandCenter.getInstance().addNewCommand(
                        client,
                        received
                    );
                else if (received.getString("message") != null)
                    client.getMessageBuffer().put(received);
            } catch (EOFException | SocketException ignored) {
                try {
                    if (client != null)
                        ActiveUserManager.getInstance().deregisterUser(client.getClientId());
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
