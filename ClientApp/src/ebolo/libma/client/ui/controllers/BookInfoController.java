package ebolo.libma.client.ui.controllers;

import ebolo.libma.client.commander.StudentCommandFactory;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import ebolo.libma.data.db.local.BookListManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
    
        titleTextField.textProperty().bind(bookUIWrapper.titleProperty());
        authorsTextField.textProperty().bind(bookUIWrapper.authorProperty());
        categoriesTextField.textProperty().bind(bookUIWrapper.categoriesProperty());
        periodTextField.textProperty().bind(bookUIWrapper.periodStrProperty());
        publisherTextField.textProperty().bind(bookUIWrapper.publisherProperty());
        pagesTextField.textProperty().bind(Bindings.convert(bookUIWrapper.pagesProperty()));
        descriptionTextArea.textProperty().bind(bookUIWrapper.descriptionProperty());
        availabilityLabel.textProperty().bind(bookUIWrapper.availabilityStrProperty());
        availabilityLabel.setTextFill(bookUIWrapper.isAvailability() ? Color.GREEN : Color.RED);
        availabilityLabel.textProperty().addListener((observable, oldValue, newValue) ->
            availabilityLabel.setTextFill(bookUIWrapper.isAvailability() ? Color.GREEN : Color.RED));
        dateTextField.textProperty().bind(bookUIWrapper.publishedDateProperty());
        languageTextField.textProperty().bind(bookUIWrapper.languageProperty());
    }
    
    @FXML
    private void reserve() {
        final String bookObjId = bookUIWrapper.getObjectId();
        new Thread(() -> {
            Future<String> result = StudentCommandFactory.getInstance().run(
                "student.reserve_book", bookObjId);
            String resultMessage = "";
            try {
                resultMessage = result.get();
                if (resultMessage.equals("success")) {
                    Platform.runLater(() -> {
                        try {
                            setBookUIWrapper(
                                BookListManager.getInstance().getUiList()
                                    .filtered(bookUIWrapper1 -> bookUIWrapper1.getObjectId().equals(bookObjId))
                                    .get(0)
                            );
                        } catch (Exception ignored) {
                        }
                    });
                    UIFactory.showAnnouncement(Alert.AlertType.INFORMATION, "Success",
                        "You have successfully reserve book \"" + bookUIWrapper.getTitle() + "\".");
                }
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
