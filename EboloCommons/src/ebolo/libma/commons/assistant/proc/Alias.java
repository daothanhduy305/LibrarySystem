package ebolo.libma.commons.assistant.proc;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ebolo.libma.commons.assistant.ui.BotInterface;
import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.db.local.BookListManager;
import ebolo.libma.generic.keys.KeyConfigs;
import javafx.application.Platform;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The artificial assistant module of the system, can be integrated into client applications
 *
 * @author Ebolo
 * @version 19/06/2017
 * @since 19/06/2017
 */

public class Alias {
    private static Alias ourInstance;
    private AIDataService dataService;
    
    private Alias() {
        AIConfiguration configuration = new AIConfiguration(KeyConfigs.getApiAiKey());
        dataService = new AIDataService(configuration);
    }
    
    public static Alias getInstance() {
        if (ourInstance == null)
            ourInstance = new Alias();
        return ourInstance;
    }
    
    synchronized public void saySomething(String speech) {
        if (!speech.isEmpty()) {
            try {
                AIRequest request = new AIRequest(speech);
                
                AIResponse response = dataService.request(request);
                
                if (response.getStatus().getCode() == 200) { // Success code
                    switch (response.getResult().getAction()) {
                        case "libma.search.book":
                            new Thread(() -> processBookAskingRequest(response)).start();
                            break;
                        default:
                            BotInterface.getInstance().addText(
                                "Alias",
                                response.getResult().getFulfillment().getSpeech()
                            );
                            break;
                    }
                } else {
                    System.err.println(response.getStatus().getErrorDetails());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            BotInterface.getInstance().addText(
                "Alias",
                "Please, say something."
            );
        }
    }
    
    @SuppressWarnings("unchecked")
    private void processBookAskingRequest(AIResponse response) {
        StringBuilder redundant = new StringBuilder("");
        String keyword;
        if (response.getResult().getParameters().get("book_entities") != null)
            keyword = response
                .getResult()
                .getParameters()
                .get("book_entities")
                .toString()
                .replace("\"", "");
        else {
            response.getResult().getParameters().forEach((s, jsonElement) -> {
                Logger.getLogger("myApp")
                    .log(Level.INFO, "action = search, parameter = " + s + ", value = " + jsonElement.toString());
                if (s.equals("First") && jsonElement.getAsString().equals("I m")) {
                    redundant.append("I'm");
                } else {
                    redundant.append(jsonElement.toString()
                        .replace("{", "")
                        .replace("}", "")
                        .replaceAll("\"\\w*\":", "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace(",", " ")
                        .replace("\"", "")
                    ).append(' ');
                }
            });
            List<String> redundantWords = Arrays.asList(redundant.toString().split(" "));
            keyword = Arrays.stream(response.getResult().getResolvedQuery()
                /*.replace("Do you ", "")*/
                .replace("?", "")
                .split(" "))
                .filter(s -> !redundantWords.contains(s))
                /*.filter(s -> !s.equals("I"))
                .filter(s -> !s.equals("a"))*/
                .collect(Collectors.joining(" "));
        }
        Logger.getLogger("myApp").log(Level.INFO, "action = search, keyword = " + keyword);
        CommandUtils.sendCommand(
            MetaInfo.USER_MODE.Alias,
            StubCommunication.getInstance().getStub(),
            "search",
            keyword
        );
        
        try {
            Document returnMessage = StubCommunication.getInstance().getStub().getMessageBuffer().take();
            if (returnMessage.getString("message").equals("found")) {
                List<String> bookObjIds = (List<String>) returnMessage.get("package");
                Platform.runLater(() -> {
                    BotInterface.getInstance().addText(
                        "Alias",
                        "Here you go."
                    );
                    BookListManager.getInstance().getUiWrapperFilteredList().setPredicate(
                        bookUIWrapper -> bookObjIds.contains(bookUIWrapper.getObjectId())
                    );
                });
            } else
                Platform.runLater(() -> BotInterface.getInstance().addText(
                    "Alias",
                    "Sorry but I found nothing."
                ));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
