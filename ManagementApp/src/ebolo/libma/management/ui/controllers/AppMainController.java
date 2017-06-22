package ebolo.libma.management.ui.controllers;

import ebolo.libma.commons.ui.ViewStatus;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.commons.ui.utils.WindowMonitor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.stage.Window;

/**
 * Controller of the main application window
 *
 * @author Ebolo
 * @version 10/06/2017
 * @since 10/06/2017
 */

public class AppMainController implements Controller {
    private static AppMainController ourInstance;
    
    @FXML
    private TabPane mainPane;
    @FXML
    private Label statusLabel;
    
    private AppMainController() {
    }
    
    public static AppMainController getInstance() {
        if (ourInstance == null)
            ourInstance = new AppMainController();
        return ourInstance;
    }
    
    @Override
    public void setUpUI() {
        
        // Set up size for the main Pane
        mainPane.prefWidthProperty().bind(
            WindowMonitor.getInstance().getMonitoringWindow()
                .widthProperty()
        );
        mainPane.minWidthProperty().bind(mainPane.prefWidthProperty());
        mainPane.maxWidthProperty().bind(mainPane.prefWidthProperty());
        
        ViewStatus.getInstance().setStatusLabel(statusLabel);
        
        // Set up UI for the children
        BooksViewController.getInstance().setUpUI();
        StudentsViewController.getInstance().setUpUI();
        TransactionsViewController.getInstance().setUpUI();
    }
    
    TabPane getMainPane() {
        return mainPane;
    }
    
    @Override
    public Window getWindow() {
        return mainPane.getScene().getWindow();
    }
}
