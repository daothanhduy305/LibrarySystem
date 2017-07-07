package ebolo.libma.stub.commander.commands.librarian;

import com.mongodb.client.result.UpdateResult;
import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import ebolo.libma.stub.commander.utils.TransactionUtils;
import ebolo.libma.stub.db.DbPortal;
import ebolo.libma.stub.db.updates.UpdateFactory;
import ebolo.libma.stub.net.managers.ActiveUserManager;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * StubCommand for librarians finishing list of transactions
 *
 * @author Ebolo
 * @version 02/07/2017
 * @see StubCommand
 * @since 02/07/2017
 */

public class FinishTransactionCommand extends StubCommand {
    private List<String> finishingTransactions;
    
    FinishTransactionCommand(SocketWrapper client, Object... args) {
        super(client, args);
    }
    
    @Override
    protected void notifyCommandResult() {
    
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected boolean checkCorrectness() {
        try {
            if (args.length == 1) {
                finishingTransactions = (List<String>) args[0];
                return true;
            }
            throw new Exception();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected boolean legalAction() throws Exception {
        Map<String, Document> updatedBooks = new HashMap<>(), updatedStudent = new HashMap<>();
        List<Document> updatedTransaction = new ArrayList<>();
        
        synchronized (DbPortal.getInstance()) {
            finishingTransactions.forEach(s -> {
                // Get transaction from DB
                final Document transactionDocument =
                    DbPortal.getInstance().getTransactionDb().find(
                        new Document("_id", new ObjectId(s))
                    ).first();
                final long currentTime = System.currentTimeMillis();
                Document query;
                // Create book update query
                query = new Document("$inc", new Document("unit_available", 1));
                Document setDocument = new Document("last_modified", currentTime);
                setDocument.put("available", true);
                query.put("$set", setDocument);
                UpdateResult updateResult;
                
                updateResult = DbPortal.getInstance().getBookDb().updateOne(
                    new Document("_id", new ObjectId(transactionDocument.getString("book"))),
                    query
                );
                Document bookDocument = DbPortal.getInstance().getBookDb().find(
                    new Document("_id", new ObjectId(transactionDocument.getString("book")))
                ).first();
                System.out.println(bookDocument);
                updatedBooks.put(bookDocument.getObjectId("_id").toString(), bookDocument);
                
                if (updateResult.getModifiedCount() > 0) {
                    // Create student update query
                    query = new Document("$inc", new Document("borrowing", -1));
                    query.put("$set", new Document("last_modified", currentTime));
                    updateResult = DbPortal.getInstance().getUserDb().updateOne(
                        new Document("_id", new ObjectId(transactionDocument.getString("student"))),
                        query
                    );
                    Document studentDocument = DbPortal.getInstance().getUserDb().find(
                        new Document("_id", new ObjectId(transactionDocument.getString("student")))
                    ).first();
                    updatedStudent.put(studentDocument.getObjectId("_id").toString(), studentDocument);
                    System.out.println(studentDocument);
                    if (updateResult.getModifiedCount() > 0) {
                        // Set transaction's returned state to true
                        setDocument = new Document("last_modified", currentTime);
                        setDocument.put("returned", true);
                        query = new Document("$set", setDocument);
                        updateResult = DbPortal.getInstance().getTransactionDb().updateOne(
                            new Document("_id", transactionDocument.getObjectId("_id")),
                            query
                        );
                        if (updateResult.getModifiedCount() > 0) {
                            // If success then add to resultList
                            Document updatedTransactionDocument = DbPortal.getInstance().getTransactionDb().find(
                                new Document("_id", new ObjectId(s))
                            ).first();
                            updatedTransaction.add(
                                TransactionUtils.toTransactionWrapperDocument(updatedTransactionDocument));
                            System.out.println(updatedTransactionDocument);
                        }
                    }
                }
            });
            
            if (updatedTransaction.size() > 0) {
                // Update book(s) info to all clients
                ActiveUserManager.getInstance().sendMessageToAll(
                    null, UpdateFactory.createUpdate(new ArrayList<>(updatedBooks.values()), "book")
                );
                // Update student(s) info to all librarians
                ActiveUserManager.getInstance().sendMessageToAllLibrarians(
                    null, UpdateFactory.createUpdate(new ArrayList<>(updatedStudent.values()), "student")
                );
                // Update transaction(s) info to all librarians
                ActiveUserManager.getInstance().sendMessageToAllLibrarians(
                    null, UpdateFactory.createUpdate(updatedTransaction, "transaction")
                );
                new Thread(() -> {
                    List<String> updatingStudents = updatedStudent.values()
                        .parallelStream()
                        .map(document -> document.getObjectId("_id").toString())
                        .collect(Collectors.toList());
                    
                    ActiveUserManager.getInstance()
                        .getActiveUsers()
                        .values()
                        .parallelStream()
                        .filter(socketWrapper -> socketWrapper.getUserMode() == MetaInfo.USER_MODE.Student)
                        .filter(socketWrapper -> updatingStudents.contains(socketWrapper.getClientId()))
                        .forEach(socketWrapper ->
                            socketWrapper.sendMessage(UpdateFactory.createUpdate(
                                updatedTransaction
                                    .parallelStream()
                                    .filter(
                                        document -> document.getString("student").equals(socketWrapper.getClientId())
                                    )
                                    .collect(Collectors.toList()),
                                "transaction"
                            ))
                        );
                }).start();
                client.sendMessage(Message.messageGenerate("success", "" + updatedTransaction.size()));
                return true;
            } else {
                failedReason = finishingTransactions.size() == 0 ?
                    "Transaction set is empty" : "Cannot finish transaction(s) from database!";
                return false;
            }
        }
    }
}
