package ebolo.libma.data.db.local;

import ebolo.libma.data.data.raw.transaction.TransactionWrapper;
import ebolo.libma.data.data.ui.transaction.TransactionUIWrapper;

/**
 * This is the implementation class of the <code>ListManager</code> for managing raw data transaction and its ui wrapper
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see ListManager
 * @since 20/06/2017
 */

public class TransactionListManager extends ListManager<TransactionWrapper, TransactionUIWrapper> {
    private static TransactionListManager ourInstance;
    
    public static TransactionListManager getInstance() {
        if (ourInstance == null)
            ourInstance = new TransactionListManager();
        return ourInstance;
    }
}
