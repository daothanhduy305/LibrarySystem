package ebolo.libma.commons.ui.utils;

/**
 * Controller wrapper wraps the host controller along with its class. This is for the purpose of utilizing the automatic
 * UI production by using the <code>ControllerFactory</code> of the <code>FXMLLoader</code> class in <code>UIFactory</code>
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class ControllerWrapper {
    private Class controllerClass;
    private Object controller;
    
    public ControllerWrapper(Class controllerClass, Object controller) {
        this.controllerClass = controllerClass;
        this.controller = controller;
    }
    
    public Class getControllerClass() {
        return controllerClass;
    }
    
    public Object getController() {
        return controller;
    }
}
