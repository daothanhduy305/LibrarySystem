package ebolo.libma.management.ui.controllers;

import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.commons.utils.Time;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.data.data.utils.exceptions.BookNotFound;
import ebolo.libma.data.data.utils.exceptions.WrongISBN;
import ebolo.libma.management.commander.CentralCommandFactory;
import ebolo.libma.management.utils.configs.AppConfigurations;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Controller of the adding book with ISBN info section
 *
 * @author Ebolo
 * @version 10/06/2017
 * @since 10/06/2017
 */

public class AddBookIsbnController implements Controller {
    private static AddBookIsbnController ourInstance;
    
    @FXML
    private TextField isbnTextField, unitTextField, periodTextField;
    @FXML
    private Button addButton;
    @FXML
    private ComboBox<Integer> periodUnitComboBox;
    @FXML
    private CheckBox availCheckBox;
    
    private AddBookIsbnController() {
    }
    
    public static AddBookIsbnController getInstance() {
        if (ourInstance == null)
            ourInstance = new AddBookIsbnController();
        return ourInstance;
    }
    
    @SuppressWarnings("Duplicates")
    @FXML
    private void addNewBook() {
        // retrieve info from UI components
        final String unitStr = unitTextField.getText(),
            periodStr = periodTextField.getText(),
            isbnStr = isbnTextField.getText();
        final boolean available = availCheckBox.isSelected();
        new Thread(() -> {
            try {
                // check input correctness
                int unit;
                try {
                    unit = Integer.parseInt(unitStr);
                } catch (NumberFormatException e) {
                    UIFactory.showAnnouncement(
                        Alert.AlertType.ERROR,
                        "Wrong input format",
                        "Unit input must be integer!"
                    );
                    return;
                }
                
                long period;
                try {
                    period = Long.parseLong(periodStr)
                        * periodUnitComboBox.getValue().longValue();
                } catch (NumberFormatException e) {
                    UIFactory.showAnnouncement(
                        Alert.AlertType.ERROR,
                        "Wrong input format",
                        "Period input must be integer!"
                    );
                    return;
                }
                // start command
                Future<String> result = CentralCommandFactory.getInstance().run(
                    "book.add_book",
                    new Book(isbnStr, unit, period, available)
                );
                // show result to user
                final String resultMes = result.get();
                if (!resultMes.equals("success"))
                    UIFactory.showAnnouncement(
                        Alert.AlertType.ERROR,
                        resultMes
                    );
                else if (AppConfigurations.getInstance().isCloseOnAdded())
                    Platform.runLater(() -> AddBookController.getInstance().cancel());
                else Platform.runLater(this::cleanUp);
            } catch (WrongISBN e) {
                UIFactory.showAnnouncement(
                    Alert.AlertType.ERROR,
                    "Wrong ISBN!"
                );
            } catch (BookNotFound bookNotFound) {
                UIFactory.showAnnouncement(
                    Alert.AlertType.ERROR,
                    "Book not found!",
                    "Book information is missing from Google database"
                );
            } catch (IOException e) {
                e.printStackTrace();
                UIFactory.showAnnouncement(
                    Alert.AlertType.ERROR,
                    "Unable to get online information for this book!"
                );
            } catch (ExecutionException | InterruptedException e) {
                UIFactory.showAnnouncement(
                    Alert.AlertType.ERROR,
                    "Internal error!"
                );
            }
        }).start();
    }
    
    @Override
    public void setUpUI() {
        periodUnitComboBox.setItems(FXCollections.observableArrayList(86400, 604800, 2629743));
        periodUnitComboBox.setCellFactory(param -> new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(Time.milisToString(item));
                }
            }
        });
        periodUnitComboBox.getSelectionModel().selectFirst();
        periodUnitComboBox.setButtonCell(new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(Time.milisToString(item));
                }
            }
        });
        addButton.disableProperty().bind(
            isbnTextField.textProperty().isEmpty().or(
                periodTextField.textProperty().isEmpty().or(
                    unitTextField.textProperty().isEmpty()
                )
            )
        );
        
        availCheckBox.setSelected(true);
    }
    
    @Override
    public Window getWindow() {
        return null;
    }
    
    void cleanUp() {
        isbnTextField.clear();
        unitTextField.clear();
        periodTextField.clear();
    }
    
    @FXML
    private void cancel() {
        AddBookController.getInstance().cancel();
    }
}
