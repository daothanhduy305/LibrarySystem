package ebolo.libma.commons.ui;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import ebolo.libma.commons.ui.utils.ControllerWrapper;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class conveniently produces various UI components in needs
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class UIFactory {
    /**
     * Method used to produce a <code>Stage</code> conveniently
     *
     * @param name        window title
     * @param fxmlFileURL url of the fxml ui template
     * @param owner       indicates parent window (can be empty)
     * @param width       desired width
     * @param height      desired height
     * @param controllers set of controllers, used to feed into <code>ControllerFactory</code>
     * @return desired window
     * @throws IOException when the fxml url is not retrievable
     */
    public static Stage createWindow(String name, URL fxmlFileURL, @Nullable Window owner,
                                     double width, double height, @NotNull ControllerWrapper... controllers)
        throws IOException {
        Map<Class, Object> controllersMap = new HashMap<>();
        Arrays.asList(controllers).forEach(
            controllerWrapper -> controllersMap.put(
                controllerWrapper.getControllerClass(),
                controllerWrapper.getController()
            )
        );
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(fxmlFileURL);
        loader.setControllerFactory(controllersMap::get);
        Parent root = loader.load();
        Scene scene = width == 0 || height == 0 ? new Scene(root) : new Scene(root, width, height);
        Stage stage = new Stage();
        if (owner != null)
            stage.initOwner(owner);
        stage.setScene(scene);
        stage.setTitle(name);
        return stage;
    }
    
    /**
     * Helper method used to create various alert windows
     *
     * @param alertType      {@link Alert.AlertType}
     * @param header         alert title
     * @param customMessages alert message
     */
    public static void showAnnouncement(Alert.AlertType alertType,
                                        String header, String... customMessages) {
        StringBuilder errorBuilder = new StringBuilder("");
        if (customMessages.length > 0) {
            for (String message : customMessages) {
                errorBuilder.append(message).append('\n');
            }
        }
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(
                alertType == Alert.AlertType.ERROR ? "Error" :
                    alertType == Alert.AlertType.WARNING ? "Warning" :
                        alertType == Alert.AlertType.INFORMATION ? "Information" : ""
            );
            alert.setHeaderText(header);
            alert.setContentText(errorBuilder.toString());
            alert.showAndWait();
        });
    }
    
}
