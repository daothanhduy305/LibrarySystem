package ebolo.libma.commons.commands;

import ebolo.libma.commons.net.Stub;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import org.bson.Document;

/**
 * Helper class contains various utilizing methods to create and send command in Bson document format
 *
 * @author Ebolo
 * @version 13/06/2017
 * @since 13/06/2017
 */

public class CommandUtils {
    public static boolean sendCommand(
        MetaInfo.USER_MODE commandType, Stub stub, String command, Object... args) {
        synchronized (stub) {
            Document commandDocument = new Document("command_type", commandType);
            commandDocument.put("command", command);
            commandDocument.put("args", args);
            
            return stub.sendMessage(commandDocument);
        }
    }
}
