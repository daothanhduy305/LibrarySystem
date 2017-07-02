package ebolo.libma.data.db.local;

import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.commons.ui.ViewStatus;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.data.ui.book.BookUIWrapper;

/**
 * This is the implementation class of the <code>ListManager</code> for managing raw data book and its ui wrapper
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see ListManager
 * @since 20/06/2017
 */

public class BookListManager extends ListManager<Book, BookUIWrapper> {
    private static BookListManager ourInstance;
    
    public static BookListManager getInstance() {
        if (ourInstance == null)
            ourInstance = new BookListManager();
        return ourInstance;
    }
    
    @Override
    public void syncStub() {
        new Thread(() -> {
            ViewStatus.getInstance().updateStatus("Loading local db...");
            BookListManager.getInstance().loadLocal();
            ViewStatus.getInstance().updateStatus("Ready.");
            CommandUtils.sendCommand(
                MetaInfo.USER_MODE.Kernel,
                StubCommunication.getInstance().getStub(),
                "request_update",
                BookListManager.getInstance().getCurrentVersion(),
                "book"
            );
        }).start();
    }
}
