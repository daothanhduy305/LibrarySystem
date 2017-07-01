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
 * Client's side command for adding new book to the system
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see Command
 * @see ebolo.libma.management.commander.book.ui.UIAddBookCommand
 * @since 28/06/2017
 */

public class AddBookCommand extends Command<String> {
    private Book book;
    
    AddBookCommand(Object[] args) {
        super(args);
        this.book = (Book) args[0];
    }
    
    @Override
    public String call() throws Exception {
        if (CommandUtils.sendCommand(
            MetaInfo.USER_MODE.Librarian,
            StubCommunication.getInstance().getStub(),
            "add_book", book)) {
            Document response = StubCommunication
                .getInstance()
                .getStub()
                .getMessageBuffer()
                .take();
            if (!response.getString("message").equals("failed")) {
                BookListManager.getInstance().add(book, new BookUIWrapper(book, response.getString("package")));
                return "success";
            } else
                return response.getString("package");
        }
        return "Server is not available";
    }
}
