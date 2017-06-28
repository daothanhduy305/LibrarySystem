package ebolo.libma.client.ui.controllers;

import ebolo.libma.authenticate.ui.fxml.FxmlManager;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.commons.ui.utils.ControllerWrapper;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import ebolo.libma.data.db.local.BookListManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Controller (for UI) of book table view tab
 *
 * @author Ebolo
 * @version 27/06/2017
 * @since 09/06/2017
 */

public class BooksViewController implements Controller {
    private static BooksViewController ourInstance;
    private Stage bookInfoWindow;
    
    @FXML
    private TableView<BookUIWrapper> booksTableView;
    @FXML
    private TableColumn<BookUIWrapper, String> titleColumn, authorColumn, publisherColumn, yearColumn,
        categoriesColumn, languageColumn;
    @FXML
    private TableColumn<BookUIWrapper, Long> periodColumn;
    @FXML
    private TableColumn<BookUIWrapper, Integer> pagesColumn;
    @FXML
    private TableColumn<BookUIWrapper, Boolean> availabilityColumn;
    
    
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
        // Set up UI for the table view
        setUpTableUI();
    }
    
    @Override
    public Window getWindow() {
        return booksTableView.getScene().getWindow();
    }
    
    @SuppressWarnings("unchecked")
    private void setUpTableUI() {
        // Set up general table UI
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
}
