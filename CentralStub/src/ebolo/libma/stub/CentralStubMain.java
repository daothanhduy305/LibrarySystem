package ebolo.libma.stub;

import ebolo.libma.stub.db.DbPortal;
import ebolo.libma.stub.net.listeners.IncomingListener;

/**
 * Main class for central stub
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class CentralStubMain {
    public static void main(String[] args) {
        DbPortal.getInstance().setUp();
        IncomingListener.getInstance().startListening();
    }
}
