package ebolo.libma.commons.ui;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * This class acts as a monitor for the status label of the UI application
 *
 * @author Ebolo
 * @version 20/06/2017
 * @since 20/06/2017
 */

public class ViewStatus {
    private static ViewStatus ourInstance;
    /**
     * front-end status label to be manipulated
     */
    private Label statusLabel;
    
    private ViewStatus() {
    }
    
    public static ViewStatus getInstance() {
        if (ourInstance == null)
            ourInstance = new ViewStatus();
        return ourInstance;
    }
    
    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }
    
    public void updateStatus(String text) {
        Platform.runLater(() -> statusLabel.setText(text));
    }
}
