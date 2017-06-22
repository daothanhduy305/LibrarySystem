package ebolo.libma.stub.commander.commands.librarian;

import com.mongodb.client.result.UpdateResult;
import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.stub.commander.commands.Command;
import ebolo.libma.stub.db.DbPortal;
import ebolo.libma.stub.db.updates.UpdateFactory;
import ebolo.libma.stub.net.managers.ActiveUserManager;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Collections;

/**
 * Command for librarian modifying a book's info
 *
 * @author Ebolo
 * @version 16/06/2017
 * @see Command
 * @since 16/06/2017
 */

public class ModifyCommand extends Command {
    private Book modifiedBook;
    private String bookObjId;
    
    ModifyCommand(SocketWrapper client, Object[] args) {
        super(client, args);
    }
    
    @Override
    protected void notifyCommandResult() {
    
    }
    
    @Override
    protected boolean checkCorrectness() {
        try {
            if (args.length == 2) {
                bookObjId = (String) args[0];
                modifiedBook = (Book) args[1];
                return true;
            }
            throw new Exception();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected boolean legalAction() throws Exception {
        // convert modified book into Bson document
        Document bookDoc = modifiedBook.toMongoDocument();
        bookDoc.put("last_modified", System.currentTimeMillis()); // update version
        // push into database
        UpdateResult updateResult = DbPortal.getInstance().getBookDb().replaceOne(
            new Document("_id", new ObjectId(bookObjId)),
            bookDoc
        );
        if (updateResult.getModifiedCount() > 0) {
            // if success then send a success message to client and notify the others
            client.sendMessage(Message.messageGenerate("success", ""));
            bookDoc.put("_id", new ObjectId(bookObjId));
            ActiveUserManager.getInstance().sendMessageToAll(client.getClientId(), UpdateFactory.createUpdate(
                Collections.singletonList(bookDoc), "book"
            ));
            return true;
        }
        failedReason = "Cannot modify record in database!";
        return false;
    }
}
