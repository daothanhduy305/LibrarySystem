package ebolo.libma.stub.utils.books.adder;

/**
 * An utility used to add books in batch from a text file of ISBN(s). This should only be used to add random samples into
 * database.
 *
 * @author Ebolo
 * @version 14/06/2017
 * @see ebolo.libma.stub.utils.books.adder.AdderController
 * @since 14/06/2017
 */

import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.ControllerWrapper;
import ebolo.libma.stub.db.DbPortal;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class AdderMain extends Application {
    
    public static void main(String[] args) {
        DbPortal.getInstance().setUp();
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        AdderController adderController = AdderController.getInstance();
        
        primaryStage = UIFactory.createWindow(
            "Book adder",
            getClass().getResource("View.fxml"),
            null, 0, 0,
            new ControllerWrapper(adderController.getClass(), adderController)
        );
        
        primaryStage.show();
    }
}
