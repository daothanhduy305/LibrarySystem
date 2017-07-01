package ebolo.libma.assistant;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ebolo.libma.assistant.commander.CentralCommandFactory;
import ebolo.libma.assistant.ui.BotInterface;
import ebolo.libma.commons.commands.factory.ClientCommandFactory;
import ebolo.libma.generic.keys.KeyConfigs;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    
                    Future<String> success = null;
                    if (witConnection.getResponseCode() == 200) { // Success code
                        // Get analysis
                        BufferedReader br = new BufferedReader(new InputStreamReader((witConnection.getInputStream())));
                        Document response = (Document) Document.parse(br.readLine()).get("entities");
                        // Get user's intent
                        try {
                            String intent = ((List<Document>) response.get("intent"))
                                .get(0)
                                .getString("value");
                            success = CentralCommandFactory.getInstance().run(intent, response);
                        } catch (NullPointerException ignored) {}
                    }
                    witConnection.disconnect();
    
                    // Fallback - failed maybe
                    if (success == null || success.get().equals("failed")) {
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
                    }
                } catch (IOException | InterruptedException | ExecutionException | AIServiceException e) {
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
    
    public void setExtendedFactory(ClientCommandFactory extendedFactory) {
        CentralCommandFactory.getInstance().setExtendedFactory(extendedFactory);
    }
}
