package ebolo.libma.client.ui.controllers;

import ebolo.libma.client.commander.StudentCommands;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Window;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Controller (for UI) of viewing-book-info window
 *
 * @author Ebolo
 * @version 15/06/2017
 * @since 15/06/2017
 */

public class BookInfoController implements Controller {
    private static BookInfoController ourInstance;
    private BookUIWrapper bookUIWrapper;
    
    @FXML
    private TextField titleTextField, authorsTextField, categoriesTextField, periodTextField, publisherTextField,
        pagesTextField, dateTextField, languageTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Label availabilityLabel;
    
    private BookInfoController() {
    }
    
    static BookInfoController getInstance() {
        if (ourInstance == null)
            ourInstance = new BookInfoController();
        return ourInstance;
    }
    
    /**
     * Setup view for currently showing book
     *
     * @param bookUIWrapper ui book chosen from table view
     */
    void setBookUIWrapper(BookUIWrapper bookUIWrapper) {
        this.bookUIWrapper = bookUIWrapper;
        
        titleTextField.setText(bookUIWrapper.getTitle());
        authorsTextField.setText(bookUIWrapper.getAuthor());
        categoriesTextField.setText(bookUIWrapper.getCategories());
        String periodStr;
        if (bookUIWrapper.getPeriod() % 2629743 == 0)
            periodStr = (bookUIWrapper.getPeriod() / 2629743) + " month(s)";
        else if (bookUIWrapper.getPeriod() % 604800 == 0)
            periodStr = (bookUIWrapper.getPeriod() / 604800) + " week(s)";
        else
            periodStr = (bookUIWrapper.getPeriod() / 86400) + " day(s)";
        periodTextField.setText(periodStr);
        publisherTextField.setText(bookUIWrapper.getPublisher());
        pagesTextField.setText("" + bookUIWrapper.getPages());
        descriptionTextArea.setText(bookUIWrapper.getDescription());
        availabilityLabel.setText(bookUIWrapper.isAvailability() ? "yes" : "no");
        availabilityLabel.setTextFill(bookUIWrapper.isAvailability() ? Color.GREEN : Color.RED);
        dateTextField.setText(bookUIWrapper.getPublishedDate());
        languageTextField.setText(bookUIWrapper.getLanguage());
    }
    
    @FXML
    private void reserve() {
        final String bookObjId = bookUIWrapper.getObjectId();
        new Thread(() -> {
            Future<String> result = StudentCommands.reserveBook(bookObjId);
            String resultMessage = "";
            try {
                resultMessage = result.get();
                if (resultMessage.equals("success"))
                    UIFactory.showAnnouncement(Alert.AlertType.INFORMATION, "Success",
                        "You have successfully reserve book \"" + bookUIWrapper.getTitle() + "\".");
            } catch (InterruptedException | ExecutionException e) {
                UIFactory.showAnnouncement(Alert.AlertType.ERROR, "Unsuccessful",
                    resultMessage);
            }
        }).start();
    }
    
    @Override
    public void setUpUI() {
    }
    
    @Override
    public Window getWindow() {
        return titleTextField.getScene().getWindow();
    }
    
    @FXML
    private void cancel() {
        getWindow().hide();
    }
}
