package ebolo.libma.commons.ui;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * Helper class provides various physical screen info
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class ScreenUtils {
    private static final Rectangle2D monitorShape = Screen.getPrimary().getVisualBounds();
    
    public static double getScreenHeight() {
        return monitorShape.getHeight();
    }
    
    public static double getScreenWidth() {
        return monitorShape.getWidth();
    }
}
