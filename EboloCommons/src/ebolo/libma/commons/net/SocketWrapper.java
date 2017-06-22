package ebolo.libma.commons.net;

import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import org.bson.Document;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * <code>SocketWrapper</code> is the wrapper of the JDK's <code>Socket</code> class to provide more tools/functions serving
 * the functionality of clients and central stub within the system
 *
 * @author Ebolo
 * @version 08/06/2017
 * @since 08/06/2017
 */

public class SocketWrapper {
    /**
     * one end's socket
     */
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    /**
     * queues that act as separated channels, demonstrating multiplex/de-multiplex mechanism
     */
    private BlockingQueue<Document> messageBuffer, bookUpdateBuffer, studentUpdateBuffer, transactionUpdateBuffer;
    /**
     * only used for users
     */
    private String clientId;
    /**
     * only used for users
     */
    private MetaInfo.USER_MODE userMode;
    
    public SocketWrapper(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        outputStream.flush();
        inputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        messageBuffer = new ArrayBlockingQueue<>(100);
        bookUpdateBuffer = new ArrayBlockingQueue<>(100);
        studentUpdateBuffer = new ArrayBlockingQueue<>(100);
        transactionUpdateBuffer = new ArrayBlockingQueue<>(100);
    }
    
    /**
     * Send the message to the other end
     *
     * @param message {@link Message} {@link CommandUtils}
     * @return the status of the sending process, either success or failed
     */
    
    public boolean sendMessage(Document message) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get the messages from stream and later on, messages would be pushed into relevant channel
     *
     * @return gotten message
     * @throws IOException            is thrown if there's any problem with the stream
     * @throws ClassNotFoundException is thrown if the message is not Bson compatible
     */
    
    public Document getMessage() throws IOException, ClassNotFoundException {
        return (Document) inputStream.readObject(); // TODO: handle class not found more properly
    }
    
    public void close() throws IOException {
        this.outputStream.close();
        this.inputStream.close();
        this.socket.close();
    }
    
    public BlockingQueue<Document> getMessageBuffer() {
        return messageBuffer;
    }
    
    BlockingQueue<Document> getBookUpdateBuffer() {
        return bookUpdateBuffer;
    }
    
    BlockingQueue<Document> getStudentUpdateBuffer() {
        return studentUpdateBuffer;
    }
    
    BlockingQueue<Document> getTransactionUpdateBuffer() {
        return transactionUpdateBuffer;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public MetaInfo.USER_MODE getUserMode() {
        return userMode;
    }
    
    public void setUserMode(MetaInfo.USER_MODE userMode) {
        this.userMode = userMode;
    }
}
