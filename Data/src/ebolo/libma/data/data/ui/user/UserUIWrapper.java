package ebolo.libma.data.data.ui.user;

import ebolo.libma.data.data.raw.user.User;
import ebolo.libma.data.data.ui.ObjectUIWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A UI wrapper class for raw <code>User</code> data type. All of its fields are properties that can be bound to JavaFX
 * UI components, and have the value from the host object.
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see User
 * @see ObjectUIWrapper
 * @since 12/06/2017
 */

public abstract class UserUIWrapper<T extends User> extends ObjectUIWrapper<T> {
    private StringProperty username, firstName, middleName, lastName, email;
    
    UserUIWrapper(T user, String objectId) {
        super(user, objectId);
        
        this.username = new SimpleStringProperty(user.getUsername());
        this.firstName = new SimpleStringProperty(user.getFirstName());
        this.middleName = new SimpleStringProperty(user.getMiddleName());
        this.lastName = new SimpleStringProperty(user.getLastName());
        this.email = new SimpleStringProperty(user.getEmail());
    }
    
    public String getUsername() {
        return username.get();
    }
    
    public StringProperty usernameProperty() {
        return username;
    }
    
    public StringProperty firstNameProperty() {
        return firstName;
    }
    
    public StringProperty middleNameProperty() {
        return middleName;
    }
    
    public StringProperty lastNameProperty() {
        return lastName;
    }
    
    public String getObjectId() {
        return objectId;
    }
    
    @Override
    public void update(T user) {
        this.username.set(user.getUsername());
        this.firstName.set(user.getFirstName());
        this.middleName.set(user.getMiddleName());
        this.lastName.set(user.getLastName());
        this.email.set(user.getEmail());
    }
}
