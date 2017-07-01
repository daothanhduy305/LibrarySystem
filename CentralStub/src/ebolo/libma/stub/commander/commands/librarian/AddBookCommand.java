package ebolo.libma.stub.commander.commands.librarian;

import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.stub.db.DbPortal;
import ebolo.libma.stub.db.updates.UpdateFactory;
import ebolo.libma.stub.net.managers.ActiveUserManager;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Collections;

/**
 * StubCommand for librarian add new book
 *
 * @author Ebolo
 * @version 09/06/2017
 * @see StubCommand
 * @since 09/06/2017
 */
public class AddBookCommand extends StubCommand {
    private Book book;
    
    AddBookCommand(SocketWrapper client, Object[] args) {
        super(client, args);
    }
    
    @Override
    protected void notifyCommandResult() {
    
    }
    
    @Override
    protected boolean checkCorrectness() {
        try {
            if (args.length == 1) {
                book = (Book) args[0];
                return true;
            }
            throw new Exception();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected boolean legalAction() throws Exception {
        // convert book into Bson document
        Document bookDocument = book.toMongoDocument();
        Document query = new Document("info_doc.title", book.getTitle());
        query.put("info_doc.authors", book.getAuthorsList().get(0));
        // check if book exists in database by week primary combo key: title + author
        if (DbPortal.getInstance().getBookDb().find(query).first() == null) {
            bookDocument.put("last_modified", System.currentTimeMillis());
            DbPortal.getInstance().getBookDb().insertOne(bookDocument);
            ObjectId bookObjectId = bookDocument.getObjectId("_id");
            if (bookObjectId != null) {
                // if everything is ok then send success message to client and notify the others
                client.sendMessage(Message.messageGenerate("success", bookObjectId.toString()));
                ActiveUserManager.getInstance().sendMessageToAll(client.getClientId(), UpdateFactory.createUpdate(
                    Collections.singletonList(bookDocument), "book"
                ));
                return true;
            } else
                failedReason = "Something went wrong. Cannot insert new book into database!";
        } else
            failedReason = "Book exists in database!";
        return false;
    }
}
