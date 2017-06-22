package ebolo.libma.stub.commander.utils;

import ebolo.libma.data.data.raw.transaction.TransactionWrapper;
import ebolo.libma.stub.db.DbPortal;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * A utility contains various helper methods for internal transaction entity
 *
 * @author Ebolo
 * @version 21/06/2017
 * @since 21/06/2017
 */

public class TransactionUtils {
    
    /**
     * Conversion method converts a Transaction into a TransactionWrapper
     *
     * @param document the document created from a transaction
     * @return the document from the result TransactionWrapper
     */
    public static Document toTransactionWrapper(Document document) {
        // Retrieve student info
        Document studentDoc = DbPortal.getInstance().getUserDb()
            .find(new Document("_id", new ObjectId(document.getString("student")))).first();
        String student = studentDoc.getString("first_name") + ' ' + studentDoc.getString("last_name");
        // Retrieve book info
        Document bookDoc = DbPortal.getInstance().getBookDb()
            .find(new Document("_id", new ObjectId(document.getString("book")))).first();
        String book = ((Document) bookDoc.get("info_doc")).getString("title");
        // Create transaction wrapper
        Document returnDocument = new TransactionWrapper(
            student, book, document.getLong("start_time"), document.getLong("expired_time"),
            document.getBoolean("returned"), document.getBoolean("expired"),
            document.getObjectId("_id").toString()
        ).toMongoDocument();
        // Inject "last_modified" timestamp
        returnDocument.put("last_modified", document.getLong("last_modified"));
        return returnDocument;
    }
}
