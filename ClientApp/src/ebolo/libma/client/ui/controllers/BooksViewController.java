package ebolo.libma.client.ui.controllers;

import ebolo.libma.authenticate.ui.fxml.FxmlManager;
import ebolo.libma.commons.assistant.proc.Alias;
import ebolo.libma.commons.assistant.ui.BotInterface;
import ebolo.libma.commons.ui.ScreenUtils;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.commons.ui.utils.ControllerWrapper;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import ebolo.libma.data.db.local.BookListManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Arrays;

/**
 * Controller (for UI) of book table view tab
 *
 * @author Ebolo
 * @version 09/06/2017
 * @since 09/06/2017
 */

public class BooksViewController implements Controller {
    private static BooksViewController ourInstance;
    private Stage bookInfoWindow;
    
    @FXML
    private TextField searchBox;
    @FXML
    private TableView<BookUIWrapper> booksTableView;
    @FXML
    private VBox sideBar;
    @FXML
    private TableColumn<BookUIWrapper, String> titleColumn, authorColumn, publisherColumn, yearColumn,
        categoriesColumn, languageColumn;
    @FXML
    private TableColumn<BookUIWrapper, Long> periodColumn;
    @FXML
    private TableColumn<BookUIWrapper, Integer> pagesColumn;
    @FXML
    private TableColumn<BookUIWrapper, Boolean> availabilityColumn;
    
    @FXML
    private TextArea botInterface;
    @FXML
    private TextField chatTextField;
    
    private BooksViewController() {
    }
    
    public static BooksViewController getInstance() {
        if (ourInstance == null)
            ourInstance = new BooksViewController();
        return ourInstance;
    }
    
    @SuppressWarnings("Duplicates")
    @Override
    public void setUpUI() {
        // Set up UI for side bar
        sideBar.setPrefWidth(ScreenUtils.getScreenWidth() * 0.15);
        sideBar.setMinWidth(sideBar.getPrefWidth());
        sideBar.setMaxWidth(sideBar.getPrefWidth());
        
        // Set up UI for the table view
        setUpTableUI();
        
        // Set up Alias interface
        BotInterface.getInstance().setBotTextArea(botInterface);
    }
    
    @Override
    public Window getWindow() {
        return booksTableView.getScene().getWindow();
    }
    
    @SuppressWarnings("unchecked")
    private void setUpTableUI() {
        // Set up general table UI
        booksTableView.prefWidthProperty().bind(
            AppMainController.getInstance().getMainPane()
                .widthProperty()
                .subtract(sideBar.getPrefWidth())
        );
        booksTableView.minWidthProperty().bind(booksTableView.prefWidthProperty());
        booksTableView.maxWidthProperty().bind(booksTableView.prefWidthProperty());
        
        booksTableView.setItems(BookListManager.getInstance().getUiWrapperSortedList());
        BookListManager
            .getInstance()
            .getUiWrapperSortedList()
            .comparatorProperty()
            .bind(booksTableView.comparatorProperty());
        
        booksTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Set up columns
        titleColumn.setCellValueFactory(param -> param.getValue().titleProperty());
        authorColumn.setCellValueFactory(param -> param.getValue().authorProperty());
        publisherColumn.setCellValueFactory(param -> param.getValue().publisherProperty());
        yearColumn.setCellValueFactory(param -> param.getValue().publishedDateProperty());
        categoriesColumn.setCellValueFactory(param -> param.getValue().categoriesProperty());
        languageColumn.setCellValueFactory(param -> param.getValue().languageProperty());
        
        periodColumn.setCellValueFactory(new PropertyValueFactory<>("period"));
        periodColumn.setCellFactory(param -> new TableCell<BookUIWrapper, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    String text;
                    if (item % 2629743 == 0)
                        text = "" + (item / 2629743) + " month(s)";
                    else if (item % 604800 == 0)
                        text = "" + (item / 604800) + " week(s)";
                    else
                        text = "" + (item / 86400) + " day(s)";
                    setText(text);
                } else
                    setText("");
            }
        });
        pagesColumn.setCellValueFactory(new PropertyValueFactory<>("pages"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
        availabilityColumn.setCellFactory(param -> new TableCell<BookUIWrapper, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item ? "Yes" : "No");
            }
        });
        
        booksTableView.getSortOrder().setAll(titleColumn);
        
        searchBox.textProperty().addListener((observable, oldValue, newValue) ->
            BookListManager.getInstance().getUiWrapperFilteredList().setPredicate(
                bookUIWrapper -> {
                    boolean matched = true;
                    for (String s : Arrays.asList(newValue.split(" "))) {
                        s = s.toLowerCase();
                        matched &= (s.isEmpty() ||
                            bookUIWrapper.getTitle().toLowerCase().contains(s) ||
                            bookUIWrapper.getAuthor().toLowerCase().contains(s) ||
                            bookUIWrapper.getCategories().toLowerCase().contains(s) ||
                            bookUIWrapper.getPublishedDate().contains(s) ||
                            bookUIWrapper.getLanguage().toLowerCase().contains(s) ||
                            bookUIWrapper.getPublisher().toLowerCase().contains(s) ||
                            bookUIWrapper.getDescription().toLowerCase().contains(s)
                        );
                    }
                    return matched;
                }
            )
        );
        
        booksTableView.setRowFactory(param -> {
            TableRow<BookUIWrapper> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    if (bookInfoWindow == null) {
                        try {
                            BookInfoController bookInfoController = BookInfoController.getInstance();
                            bookInfoWindow = UIFactory.createWindow(
                                row.getItem().getTitle(),
                                FxmlManager.getInstance().getFxmlTemplate("BookInfoFXML"),
                                AppMainController.getInstance().getWindow(), 0, 0,
                                new ControllerWrapper(bookInfoController.getClass(), bookInfoController)
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    BookInfoController.getInstance().setBookUIWrapper(row.getItem());
                    bookInfoWindow.show();
                }
            });
            return row;
        });
    }
    
    @FXML
    private void chat() {
        BotInterface.getInstance().addText("Student", chatTextField.getText());
        Alias.getInstance().saySomething(chatTextField.getText());
        chatTextField.clear();
    }
}
