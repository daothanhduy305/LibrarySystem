package ebolo.libma.authenticate.ui.fxml;

import java.net.URL;

/**
 * A utility class to retrieve the FXML files within same package
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class FxmlManager {
    private static FxmlManager ourInstance;
    
    private FxmlManager() {
    }
    
    public static FxmlManager getInstance() {
        if (ourInstance == null)
            ourInstance = new FxmlManager();
        return ourInstance;
    }
    
    public URL getFxmlTemplate(String fxmlFileName) {
        return getClass().getResource(fxmlFileName + ".fxml");
    }
}
