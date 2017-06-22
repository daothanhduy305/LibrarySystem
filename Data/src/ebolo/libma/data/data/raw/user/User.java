package ebolo.libma.data.data.raw.user;

import ebolo.libma.data.data.raw.AbstractMongolizable;
import ebolo.libma.data.data.raw.user.utils.MetaInfo;
import org.bson.Document;

/**
 * This is the demonstration class of the internal database user entity
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see AuthenticationInfo
 * @see AbstractMongolizable
 * @since 06/06/2017
 */

public abstract class User extends AbstractMongolizable {
    
    /**
     * Bson document, which is returned when the toMongoDocument() is revoked
     */
    Document userDocument;
    private AuthenticationInfo authenticationInfo;
    /**
     * indicates type of the user, so that permissions/actions can be granted per user
     */
    private MetaInfo.USER_MODE userMode;
    private String firstName, middleName, lastName, email;
    
    User(MetaInfo.USER_MODE userMode, String firstName, String middleName, String lastName, String email,
         String userName, String password) {
        this.authenticationInfo = new AuthenticationInfo(userName, password);
        this.userMode = userMode;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
    }
    
    User(Document document) {
        this.authenticationInfo = new AuthenticationInfo((Document) document.get("auth"));
        this.firstName = document.getString("first_name");
        this.middleName = document.getString("middle_name");
        this.lastName = document.getString("last_name");
        this.email = document.getString("email");
        this.userMode = MetaInfo.USER_MODE.valueOf(document.getString("user_mode"));
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getUsername() {
        return authenticationInfo.getUserName();
    }
    
    public String getEmail() {
        return email;
    }
    
    void prepareBasicUserDocument() {
        if (userDocument == null) {
            userDocument = new Document("auth", authenticationInfo.toMongoDocument());
            userDocument.put("first_name", firstName);
            userDocument.put("middle_name", middleName);
            userDocument.put("last_name", lastName);
            userDocument.put("email", email);
            userDocument.put("user_mode", userMode.name());
        }
    }
}
