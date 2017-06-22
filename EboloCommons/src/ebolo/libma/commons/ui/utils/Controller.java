package ebolo.libma.commons.ui.utils;

import javafx.stage.Window;

/**
 * Controller interface, which is used for JavaFx UI controllers within this project
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public interface Controller {
    void setUpUI();
    
    Window getWindow();
}
