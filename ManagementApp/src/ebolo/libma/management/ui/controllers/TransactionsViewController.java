package ebolo.libma.management.ui.controllers;

import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.data.data.ui.transaction.TransactionUIWrapper;
import ebolo.libma.data.db.local.TransactionListManager;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;

/**
 * Controller of the transaction table view tab
 *
 * @author Ebolo
 * @version 09/06/2017
 * @since 09/06/2017
 */

public class TransactionsViewController implements Controller {
    private static TransactionsViewController ourInstance;
    
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
        tExpiredColumn.setCellValueFactory(new PropertyValueFactory<>("expireTime"));
        returnedColumn.setCellValueFactory(new PropertyValueFactory<>("returned"));
        expiredColumn.setCellValueFactory(new PropertyValueFactory<>("expired"));
    }
    
    @Override
    public Window getWindow() {
        return null;
    }
}
