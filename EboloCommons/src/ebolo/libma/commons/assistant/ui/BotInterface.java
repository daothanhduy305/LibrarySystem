package ebolo.libma.commons.assistant.ui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Class acts as the bridge between bot interface and internal classes
 *
 * @author Ebolo
 * @version 27/06/2017
 * @since 19/06/2017
 */

public class BotInterface {
    private static BotInterface ourInstance;
    private ObservableList<Node> botInterface;
    private Text botStatus;
    
    private BotInterface() {
    }
    
    public static BotInterface getInstance() {
        if (ourInstance == null)
            ourInstance = new BotInterface();
        return ourInstance;
    }
    
    /**
     * Setup bot interface with required UI components
     *
     * @param botInterface is where the main conversation is going on
     * @param botStatus    is where Alias would update its status to
     */
    public void setBotInterface(TextFlow botInterface, Text botStatus) {
        this.botInterface = botInterface.getChildren();
        this.botStatus = botStatus;
    }
    
    /**
     * Concatenate message to the conversation
     * @param user Alias or ?
     * @param message message content
     */
    public void addText(String user, String message) {
        Platform.runLater(() -> {
            Text userName = new Text((botInterface.size() == 0 ? user : "\n" + user) + ": ");
            userName.setStyle("-fx-font-weight: bold");
            Text content = new Text(message);
            botInterface.add(userName);
            botInterface.add(content);
        });
    }
    
    public void setAliasStatus(String status) {
        Platform.runLater(() -> botStatus.setText(status.isEmpty() ? "" : "Alias is " + status));
    }
}
