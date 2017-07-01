package ebolo.libma.management.ui.controllers;

import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.commons.utils.Time;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.data.data.raw.book.utils.BookLanguage;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import ebolo.libma.data.data.utils.exceptions.WrongISBN;
import ebolo.libma.management.commander.CentralCommandFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Controller of the modifying book window
 *
 * @author Ebolo
 * @version 15/06/2017
 * @since 15/06/2017
 */

public class ModifyBookController implements Controller {
    private static ModifyBookController ourInstance;
    private BookUIWrapper bookUIWrapper;
    
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<BookLanguage.LANG> langComboBox;
    @FXML
    private ComboBox<Integer> periodUnitComboBox;
    @FXML
    private TextField titleTextField, authorsTextField, categoriesTextField, periodTextField, isbn10TextField,
        isbn13TextField, publisherTextField, pagesTextField, totalUnitTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private CheckBox availableCheckBox;
    
    private List<TextField> textFields;
    
    private ModifyBookController() {
    }
    
    public static ModifyBookController getInstance() {
        if (ourInstance == null)
            ourInstance = new ModifyBookController();
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
        authorsTextField.setText(bookUIWrapper.getAuthor().replace(", ", "_"));
        categoriesTextField.setText(bookUIWrapper.getCategories().replace(", ", "_"));
        if (bookUIWrapper.getPeriod() % 2629743 == 0)
            periodUnitComboBox.setValue(2629743);
        else if (bookUIWrapper.getPeriod() % 604800 == 0)
            periodUnitComboBox.setValue(604800);
        else
            periodUnitComboBox.setValue(86400);
        periodTextField.setText("" + (bookUIWrapper.getPeriod() / periodUnitComboBox.getValue()));
        isbn10TextField.setText(bookUIWrapper.getIsbn10());
        isbn13TextField.setText(bookUIWrapper.getIsbn13());
        publisherTextField.setText(bookUIWrapper.getPublisher());
        pagesTextField.setText("" + bookUIWrapper.getPages());
        totalUnitTextField.setText("" + bookUIWrapper.getUnitTotal());
        descriptionTextArea.setText(bookUIWrapper.getDescription());
        availableCheckBox.setSelected(bookUIWrapper.isAvailability());
        datePicker.getEditor().setText(bookUIWrapper.getPublishedDate());
        langComboBox.setValue(BookLanguage.getLang(bookUIWrapper.getLanguage()));
    }
    
    @FXML
    private void save() {
        int total, unitAvailable;
        try {
            // check for input correctness
            total = Integer.parseInt(totalUnitTextField.getText());
            if (total < bookUIWrapper.getUnitTotal() - bookUIWrapper.getUnitAvailable()) {
                UIFactory.showAnnouncement(Alert.AlertType.ERROR, "New value is not acceptable",
                    "Total unit must be greater than borrowing books.");
                return;
            }
            unitAvailable = bookUIWrapper.getUnitAvailable() + (total - bookUIWrapper.getUnitTotal());
        } catch (NumberFormatException e) {
            UIFactory.showAnnouncement(Alert.AlertType.ERROR, "Wrong input format",
                "Total unit input must be integer!");
            return;
        }
        if (!availableCheckBox.isSelected() && (bookUIWrapper.getUnitAvailable() < bookUIWrapper.getUnitTotal())) {
            UIFactory.showAnnouncement(Alert.AlertType.ERROR, "New value is not acceptable",
                "Cannot set book unavailable while there are copies being lent");
            return;
        }
        // retrieve other info from ui components
        boolean available = availableCheckBox.isSelected();
        String titleStr = titleTextField.getText(),
            authorsStr = authorsTextField.getText(),
            categoriesStr = categoriesTextField.getText(),
            periodStr = periodTextField.getText(),
            isbn10Str = isbn10TextField.getText(),
            isbn13Str = isbn13TextField.getText(),
            publisherStr = publisherTextField.getText(),
            pagesStr = pagesTextField.getText(),
            totalUnitStr = totalUnitTextField.getText(),
            dateStr = datePicker.getEditor().getText(),
            langStr = langComboBox.getValue().name(),
            descriptionStr = descriptionTextArea.getText();
        long periodDelta = periodUnitComboBox.getValue().longValue();
        new Thread(() -> {
            // check for input correctness again
            int pages;
            try {
                pages = pagesStr.isEmpty() ? 0 : Integer.parseInt(pagesStr);
            } catch (NumberFormatException e) {
                UIFactory.showAnnouncement(
                    Alert.AlertType.ERROR,
                    "Wrong input format",
                    "Pages input must be integer!"
                );
                return;
            }
            
            int unit;
            try {
                unit = Integer.parseInt(totalUnitStr);
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
                period = Long.parseLong(periodStr) * periodDelta;
            } catch (NumberFormatException e) {
                UIFactory.showAnnouncement(
                    Alert.AlertType.ERROR,
                    "Wrong input format",
                    "Period input must be integer!"
                );
                return;
            }
            try { // run command
                Future<String> result = CentralCommandFactory.getInstance().run(
                    "book.modify_book",
                    new Book(
                        titleStr, authorsStr, publisherStr, dateStr,
                        descriptionStr, categoriesStr,
                        isbn10Str, isbn13Str, langStr,
                        pages, unit, unitAvailable, period, available
                    ),
                    bookUIWrapper
                );
                // show result to user
                String resultMessage = result.get();
                if (!resultMessage.equals("success")) {
                    UIFactory.showAnnouncement(
                        Alert.AlertType.ERROR,
                        resultMessage
                    );
                } else {
                    UIFactory.showAnnouncement(Alert.AlertType.INFORMATION,
                        "Success", "Book has been successfully modified.");
                }
            } catch (WrongISBN | InterruptedException | ExecutionException e) {
                e.printStackTrace();
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
        periodUnitComboBox.setButtonCell(new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(Time.milisToString(item));
                }
            }
        });
        
        textFields = new ArrayList<>(Arrays.asList(titleTextField, authorsTextField, categoriesTextField,
            periodTextField, isbn10TextField, isbn13TextField, publisherTextField, pagesTextField,
            totalUnitTextField)
        );
        descriptionTextArea.setWrapText(true);
        
        datePicker.setEditable(false);
        
        // Set up language box
        langComboBox.setItems(FXCollections.observableArrayList(BookLanguage.LANG.values())
            .sorted(Comparator.comparing(lang -> BookLanguage.getFullLangTerm(lang.name()))));
        langComboBox.setCellFactory(param -> new ListCell<BookLanguage.LANG>() {
            @Override
            protected void updateItem(BookLanguage.LANG item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(BookLanguage.getFullLangTerm(item.name()));
                }
            }
        });
        langComboBox.getSelectionModel().selectFirst();
        langComboBox.setButtonCell(new ListCell<BookLanguage.LANG>() {
            @Override
            protected void updateItem(BookLanguage.LANG item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText(BookLanguage.getFullLangTerm(item.name()));
                }
            }
        });
    }
    
    @Override
    public Window getWindow() {
        return datePicker.getScene().getWindow();
    }
    
    @FXML
    private void cancel() {
        textFields.forEach(TextInputControl::clear);
        descriptionTextArea.clear();
        datePicker.getEditor().clear();
        getWindow().hide();
    }
}
