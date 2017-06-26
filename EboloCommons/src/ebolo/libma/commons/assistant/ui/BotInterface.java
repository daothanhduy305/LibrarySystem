package ebolo.libma.commons.assistant.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Class acts as the bridge between bot interface and internal classes
 *
 * @author Ebolo
 * @version 19/06/2017
 * @since 19/06/2017
 */

public class BotInterface {
    private static BotInterface ourInstance;
    private StringProperty aliasStatus;
    private ObservableList<Node> botInterface;
    
    private BotInterface() {
        aliasStatus = new SimpleStringProperty("");
        aliasStatus.addListener(
            (observable, oldValue, newValue) -> {
                botInterface.remove(botInterface.size() - 1);
                botInterface.add(new Text(newValue));
            }
        );
    }
    
    public static BotInterface getInstance() {
        if (ourInstance == null)
            ourInstance = new BotInterface();
        return ourInstance;
    }
    
    public void setBotInterface(TextFlow botInterface) {
        this.botInterface = botInterface.getChildren();
        this.botInterface.add(new Text(""));
    }
    
    public void addText(String user, String message) {
        Platform.runLater(() -> {
            Text userName = new Text(user + ": ");
            userName.setStyle("-fx-font-weight: bold");
            Text content = new Text(message + "\n");
            botInterface.add(botInterface.size() - 1, userName);
            botInterface.add(botInterface.size() - 1, content);
        });
    }
    
    public void setAliasStatus(String status) {
        Platform.runLater(() -> aliasStatus.set(status.isEmpty() ? "" : "Alias is " + status));
    }
}
