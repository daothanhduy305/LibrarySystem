package ebolo.libma.commons.net;

import ebolo.libma.commons.ui.ViewStatus;
import ebolo.libma.data.db.local.BookListManager;
import ebolo.libma.data.db.local.ListManager;
import ebolo.libma.data.db.local.StudentListManager;
import ebolo.libma.data.db.local.TransactionListManager;
import ebolo.libma.generic.net.GenericNetConfigs;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * This acts like the wrapper class representing for the central stub communication. It also means there should only be one
 * representative of this at a time
 *
 * @author Ebolo
 * @version 09/06/2017
 * @since 08/06/2017
 */

public class StubCommunication {
    private static StubCommunication ourInstance;
    private Stub stub;
    /**
     * separated threads monitoring relevant channel
     */
    private Thread bookUpdateMonitorThread, studentUpdateMonitorThread, transactionUpdateMonitorThread;
    
    private StubCommunication() {
        stub = new Stub(GenericNetConfigs.getStubAddress(), GenericNetConfigs.getStubPort());
    }
    
    public static StubCommunication getInstance() {
        if (ourInstance == null)
            ourInstance = new StubCommunication();
        return ourInstance;
    }
    
    public Stub getStub() {
        return stub;
    }
    
    public void startStubCommunication() {
        stub.startStubCommunication();
    }
    
    public void stopStubCommunication() {
        if (bookUpdateMonitorThread != null)
            bookUpdateMonitorThread.interrupt();
        if (studentUpdateMonitorThread != null)
            studentUpdateMonitorThread.interrupt();
        if (transactionUpdateMonitorThread != null)
            transactionUpdateMonitorThread.interrupt();
        stub.stopStubCommunication();
    }
    
    public void startBookMonitorThreads() {
        bookUpdateMonitorThread = new Thread(monitoringJob(BookListManager.getInstance(), stub.getBookUpdateBuffer()));
        bookUpdateMonitorThread.start();
    }
    
    public void startStudentMonitorThreads() {
        studentUpdateMonitorThread = new Thread(monitoringJob(StudentListManager.getInstance(), stub.getStudentUpdateBuffer()));
        studentUpdateMonitorThread.start();
    }
    
    public void startTransactionMonitorThreads() {
        transactionUpdateMonitorThread = new Thread(monitoringJob(
            TransactionListManager.getInstance(), stub.getTransactionUpdateBuffer())
        );
        transactionUpdateMonitorThread.start();
    }
    
    /**
     * Create monitoring job for requesting thread
     *
     * @param listManager indicates the <code>ListManager</code> in use {@link ListManager}
     * @param buffer      indicates the channel in use {@link SocketWrapper#bookUpdateBuffer} {@link SocketWrapper#studentUpdateBuffer}
     *                    {@link SocketWrapper#transactionUpdateBuffer}
     * @return the desired job
     */
    
    @SuppressWarnings("unchecked")
    private Runnable monitoringJob(ListManager listManager, BlockingQueue<Document> buffer) {
        return () -> {
            while (!Thread.interrupted()) {
                try {
                    List<Document> update = (List<Document>) buffer.take().get("package");
                    synchronized (listManager) {
                        ViewStatus.getInstance().updateStatus(
                            "Synchronizing " + listManager.getType().toLowerCase() + " DB...");
                        listManager.sync(update);
                        ViewStatus.getInstance().updateStatus(
                            "Finished synchronizing " + listManager.getType().toLowerCase() + " DB.");
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        };
    }
}
