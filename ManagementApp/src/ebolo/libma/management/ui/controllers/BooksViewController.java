package ebolo.libma.management.ui.controllers;

import ebolo.libma.authenticate.ui.fxml.FxmlManager;
import ebolo.libma.commons.ui.ScreenUtils;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.commons.ui.utils.ControllerWrapper;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import ebolo.libma.data.db.local.BookListManager;
import ebolo.libma.management.commander.BookCommands;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Controller of the book table view tab
 *
 * @author Ebolo
 * @version 09/06/2017
 * @since 09/06/2017
 */

public class BooksViewController implements Controller {
    private static BooksViewController ourInstance;
    private Stage addNewBookWindow, modifyBookWindow;
    
    @FXML
    private TextField searchBox;
    @FXML
    private TableView<BookUIWrapper> booksTableView;
    @FXML
    private VBox sideBar;
    @FXML
    private TableColumn<BookUIWrapper, String> titleColumn, authorColumn, publisherColumn, yearColumn, isbn10Column,
        isbn13Column, categoriesColumn, languageColumn;
    @FXML
    private TableColumn<BookUIWrapper, Long> periodColumn;
    @FXML
    private TableColumn<BookUIWrapper, Integer> pagesColumn, totalColumn, availableColumn;
    @FXML
    private TableColumn<BookUIWrapper, Boolean> availabilityColumn;
    
    private ContextMenu contextMenu;
    
    private BooksViewController() {
    }
    
    public static BooksViewController getInstance() {
        if (ourInstance == null)
            ourInstance = new BooksViewController();
        return ourInstance;
    }
    
    private void addNewBook() throws IOException {
        if (addNewBookWindow == null) {
            AddBookController addBookController = AddBookController.getInstance();
            AddBookIsbnController addBookIsbnController = AddBookIsbnController.getInstance();
            AddBookFullController addBookFullController = AddBookFullController.getInstance();
            
            addNewBookWindow = UIFactory.createWindow(
                "Add new book",
                FxmlManager.getInstance().getFxmlTemplate("AddBookFXML"),
                AppMainController.getInstance().getWindow(),
                ScreenUtils.getScreenWidth() * 0.6,
                ScreenUtils.getScreenHeight() * 0.6,
                new ControllerWrapper(addBookController.getClass(), addBookController),
                new ControllerWrapper(addBookIsbnController.getClass(), addBookIsbnController),
                new ControllerWrapper(addBookFullController.getClass(), addBookFullController)
            );
            
            addBookController.setUpUI();
            addNewBookWindow.setOnCloseRequest(event -> AddBookController.getInstance().cancel());
        }
        addNewBookWindow.show();
    }
    
    @SuppressWarnings("Duplicates")
    private void deleteBooks() {
        List<BookUIWrapper> deletingBooks = booksTableView
            .getSelectionModel()
            .getSelectedItems();
        new Thread(() -> {
            Future<String> result = BookCommands.removeBook(deletingBooks);
            try {
                String resultMessage = result.get();
                if (!resultMessage.equals("success")) {
                    UIFactory.showAnnouncement(
                        Alert.AlertType.ERROR,
                        resultMessage
                    );
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void modifyBook() throws IOException {
        BookUIWrapper modifyingBook = booksTableView
            .getSelectionModel()
            .getSelectedItems().get(0);
        if (modifyBookWindow == null) {
            EditBookController editBookController = EditBookController.getInstance();
            modifyBookWindow = UIFactory.createWindow(
                "Modifying " + modifyingBook.getTitle(),
                FxmlManager.getInstance().getFxmlTemplate("EditBookFXML"),
                null, 0, 0,
                new ControllerWrapper(editBookController.getClass(), editBookController)
            );
            editBookController.setUpUI();
        }
        EditBookController.getInstance().setBookUIWrapper(modifyingBook);
        modifyBookWindow.show();
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
        isbn10Column.setCellValueFactory(param -> param.getValue().isbn10Property());
        isbn13Column.setCellValueFactory(param -> param.getValue().isbn13Property());
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
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("unitTotal"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("unitAvailable"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
        availabilityColumn.setCellFactory(param -> new TableCell<BookUIWrapper, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item ? "Yes" : "No");
            }
        });
        
        // Set up context menus
        booksTableView.setOnContextMenuRequested(this::showContextMenu);
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
                            bookUIWrapper.getDescription().toLowerCase().contains(s) ||
                            bookUIWrapper.getIsbn10().toLowerCase().contains(s) ||
                            bookUIWrapper.getIsbn13().toLowerCase().contains(s)
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
                    try {
                        modifyBook();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
    }
    
    private void showContextMenu(ContextMenuEvent event) {
        if (contextMenu == null) {
            MenuItem addNew = new MenuItem("Add new...");
            MenuItem delete = new MenuItem("Delete");
            MenuItem modify = new MenuItem("Edit...");
            delete.disableProperty().bind(
                Bindings.size(BookListManager.getInstance().getUiList())
                    .isEqualTo(0)
            );
            modify.disableProperty().bind(
                Bindings.size(BookListManager.getInstance().getUiList()).isEqualTo(0));
            
            addNew.setOnAction(event1 -> {
                try {
                    addNewBook();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    
            delete.setOnAction(event1 -> deleteBooks());
            
            modify.setOnAction(event1 -> {
                try {
                    modifyBook();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            
            contextMenu = new ContextMenu(
                addNew,
                modify,
                delete
            );
        }
        contextMenu.show(getWindow(), event.getScreenX(), event.getScreenY());
    }
}
