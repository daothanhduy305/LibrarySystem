package ebolo.libma.commons.assistant.proc;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ebolo.libma.commons.assistant.ui.BotInterface;
import ebolo.libma.generic.keys.KeyConfigs;

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
                    BotInterface.getInstance().addText(
                        "Alias",
                        response.getResult().getFulfillment().getSpeech()
                    );
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
    
    
}
