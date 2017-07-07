package ebolo.libma.stub.commander.commands.librarian;

import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;
import ebolo.libma.stub.db.DbPortal;
import ebolo.libma.stub.db.updates.UpdateFactory;
import ebolo.libma.stub.net.managers.ActiveUserManager;
import org.apache.http.HttpHost;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * StubCommand for librarian removing a list of books
 *
 * @author Ebolo
 * @version 13/06/2017
 * @see StubCommand
 * @since 13/06/2017
 */

public class RemoveBooksCommand extends StubCommand {
    private List<String> bookObjIds;
    
    RemoveBooksCommand(SocketWrapper client, Object[] args) {
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
                bookObjIds = (List<String>) args[0];
                return true;
            }
            throw new Exception();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    protected boolean legalAction() throws Exception {
        List<String> resultList = new ArrayList<>(bookObjIds.size());
    
        synchronized (DbPortal.getInstance()) {
            bookObjIds.forEach(s -> {
                if (DbPortal.getInstance().getBookDb()
                    .deleteOne(new Document("_id", new ObjectId(s)))
                    .getDeletedCount() > 0) {
                    resultList.add(s);
                
                    RestClient restClient = RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")).build();
                    //index a document
                    try {
                        Response indexResponse = restClient.performRequest(
                            "DELETE",
                            "/libmantest/books/" + s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        
            if (resultList.size() > 0) {
                client.sendMessage(Message.messageGenerate("success", resultList));
                ActiveUserManager.getInstance().sendMessageToAll(
                    client.getClientId(), UpdateFactory.createDeleteUpdate(resultList, "book"));
                return true;
            } else {
                failedReason = bookObjIds.size() == 0 ?
                    "Deleting set is empty" : "Cannot delete book(s) from database!";
                return false;
            }
        }
    }
}
