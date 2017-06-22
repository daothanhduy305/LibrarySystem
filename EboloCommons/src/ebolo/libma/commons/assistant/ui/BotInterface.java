package ebolo.libma.commons.assistant.ui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Class acts as the bridge between bot interface and internal classes
 *
 * @author Ebolo
 * @version 19/06/2017
 * @since 19/06/2017
 */

public class BotInterface {
    private static BotInterface ourInstance;
    private TextArea botTextArea;
    
    private BotInterface() {
    }
    
    public static BotInterface getInstance() {
        if (ourInstance == null)
            ourInstance = new BotInterface();
        return ourInstance;
    }
    
    public void setBotTextArea(TextArea botTextArea) {
        this.botTextArea = botTextArea;
    }
    
    public void addText(String user, String message) {
        Platform.runLater(() -> botTextArea.appendText(user + ": " + message + '\n'));
    }
}
