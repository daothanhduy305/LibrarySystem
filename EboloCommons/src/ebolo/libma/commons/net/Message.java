package ebolo.libma.commons.net;

import org.bson.Document;

/**
 * <code>Message</code> class contains helper methods to generate correct message documents used for communicating
 * between the clients and the central stub
 *
 * @author Ebolo
 * @version 15/06/2017
 * @since 15/06/2017
 */

public class Message {
    public static Document messageGenerate(String type, Object messagePackage) {
        Document message = new Document("message", type);
        message.put("package", messagePackage);
        return message;
    }
}
