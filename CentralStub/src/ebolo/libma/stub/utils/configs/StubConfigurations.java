package ebolo.libma.stub.utils.configs;

import java.io.File;

/**
 * Contains various configurations used for central stub
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class StubConfigurations {
    private final static int SERVICE_PORT = 3005;
    
    private static final String jarFilePath =
        StubConfigurations.class.getProtectionDomain()
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
    
    public static int getServicePort() {
        return SERVICE_PORT;
    }
}
