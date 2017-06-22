package ebolo.libma.management.commander;

import ebolo.libma.commons.commands.CommandProcessor;
import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.commands.Commands;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import ebolo.libma.data.db.local.BookListManager;
import javafx.scene.control.Alert;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Set of commands on book objects for librarian on client side
 *
 * @author Ebolo
 * @version 10/06/2017
 * @see Commands
 * @since 10/06/2017
 */

public class BookCommands extends Commands {
    /**
     * Command adding new book
     *
     * @param book adding book
     * @return a string indicates if the result is success or a failure statement
     */
    public static Future<String> addNewBook(final Book book) {
        return CommandProcessor.getInstance().getExecutorService().submit(() -> {
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
        });
    }
    
    /**
     * Command removing books
     *
     * @param deletingBooks list of deleting books
     * @return a string indicates if the result is success or a failure statement
     */
    @SuppressWarnings("unchecked")
    public static Future<String> removeBook(final List<BookUIWrapper> deletingBooks) {
        return CommandProcessor.getInstance().getExecutorService().submit(() -> {
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
        });
    }
    
    /**
     * Command modifying a book
     *
     * @param modifyingBook modifying book as raw data
     * @param bookUIWrapper modifying book as ui wrapper (directly from ui list component)
     * @return a string indicates if the result is success or a failure statement
     */
    public static Future<String> modifyBook(final Book modifyingBook, final BookUIWrapper bookUIWrapper) {
        return CommandProcessor.getInstance().getExecutorService().submit(() -> {
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
        });
    }
}
