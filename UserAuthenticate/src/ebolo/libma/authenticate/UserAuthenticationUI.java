package ebolo.libma.authenticate;

import ebolo.libma.authenticate.ui.controllers.AuthenticationController;
import ebolo.libma.authenticate.ui.fxml.FxmlManager;
import ebolo.libma.commons.net.Stub;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.commons.ui.utils.ControllerWrapper;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main class for user authentication dialog
 *
 * @author Ebolo
 * @version 08/06/2017
 * @since 08/06/2017
 */

public class UserAuthenticationUI {
    public static void showAndAuthenticate(
        Stub stub, MetaInfo.USER_MODE userMode, Runnable onSuccess, Runnable onExit) throws IOException {
        AuthenticationController authenticationUIController = new AuthenticationController(stub, userMode, onSuccess);
        Stage primaryStage = UIFactory.createWindow(
            "Authentication",
            FxmlManager.getInstance().getFxmlTemplate("AuthenticationFXML"),
            null, 300, 100,
            new ControllerWrapper(authenticationUIController.getClass(), authenticationUIController)
        );
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            authenticationUIController.exit();
            onExit.run();
        });
        primaryStage.show();
    }
}
