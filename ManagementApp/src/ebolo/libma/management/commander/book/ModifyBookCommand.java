package ebolo.libma.management.commander.book;

import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import ebolo.libma.data.db.local.BookListManager;
import org.bson.Document;

/**
 * Client's side command for modifying book(s) in the system
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see Command
 * @since 28/06/2017
 */

public class ModifyBookCommand extends Command<String> {
    private Book modifyingBook;
    private BookUIWrapper bookUIWrapper;
    
    ModifyBookCommand(Object[] args) {
        super(args);
        modifyingBook = (Book) args[0];
        bookUIWrapper = (BookUIWrapper) args[1];
    }
    
    @Override
    public String call() throws Exception {
        if (CommandUtils.sendCommand(
            MetaInfo.USER_MODE.Librarian,
            StubCommunication.getInstance().getStub(),
            "modify_book",
            bookUIWrapper.getObjectId(), modifyingBook)) {
            Document response = StubCommunication
                .getInstance()
                .getStub()
                .getMessageBuffer()
                .take();
            if (!response.getString("message").equals("failed")) {
                BookListManager.getInstance().modify(modifyingBook, bookUIWrapper);
                return "success";
            } else
                return response.getString("package");
        }
        return "Server is not available";
    }
}
