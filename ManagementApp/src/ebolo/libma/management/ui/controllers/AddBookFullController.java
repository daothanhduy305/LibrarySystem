package ebolo.libma.management.ui.controllers;

import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.commons.utils.Time;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.data.data.raw.book.utils.BookLanguage;
import ebolo.libma.data.data.utils.exceptions.WrongISBN;
import ebolo.libma.management.commander.BookCommands;
import ebolo.libma.management.utils.configs.AppConfigurations;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Controller of the adding book with full info section
 *
 * @author Ebolo
 * @version 10/06/2017
 * @since 10/06/2017
 */

public class AddBookFullController implements Controller {
    private static AddBookFullController ourInstance;
    
    @FXML
    private Button addButton;
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
    
    private AddBookFullController() {
    }
    
    public static AddBookFullController getInstance() {
        if (ourInstance == null)
            ourInstance = new AddBookFullController();
        return ourInstance;
    }
    
    @SuppressWarnings("Duplicates")
    @FXML
    private void addNewBook() {
        // retrieve info from UI components
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
        boolean available = availableCheckBox.isSelected();
        long periodDelta = periodUnitComboBox.getValue().longValue();
        
        new Thread(() -> {
            try {
                // check input correctness
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
                // start command
                Future<String> result = BookCommands.addNewBook(new Book(
                    titleStr, authorsStr, publisherStr, dateStr,
                    descriptionStr, categoriesStr,
                    isbn10Str, isbn13Str, langStr,
                    pages, unit, unit, period, available
                ));
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
        
        // Set up date picker
        String pattern = "yyyy-MM-dd";
        datePicker.setPromptText(pattern.toLowerCase());
        
        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
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
        
        // set up period box
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
        
        // set up add button
        addButton.disableProperty().bind(
            titleTextField.textProperty().isEmpty().or(
                authorsTextField.textProperty().isEmpty().or(
                    periodTextField.textProperty().isEmpty().or(
                        totalUnitTextField.textProperty().isEmpty()
                    )
                )
            )
        );
        
        availableCheckBox.setSelected(true);
        
        textFields = new ArrayList<>(Arrays.asList(titleTextField, authorsTextField, categoriesTextField,
            periodTextField, isbn10TextField, isbn13TextField, publisherTextField, pagesTextField,
            totalUnitTextField)
        );
    }
    
    @Override
    public Window getWindow() {
        return descriptionTextArea.getScene().getWindow();
    }
    
    void cleanUp() {
        textFields.forEach(TextInputControl::clear);
        descriptionTextArea.clear();
        datePicker.getEditor().clear();
    }
    
    @FXML
    private void cancel() {
        AddBookController.getInstance().cancel();
    }
}
