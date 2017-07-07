package ebolo.libma.stub.commander.commands.librarian;

import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.stub.db.DbPortal;
import ebolo.libma.stub.db.updates.UpdateFactory;
import ebolo.libma.stub.net.managers.ActiveUserManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Collections;

import static ebolo.libma.commons.ui.utils.GenericUtils.util;

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
    
                // Update Elastic Search
                new Thread(() -> {
                    RestClient restClient = RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")).build();
                    book = new Book(bookDocument);
                    String content = (util(book.getFullTitle()) +
                        util(book.getAuthors().replace(",", "")) +
                        util(book.getCategories().replace(",", "")) +
                        util(book.getDescription()) +
                        util(book.getPublisher()) +
                        util(book.getPublishedDate()) +
                        util(book.getIsbn(10)) +
                        book.getIsbn(13)).replace("\"", "");
                    //index a document
                    HttpEntity entities = new NStringEntity(
                        "{\n" +
                            "    \"available\" : \"" + book.isAvailable() + "\",\n" +
                            "    \"content\" : \"" + content + "\"\n" +
                            "}", ContentType.APPLICATION_JSON);
        
                    try {
                        Response indexResponse = restClient.performRequest(
                            "PUT",
                            "/libmantest/books/" + book.getObjectId(),
                            Collections.emptyMap(),
                            entities);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                return true;
            } else
                failedReason = "Something went wrong. Cannot insert new book into database!";
        } else
            failedReason = "Book exists in database!";
        return false;
    }
}
