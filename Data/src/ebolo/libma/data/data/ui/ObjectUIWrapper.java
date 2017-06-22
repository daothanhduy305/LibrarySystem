package ebolo.libma.data.data.ui;

import ebolo.libma.data.data.raw.AbstractMongolizable;

/**
 * <code>ObjectUIWrapper</code> is the wrapper class for relevant raw data class. These ui wrapper classes are used by
 * JavaFX components, e.g <code>SortedList</code>, <code>FilteredList</code>, <code>TableView</code>,...
 * For the convenience sake, these classes also contains object ID and the relevant raw data object's object ID would be
 * updated when its ui wrapper is created to ensure every raw data objects have the correct object ID in the database
 * (which is missing in some creating new cases)
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see AbstractMongolizable
 * @since 06/06/2017
 */

public abstract class ObjectUIWrapper<T extends AbstractMongolizable> {
    protected String objectId;
    
    protected ObjectUIWrapper(T t, String objectId) {
        this.objectId = objectId;
        t.setObjectId(objectId);
    }
    
    public String getObjectId() {
        return objectId;
    }
    
    public abstract void update(T descendant);
}
