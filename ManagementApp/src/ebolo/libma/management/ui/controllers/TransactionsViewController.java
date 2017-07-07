package ebolo.libma.management.ui.controllers;

import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.data.data.ui.ObjectUIWrapper;
import ebolo.libma.data.data.ui.transaction.TransactionUIWrapper;
import ebolo.libma.data.db.local.TransactionListManager;
import ebolo.libma.management.commander.CentralCommandFactory;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Window;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Controller of the transaction table view tab
 *
 * @author Ebolo
 * @version 09/06/2017
 * @since 09/06/2017
 */

public class TransactionsViewController implements Controller {
    private static TransactionsViewController ourInstance;
    private ContextMenu contextMenu;
    
    @FXML
    private TableView<TransactionUIWrapper> transactionTableView;
    @FXML
    private TableColumn<TransactionUIWrapper, String> studentColumn, bookColumn;
    @FXML
    private TableColumn<TransactionUIWrapper, Long> tStartColumn, tExpiredColumn;
    @FXML
    private TableColumn<TransactionUIWrapper, Boolean> returnedColumn, expiredColumn;
    
    private TransactionsViewController() {
    }
    
    public static TransactionsViewController getInstance() {
        if (ourInstance == null)
            ourInstance = new TransactionsViewController();
        return ourInstance;
    }
    
    @Override
    public void setUpUI() {
        transactionTableView.setItems(TransactionListManager.getInstance().getUiWrapperSortedList());
    
        studentColumn.setCellValueFactory(param -> param.getValue().studentProperty());
        bookColumn.setCellValueFactory(param -> param.getValue().bookProperty());
    
        tStartColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        tStartColumn.setCellFactory(param -> new TableCell<TransactionUIWrapper, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(!empty ?
                    new Date(item)
                        .toInstant()
                        .atOffset(ZoneOffset.UTC)
                        .format(DateTimeFormatter
                            .ofPattern("yyyy-MM-dd")
                        ) :
                    ""
                );
            }
        });
        tExpiredColumn.setCellValueFactory(new PropertyValueFactory<>("expireTime"));
        tExpiredColumn.setCellFactory(param -> new TableCell<TransactionUIWrapper, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(!empty ?
                    new Date(item)
                        .toInstant()
                        .atOffset(ZoneOffset.UTC)
                        .format(DateTimeFormatter
                            .ofPattern("yyyy-MM-dd")
                        ) :
                    ""
                );
            }
        });
        returnedColumn.setCellValueFactory(new PropertyValueFactory<>("returned"));
        expiredColumn.setCellValueFactory(new PropertyValueFactory<>("expired"));
        TransactionListManager
            .getInstance()
            .getUiWrapperSortedList()
            .comparatorProperty()
            .bind(transactionTableView.comparatorProperty());
        transactionTableView.getSortOrder().setAll(tStartColumn);
        transactionTableView.setOnContextMenuRequested(this::showContextMenu);
    }
    
    private void finishTransactions() {
        Future<String> result = CentralCommandFactory.getInstance().run(
            "transaction.finish_transactions",
            transactionTableView
                .getSelectionModel()
                .getSelectedItems()
                .parallelStream()
                .map(ObjectUIWrapper::getObjectId)
                .collect(Collectors.toList())
        );
    }
    
    private void showContextMenu(ContextMenuEvent event) {
        if (contextMenu == null) {
            MenuItem finish = new MenuItem("Finish");
            finish.disableProperty().bind(
                Bindings.size(TransactionListManager.getInstance().getUiList())
                    .isEqualTo(0)
            );
            
            finish.setOnAction(event1 -> finishTransactions());
            
            contextMenu = new ContextMenu(
                finish
            );
        }
        contextMenu.show(getWindow(), event.getScreenX(), event.getScreenY());
    }
    
    @Override
    public Window getWindow() {
        return transactionTableView.getScene().getWindow();
    }
}
