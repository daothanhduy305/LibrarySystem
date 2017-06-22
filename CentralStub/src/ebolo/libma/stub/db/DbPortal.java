package ebolo.libma.stub.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import ebolo.libma.stub.utils.configs.DbConfigurations;
import org.bson.Document;

import java.util.Collections;

/**
 * Class that acts as the portal between the stub and the internal database (MongoDB 3.4)
 *
 * @author Ebolo
 * @version 07/06/2017
 * @see <a href="http://mongodb.github.io/mongo-java-driver/3.4/javadoc/">MongoDB 3.4 API</a>
 * @since 06/06/2017
 */

public class DbPortal {
    private static DbPortal ourInstance;
    private final MongoCredential dbCredential;
    private MongoCollection<Document> bookDb, userDb, transactionDb;
    
    private DbPortal() {
        dbCredential = MongoCredential.createCredential(
            DbConfigurations.getUserName(),
            DbConfigurations.getMainDbName(),
            DbConfigurations.getPassword().toCharArray()
        );
    }
    
    public static DbPortal getInstance() {
        if (ourInstance == null)
            ourInstance = new DbPortal();
        return ourInstance;
    }
    
    public void setUp() {
        MongoClient dbClient = new MongoClient(
            new ServerAddress(DbConfigurations.getDbHost()),
            Collections.singletonList(dbCredential)
        );
        
        MongoDatabase database = dbClient.getDatabase("libmantest");
        
        bookDb = database.getCollection(DbConfigurations.getBookDbName());
        userDb = database.getCollection(DbConfigurations.getUserDbName());
        transactionDb = database.getCollection(DbConfigurations.getTransactionDbName());
    }
    
    public MongoCollection<Document> getBookDb() {
        return bookDb;
    }
    
    public MongoCollection<Document> getUserDb() {
        return userDb;
    }
    
    public MongoCollection<Document> getTransactionDb() {
        return transactionDb;
    }
}
