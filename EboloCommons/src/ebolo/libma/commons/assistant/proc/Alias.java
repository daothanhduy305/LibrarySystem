package ebolo.libma.commons.assistant.proc;

import ebolo.libma.commons.assistant.ui.BotInterface;
import ebolo.libma.commons.commands.CommandUtils;
import ebolo.libma.commons.net.StubCommunication;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.data.db.local.BookListManager;
import ebolo.libma.generic.keys.KeyConfigs;
import javafx.application.Platform;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
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
    
    private Alias() {
    }
    
    public static Alias getInstance() {
        if (ourInstance == null)
            ourInstance = new Alias();
        return ourInstance;
    }
    
    @SuppressWarnings("unchecked")
    synchronized public void saySomething(String speech) {
        if (!speech.isEmpty()) {
            new Thread(() -> {
                try {
                    BotInterface.getInstance().setAliasStatus("typing...");
                    
                    HttpURLConnection witConnection = (HttpURLConnection) new URL(
                        "https://api.wit.ai/message?v=20170307&q=" + URLEncoder.encode(speech, "UTF-8")
                    ).openConnection();
                    witConnection.setRequestMethod("GET");
                    witConnection.setRequestProperty("Accept", "application/json");
                    witConnection.setRequestProperty("Authorization", "Bearer " + KeyConfigs.getWitClientKey());
                    
                    if (witConnection.getResponseCode() == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader((witConnection.getInputStream())));
                        Document response = (Document) Document.parse(br.readLine()).get("entities");
                        String intent = ((List<Document>) response.get("intent")).get(0).getString("value");
                        switch (intent) {
                            case "searchbook":
                                new Thread(() -> searchBook(
                                    ((List<Document>) response.get("keyword"))
                                        .stream()
                                        .map(document -> document.getString("value"))
                                        .collect(Collectors.joining(" "))
                                )).start();
                                break;
                            default:
                                BotInterface.getInstance().setAliasStatus("");
                                BotInterface.getInstance().addText(
                                    "Alias",
                                    "Sorry but I'm not capable to do this at the moment!"
                                );
                                break;
                        }
                    }
                    witConnection.disconnect();
                } catch (IOException e) {
                    BotInterface.getInstance().setAliasStatus("");
                    BotInterface.getInstance().addText(
                        "Alias",
                        "Sorry but it seems like the service is unavailable right now!"
                    );
                }
            }).start();
        } else {
            BotInterface.getInstance().addText(
                "Alias",
                "Please, say something."
            );
        }
    }
    
    @SuppressWarnings("unchecked")
    private void searchBook(String keyword) {
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
