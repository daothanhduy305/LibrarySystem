package ebolo.libma.assistant.commander.common;

import ebolo.libma.assistant.ui.BotInterface;
import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.commands.command.Command;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.db.local.BookListManager;
import javafx.application.Platform;
import org.bson.Document;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Alias's side command for requesting book search from database based
 * on certain information from user
 *
 * @author Ebolo
 * @version 28/06/2017
 * @see Command
 * @since 28/06/2017
 */

public class SearchBookCommand extends Command<String> {
    private Document response;
    
    SearchBookCommand(Object[] args) {
        super(args);
        response = (Document) args[0];
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String call() throws Exception {
        try {
            String keyword = ((List<Document>) response.get("keyword"))
                .stream()
                .map(document -> document.getString("value"))
                .collect(Collectors.joining(" "));
            
            CommandUtils.sendCommand(
                MetaInfo.USER_MODE.Alias,
                StubCommunication.getInstance().getStub(),
                "search",
                keyword
            );
            Document returnMessage = StubCommunication.getInstance().getStub().getMessageBuffer().take();
            if (returnMessage.getString("message").equals("found")) {
                List<String> bookObjIds = (List<String>) returnMessage.get("package");
                BotInterface.getInstance().setAliasStatus("");
                BotInterface.getInstance().addText(
                    "Alias",
                    "Here you go."
                );
                Platform.runLater(() -> BookListManager.getInstance().getUiWrapperFilteredList().setPredicate(
                    bookUIWrapper -> bookObjIds.contains(bookUIWrapper.getObjectId())
                ));
            } else
                BotInterface.getInstance().addText(
                    "Alias",
                    "Sorry but I found nothing."
                );
            return "success";
        } catch (Exception e) {
            return "failed";
        }
    }
}
