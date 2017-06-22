package ebolo.libma.client.utils.configs;

import java.io.File;

/**
 * Configurations for client application
 *
 * @author Ebolo
 * @version 07/06/2017
 * @since 07/06/2017
 */

public class AppConfigurations {
    private static final String jarFilePath =
        AppConfigurations.class.getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getFile()
            .replace('/', File.separatorChar);
    private static final String workingDir =
        (new StringBuilder(jarFilePath))
            .delete(
                jarFilePath.lastIndexOf(File.separatorChar) + 1,
                jarFilePath.length())
            .toString();
    private static AppConfigurations ourInstance;
    private boolean closeOnAdded = false;
    
    private AppConfigurations() {
    }
    
    public static AppConfigurations getInstance() {
        if (ourInstance == null)
            ourInstance = new AppConfigurations();
        return ourInstance;
    }
    
    public static String getWorkingDir() {
        return workingDir;
    }
    
    public boolean isCloseOnAdded() {
        return closeOnAdded;
    }
    
    public void setCloseOnAdded(boolean closeOnAdded) {
        this.closeOnAdded = closeOnAdded;
    }
}
