package ebolo.libma.stub.commander.commands.student;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.sun.istack.internal.Nullable;
import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.data.data.raw.transaction.Transaction;
import ebolo.libma.data.data.raw.transaction.TransactionWrapper;
import ebolo.libma.stub.db.DbPortal;
import ebolo.libma.stub.db.updates.UpdateFactory;
import ebolo.libma.stub.net.managers.ActiveUserManager;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Collections;

/**
 * StubCommand for student reserving an available book
 *
 * @author Ebolo
 * @version 07/06/2017
 * @see StubCommand
 * @since 06/06/2017
 */

public class ReserveCommand extends StubCommand {
    /**
     * Transaction made by the student on client side
     */
    private Transaction transaction;
    
    ReserveCommand(@Nullable SocketWrapper client, Object[] args) {
        super(client, args);
    }
    
    @Override
    protected void notifyCommandResult() {
    
    }
    
    @Override
    protected boolean checkCorrectness() {
        try {
            if (args.length == 1) {
                transaction = (Transaction) args[0];
                return true;
            }
            throw new Exception();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected boolean legalAction() throws Exception {
        synchronized (DbPortal.getInstance()) { // one book at a time
            // prepare book/student info
            final MongoCollection<Document> bookDb = DbPortal.getInstance().getBookDb();
            final MongoCollection<Document> studentDb = DbPortal.getInstance().getUserDb();
            final ObjectId bookObjId = new ObjectId(transaction.getBookObjId()),
                studentObjId = new ObjectId(transaction.getStudentObjId());
            final Document bookDoc = bookDb.find(new Document("_id", bookObjId)).first(),
                studentDoc = DbPortal.getInstance().getUserDb().find(new Document("_id", studentObjId)).first();
            // get current time as version number
            final long currentTime = System.currentTimeMillis();
    
            if (bookDoc.getBoolean("available")) {
                // retrieve book from database and check if it's available
                Document updateQuery = new Document("$inc", new Document("unit_available", -1));
                updateQuery.put("$set", new Document("last_modified", currentTime));
                // If this is the last unit of the book then it is not available for other students
                if (bookDoc.getInteger("unit_available") == 1)
                    updateQuery.put("$set", new Document("available", false));
                // if book is available then try updating book's info in database first
                UpdateResult updateResult;
                updateResult = bookDb.updateOne(
                    new Document("_id", bookObjId),
                    updateQuery
                );
                if (updateResult.getModifiedCount() > 0) {
                    // If everything is ok then modify student's info
                    updateQuery = new Document("$inc", new Document("borrowing", 1));
                    updateQuery.put("$set", new Document("last_modified", currentTime));
                    updateResult = DbPortal.getInstance().getUserDb().updateOne(
                        new Document("_id", studentObjId),
                        updateQuery
                    );
                    if (updateResult.getModifiedCount() > 0) {
                        // Then push the transaction into database
                        long startTime = System.currentTimeMillis();
                        transaction.setStartTime(startTime);
                        transaction.setExpireTime(startTime + bookDoc.getLong("period") * 1000);
                        Document transactionDoc = transaction.toMongoDocument();
                        transactionDoc.put("last_modified", currentTime);
                        DbPortal.getInstance().getTransactionDb().insertOne(transactionDoc);
                        // Update other clients
                        // First we need to update book's info to all clients
                        ActiveUserManager.getInstance().sendMessageToAll(null, UpdateFactory.createUpdate(
                            Collections.singletonList(bookDb.find(new Document("_id", bookObjId)).first()), "book"
                        ));
                        // And then update student's info to librarians
                        ActiveUserManager.getInstance().sendMessageToAllLibrarians(null, UpdateFactory.createUpdate(
                            Collections.singletonList(studentDb.find(new Document("_id", studentObjId)).first()), "student"
                        ));
        
                        // Finally send out the transaction wrapper back to client
                        String studentName =
                            studentDoc.getString("first_name") + ", " + studentDoc.getString("last_name");
                        TransactionWrapper transactionWrapper = new TransactionWrapper(
                            studentName, ((Document) bookDoc.get("info_doc")).getString("title"),
                            transaction.getStartTime(), transaction.getExpireTime(),
                            transaction.isReturned(), transaction.isExpired(),
                            transactionDoc.getObjectId("_id").toString()
                        );
                        client.sendMessage(Message.messageGenerate("success", transactionWrapper));
                        // and to librarians
                        Document transactionWrapperDoc = transactionWrapper.toMongoDocument();
                        transactionWrapperDoc.put("last_modified", currentTime);
                        ActiveUserManager.getInstance().sendMessageToAllLibrarians(null, UpdateFactory.createUpdate(
                            Collections.singletonList(transactionWrapperDoc), "transaction"
                        ));
                        return true;
                    }
                }
                failedReason = "Cannot get access to internal database!";
                return false;
            }
            failedReason = "Sorry, book is not available to be reserved!";
            return false;
        }
    }
    
    @Override
    protected void illegalAction() throws Exception {
    }
}
