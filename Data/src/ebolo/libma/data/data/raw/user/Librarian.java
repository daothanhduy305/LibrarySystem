package ebolo.libma.data.data.raw.user;

import ebolo.libma.data.data.raw.user.utils.MetaInfo.USER_MODE;
import org.bson.Document;

/**
 * This is the demonstration class of the internal database librarian entity
 * Librarian basically is a user thus contains user's entities plus extra librarian's info
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see User
 * @since 06/06/2017
 */

public class Librarian extends User {
    
    /**
     * real life employee ID
     */
    private String employeeId;
    
    public Librarian(String firstName, String middleName, String lastName, String email,
                     String userName, String password, String employeeId) {
        super(USER_MODE.Librarian, firstName, middleName, lastName, email, userName, password);
        
        this.employeeId = employeeId;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    @Override
    public Document toMongoDocument() {
        prepareBasicUserDocument();
        userDocument.put("employee_id", employeeId);
        return userDocument;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Librarian)) return false;
        
        Librarian librarian = (Librarian) o;
        
        return employeeId.equals(librarian.employeeId);
    }
    
    @Override
    public int hashCode() {
        return employeeId.hashCode();
    }
}
