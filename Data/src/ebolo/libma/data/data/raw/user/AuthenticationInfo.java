package ebolo.libma.data.data.raw.user;

import ebolo.libma.data.data.utils.interfaces.Mongolizable;
import org.apache.commons.codec.digest.Crypt;
import org.bson.Document;

/**
 * This is the helper class contains authentication info for a user
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see Mongolizable
 * @see <a href="https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/digest/Crypt.html">Apache crypt</a>
 * @since 06/06/2017
 */

public class AuthenticationInfo implements Mongolizable {
    
    private String userName;
    /**
     * User's hashed password (by Apache Crypt mechanism)
     */
    private String hashedPassword;
    
    AuthenticationInfo(final String userName, String password) {
        this.userName = userName;
        this.hashedPassword = Crypt.crypt(password);
    }
    
    AuthenticationInfo(Document document) {
        this.userName = document.getString("username");
        this.hashedPassword = document.getString("password");
    }
    
    @Override
    public Document toMongoDocument() {
        Document returnDocument = new Document("username", this.userName);
        returnDocument.put("password", this.hashedPassword);
        return returnDocument;
    }
    
    String getUserName() {
        return userName;
    }
}
