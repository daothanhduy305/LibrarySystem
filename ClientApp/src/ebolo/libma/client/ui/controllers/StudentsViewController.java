package ebolo.libma.client.ui.controllers;

import ebolo.libma.commons.ui.utils.Controller;
import javafx.stage.Window;

/**
 * Controller (for UI) of the student info tab
 *
 * @author Ebolo
 * @version 09/06/2017
 * @since 09/06/2017
 */

public class StudentsViewController implements Controller {
    private static StudentsViewController ourInstance;
    
    private StudentsViewController() {
    }
    
    public static StudentsViewController getInstance() {
        if (ourInstance == null)
            ourInstance = new StudentsViewController();
        return ourInstance;
    }
    
    @SuppressWarnings("Duplicates")
    @Override
    public void setUpUI() {
    
    }
    
    
    @Override
    public Window getWindow() {
        return null;
    }
}
