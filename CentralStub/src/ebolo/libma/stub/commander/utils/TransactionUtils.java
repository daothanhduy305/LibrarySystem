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
     * @param transactionDocument the transactionDocument created from a transaction
     * @return the transactionDocument from the result TransactionWrapper
     */
    public static Document toTransactionWrapperDocument(Document transactionDocument) {
        // Retrieve student info
        Document studentDoc = DbPortal.getInstance().getUserDb()
            .find(new Document("_id", new ObjectId(transactionDocument.getString("student")))).first();
        String student = studentDoc.getString("first_name") + ' ' + studentDoc.getString("last_name");
        // Retrieve book info
        Document bookDoc = DbPortal.getInstance().getBookDb()
            .find(new Document("_id", new ObjectId(transactionDocument.getString("book")))).first();
        String book = ((Document) bookDoc.get("info_doc")).getString("title");
        // Create transaction wrapper
        transactionDocument.put("student", student);
        transactionDocument.put("book", book);
        return transactionDocument;
    }
}
