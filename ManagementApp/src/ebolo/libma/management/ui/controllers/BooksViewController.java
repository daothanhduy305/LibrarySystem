package ebolo.libma.management.ui.controllers;

import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.data.data.ui.book.BookUIWrapper;
import ebolo.libma.data.db.local.BookListManager;
import ebolo.libma.management.commander.CentralCommandFactory;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Window;

import java.io.IOException;
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
    
    @FXML
    private TableView<BookUIWrapper> booksTableView;
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
    
    public void addNewBook() throws IOException {
        AddBookController.getInstance().show();
    }
    
    @SuppressWarnings("Duplicates")
    private void deleteBooks() {
        List<BookUIWrapper> deletingBooks = booksTableView
            .getSelectionModel()
            .getSelectedItems();
        new Thread(() -> {
            Future<String> result = CentralCommandFactory.getInstance().run(
                "book.delete_books",
                deletingBooks
            );
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
        ModifyBookController.getInstance().show(
            booksTableView
                .getSelectionModel()
                .getSelectedItems().get(0)
        );
    }
    
    @SuppressWarnings("Duplicates, unchecked")
    @Override
    public void setUpUI() {
        // Set up general table UI
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
    
    @Override
    public Window getWindow() {
        return booksTableView.getScene().getWindow();
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
