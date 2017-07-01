package ebolo.libma.stub.commander.commands.alias;

import ebolo.libma.commons.commands.command.StubCommand;
import ebolo.libma.commons.net.Message;
import ebolo.libma.commons.net.SocketWrapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StubCommand for Alias requesting a search for book(s) base on user's info
 *
 * @author Ebolo
 * @version 26/06/2017
 * @see StubCommand
 * @since 26/06/2017
 */

public class SearchBookCommand extends StubCommand {
    private String keyword;
    
    SearchBookCommand(SocketWrapper client, Object[] args) {
        super(client, args);
    }
    
    @Override
    protected void notifyCommandResult() {
    
    }
    
    @Override
    protected boolean checkCorrectness() {
        try {
            if (args.length == 1) {
                keyword = (String) args[0];
                return true;
            }
            throw new Exception();
        } catch (Exception e) {
            return false;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected boolean legalAction() throws Exception {
        RestClient restClient = RestClient.builder(
            new HttpHost("localhost", 9200, "http"),
            new HttpHost("localhost", 9201, "http")).build();
        
        Document elasticSearchQuery = new Document("_source", "");
        elasticSearchQuery.put("query", new Document("match", new Document("content", keyword)));
        HttpEntity entity = new NStringEntity(
            elasticSearchQuery.toJson(),
            ContentType.APPLICATION_JSON
        );
        
        try {
            Response response = restClient.performRequest(
                "GET",
                "/libmantest/books/_search",
                Collections.emptyMap(),
                entity);
            
            Document hitsDocument = (Document) Document.parse(EntityUtils.toString(response.getEntity())).get("hits");
            if (hitsDocument.getInteger("total") > 0) {
                List<String> objectIds = ((List<Document>) hitsDocument.get("hits"))
                    .stream()
                    .map(document -> document.getString("_id"))
                    .collect(Collectors.toList());
                restClient.close();
                client.sendMessage(Message.messageGenerate("found", objectIds));
                return true;
            } else
                client.sendMessage(Message.messageGenerate("notFound", ""));
        } catch (IOException e) {
            failedReason = "Internal database error";
        }
        restClient.close();
        return false;
    }
}
