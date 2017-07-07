package ebolo.libma.management.commander.transaction;

import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.commons.ui.UIFactory;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import javafx.scene.control.Alert;
import org.bson.Document;

import java.util.List;

/**
 * Client's side command for finishing a transaction
 *
 * @author Ebolo
 * @version 06/07/2017
 * @see Command
 * @since 05/07/2017
 */
public class FinishTransactionsCommand extends Command<String> {
    private List<String> finishingTransactions;
    
    @SuppressWarnings("unchecked")
    FinishTransactionsCommand(Object[] args) {
        super(args);
        this.finishingTransactions = (List<String>) args[0];
    }
    
    @Override
    public String call() throws Exception {
        if (CommandUtils.sendCommand(
            MetaInfo.USER_MODE.Librarian,
            StubCommunication.getInstance().getStub(),
            "finish_transactions",
            finishingTransactions)
            ) {
            Document response = StubCommunication.getInstance().getStub().getMessageBuffer().take();
            if (response.getString("message").equals("success")) {
                UIFactory.showAnnouncement(
                    Alert.AlertType.INFORMATION,
                    "Request success",
                    "Successfully finishing " + response.getString("package") +
                        "transaction(s)!"
                );
            } else {
                UIFactory.showAnnouncement(
                    Alert.AlertType.ERROR,
                    "Request failed",
                    response.getString("package")
                );
            }
        }
        return null;
    }
}
