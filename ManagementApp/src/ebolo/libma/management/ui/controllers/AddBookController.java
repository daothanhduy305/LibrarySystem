package ebolo.libma.management.ui.controllers;

import ebolo.libma.commons.ui.ScreenUtils;
import ebolo.libma.commons.ui.utils.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Controller of the general adding book window
 *
 * @author Ebolo
 * @version 10/06/2017
 * @since 10/06/2017
 */

public class AddBookController implements Controller {
    private static AddBookController ourInstance;
    
    @FXML
    private TitledPane isbnPane, fullInfoPane;
    @FXML
    private Accordion accordion;
    
    private AddBookController() {
    }
    
    public static AddBookController getInstance() {
        if (ourInstance == null)
            ourInstance = new AddBookController();
        return ourInstance;
    }
    
    @Override
    public void setUpUI() {
        final Stage window = (Stage) getWindow();
        window.setMinWidth(ScreenUtils.getScreenWidth() * 0.58);
        window.setMinHeight(ScreenUtils.getScreenHeight() * 0.6);
        
        isbnPane.prefWidthProperty().bind(window.widthProperty());
        isbnPane.minWidthProperty().bind(isbnPane.prefWidthProperty());
        isbnPane.maxWidthProperty().bind(isbnPane.prefWidthProperty());
        
        fullInfoPane.prefWidthProperty().bind(window.widthProperty());
        fullInfoPane.minWidthProperty().bind(fullInfoPane.prefWidthProperty());
        fullInfoPane.maxWidthProperty().bind(fullInfoPane.prefWidthProperty());
        
        AddBookIsbnController.getInstance().setUpUI();
        AddBookFullController.getInstance().setUpUI();
        
        accordion.setExpandedPane(isbnPane);
    }
    
    @Override
    public Window getWindow() {
        return accordion.getScene().getWindow();
    }
    
    public void cancel() {
        AddBookIsbnController.getInstance().cleanUp();
        AddBookFullController.getInstance().cleanUp();
        getWindow().hide();
    }
}
