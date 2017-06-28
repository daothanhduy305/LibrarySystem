package ebolo.libma.assistant;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ebolo.libma.assistant.ui.BotInterface;
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
 * @version 27/06/2017
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
    
    /**
     * Get user's speech and process based on analyzed intent(s)
     *
     * @param speech user's speech
     */
    @SuppressWarnings("unchecked")
    synchronized public void saySomething(String speech) {
        if (!speech.isEmpty()) {
            new Thread(() -> {
                try {
                    BotInterface.getInstance().setAliasStatus("typing...");
                    // Trying to contact online assistant with speech
                    HttpURLConnection witConnection = (HttpURLConnection) new URL(
                        "https://api.wit.ai/message?v=20170307&q=" + URLEncoder.encode(speech, "UTF-8")
                    ).openConnection();
                    witConnection.setRequestMethod("GET");
                    witConnection.setRequestProperty("Accept", "application/json");
                    witConnection.setRequestProperty("Authorization", "Bearer " + KeyConfigs.getWitClientKey());
    
                    String intent = "";
                    if (witConnection.getResponseCode() == 200) { // Success code
                        // Get analysis
                        BufferedReader br = new BufferedReader(new InputStreamReader((witConnection.getInputStream())));
                        Document response = (Document) Document.parse(br.readLine()).get("entities");
                        // Get user's intent
                        try {
                            intent = ((List<Document>) response.get("intent")).get(0).getString("value");
                        } catch (NullPointerException ignored) {}
                        switch (intent) {
                            case "searchbook":
                                try {
                                    searchBook(
                                        ((List<Document>) response.get("keyword"))
                                            .stream()
                                            .map(document -> document.getString("value"))
                                            .collect(Collectors.joining(" "))
                                    );
                                } catch (Exception e) {
                                    intent = "";
                                }
                                break;
                            default:
                                intent = "";
                                break;
                        }
                    }
                    witConnection.disconnect();
    
                    // Fallback
                    if (intent.isEmpty()) {
                        new Thread(() -> {
                            try {
                                AIRequest fallbackRequest = new AIRequest(speech);
                                AIResponse fallbackResponse = dataService.request(fallbackRequest);
                                BotInterface.getInstance().setAliasStatus("");
                                if (fallbackResponse.getStatus().getCode() == 200) { // Success code
                                    BotInterface.getInstance().addText(
                                        "Alias",
                                        fallbackResponse.getResult().getFulfillment().getSpeech()
                                    );
                                } else {
                                    BotInterface.getInstance().addText(
                                        "Alias",
                                        "Sorry but I'm not capable to do this at the moment!"
                                    );
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }).start();
                    }
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
    
    /**
     * Send the extracted keyword (done by AI) to stub and get the result list
     * @param keyword information that user wants to search for
     */
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