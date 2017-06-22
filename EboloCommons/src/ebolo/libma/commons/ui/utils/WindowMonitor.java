package ebolo.libma.commons.ui.utils;

import javafx.stage.Window;

/**
 * This class is used as a wrapper for the main App window. TODO: make this class deprecated
 *
 * @author Ebolo
 * @version 09/06/2017
 * @since 09/06/2017
 */

public class WindowMonitor {
    private static WindowMonitor ourInstance;
    private Window monitoringWindow;
    
    private WindowMonitor() {
    }
    
    public static WindowMonitor getInstance() {
        if (ourInstance == null)
            ourInstance = new WindowMonitor();
        return ourInstance;
    }
    
    public void setWindow(Window monitoringWindow) {
        this.monitoringWindow = monitoringWindow;
    }
    
    public Window getMonitoringWindow() {
        return monitoringWindow;
    }
}
