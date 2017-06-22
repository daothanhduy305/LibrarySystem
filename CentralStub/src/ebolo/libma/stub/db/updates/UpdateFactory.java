package ebolo.libma.stub.db.updates;

import ebolo.libma.commons.net.Message;
import org.bson.Document;

import java.util.List;

/**
 * Created by ebolo on 6/7/17.
 */
public class UpdateFactory {
    public static Document createUpdate(List<Document> updates, String type) {
        Document response = Message.messageGenerate("update", updates);
        response.put("type", type);
        return response;
    }
}
