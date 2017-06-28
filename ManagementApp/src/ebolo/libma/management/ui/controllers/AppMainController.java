package ebolo.libma.management.ui.controllers;

import ebolo.libma.assistant.Alias;
import ebolo.libma.assistant.ui.BotInterface;
import ebolo.libma.commons.ui.ScreenUtils;
import ebolo.libma.commons.ui.ViewStatus;
import ebolo.libma.commons.ui.utils.Controller;
import ebolo.libma.data.db.local.BookListManager;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Window;

import java.util.Arrays;

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
    private Label statusLabel;
    @FXML
    private TabPane mainPane;
    @FXML
    private GridPane mHolder;
    @FXML
    private TextFlow botInterface;
    @FXML
    private Text botStatus;
    @FXML
    private ScrollPane botScrollPane;
    @FXML
    private TextField chatTextField;
    @FXML
    private VBox sideBar;
    @FXML
    private TextField searchBox;
    
    private AppMainController() {
    }
    
    public static AppMainController getInstance() {
        if (ourInstance == null)
            ourInstance = new AppMainController();
        return ourInstance;
    }
    
    @Override
    public void setUpUI() {
        // Set up UI for side bar
        sideBar.setPrefWidth(ScreenUtils.getScreenWidth() * 0.2);
        sideBar.setMinWidth(sideBar.getPrefWidth());
        sideBar.setMaxWidth(sideBar.getPrefWidth());
    
        // Set up size for the main Pane
        mainPane.prefWidthProperty().bind(mHolder
            .widthProperty()
            .subtract(sideBar.getPrefWidth())
        );
        mainPane.minWidthProperty().bind(mainPane.prefWidthProperty());
        mainPane.maxWidthProperty().bind(mainPane.prefWidthProperty());
    
        ViewStatus.getInstance().setStatusLabel(statusLabel);
    
        // Set up Alias interface
        BotInterface.getInstance().setBotInterface(botInterface, botStatus);
        botInterface.getChildren().addListener(
            (ListChangeListener<Node>) ((change) -> {
                botInterface.layout();
                botScrollPane.layout();
                botScrollPane.setVvalue(1.0f);
            })
        );
    
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
        
        // Set up UI for the children
        BooksViewController.getInstance().setUpUI();
        StudentsViewController.getInstance().setUpUI();
        TransactionsViewController.getInstance().setUpUI();
    }
    
    @FXML
    private void chat() {
        // TODO: replace with username
        String speech = chatTextField.getText();
        BotInterface.getInstance().addText("User", speech);
        chatTextField.clear();
        Alias.getInstance().saySomething(speech);
    }
    
    @Override
    public Window getWindow() {
        return mainPane.getScene().getWindow();
    }
}
