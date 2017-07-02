package ebolo.libma.stub.utils.books.adder;

import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.data.data.utils.exceptions.BookNotFound;
import ebolo.libma.data.data.utils.exceptions.WrongISBN;
import ebolo.libma.stub.db.DbPortal;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the <code>AdderMain</code>
 *
 * @author Ebolo
 * @version 14/06/2017
 * @see AdderMain
 * @since 14/06/2017
 */

public class AdderController implements Controller {
    private static AdderController ourInstance;
    
    @FXML
    private Label statusLabel;
    
    private AdderController() {
    }
    
    public static AdderController getInstance() {
        if (ourInstance == null)
            ourInstance = new AdderController();
        return ourInstance;
    }
    
    /**
     * Show dialog for choosing text file and process it
     */
    @FXML
    private void add() {
        // Show dialog to chose file
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open ISBN file");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text file(.txt)", "*.txt")
        );
        File isbnFile = chooser.showOpenDialog(statusLabel.getScene().getWindow());
        
        if (isbnFile != null) {
            Task<Integer> addBookTask = new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    // Retrieve ISBN list from file
                    Set<String> isbnStrings = new HashSet<>();
                    Scanner scanner = new Scanner(isbnFile);
                    updateMessage("Reading isbn from file...");
                    while (scanner.hasNext()) {
                        isbnStrings.add(scanner.nextLine().trim());
                    }
                    Random random = new Random(); // is used to generate random number for unit total and borrowing period
                    updateMessage("Creating book list...");
                    
                    // Process isbn strings into list of books
                    List<Book> bookList = isbnStrings
                        .parallelStream()
                        .map(s -> {
                            try {
                                //noinspection unchecked
                                return new Book(
                                    s, random.nextInt(15),
                                    (long) random.nextInt(30) * (long) 86400,
                                    true);
                            } catch (WrongISBN | BookNotFound | IOException wrongISBN) {
                                // Books that cannot be found in Google database
                                System.out.println(s + " --> N/a");
                                return null;
                            }
                        })
                        .filter(book -> (book != null))
                        .collect(Collectors.toList());
                    // Push every legal book into database
                    bookList.forEach(book -> {
                        DbPortal.getInstance().getBookDb().insertOne(book.toMongoDocument());
                        updateMessage("Added book: " + book.getFullTitle());
                    });
                    updateMessage("Finished adding " + bookList.size() + " book(s).");
                    return isbnStrings.size();
                }
            };
            statusLabel.textProperty().bind(addBookTask.messageProperty());
            new Thread(addBookTask).start();
        }
    }
    
    @Override
    public void setUpUI() {
    
    }
    
    @Override
    public Window getWindow() {
        return null;
    }
}
