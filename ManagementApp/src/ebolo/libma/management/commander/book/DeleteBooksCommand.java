package ebolo.libma.management.commander.book;

import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import ebolo.libma.data.db.local.BookListManager;
import javafx.scene.control.Alert;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Client's side command for deleting new book from the system
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see Command
 * @since 28/06/2017
 */

public class DeleteBooksCommand extends Command<String> {
    private List<BookUIWrapper> deletingBooks;
    
    @SuppressWarnings("unchecked")
    DeleteBooksCommand(Object[] args) {
        super(args);
        deletingBooks = (List<BookUIWrapper>) args[0];
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String call() throws Exception {
        if (CommandUtils.sendCommand(
            MetaInfo.USER_MODE.Librarian,
            StubCommunication.getInstance().getStub(),
            "remove_books",
            deletingBooks.parallelStream().map(BookUIWrapper::getObjectId).collect(Collectors.toList()))) {
            Document response = StubCommunication
                .getInstance()
                .getStub()
                .getMessageBuffer()
                .take();
            if (!response.getString("message").equals("failed")) {
                List<String> deletedBooks = (List<String>) response.get("package");
                
                UIFactory.showAnnouncement(
                    Alert.AlertType.INFORMATION,
                    "Deleted successfully: " + deletedBooks.size() + '/' + deletingBooks.size()
                );
                
                BookListManager.getInstance().delete(
                    deletingBooks
                        .parallelStream()
                        .filter(bookUIWrapper -> deletedBooks
                            .contains(bookUIWrapper.getObjectId())
                        ).collect(Collectors.toList())
                );
                return "success";
            } else
                return response.getString("package");
        }
        return "Server is not available";
    }
}
