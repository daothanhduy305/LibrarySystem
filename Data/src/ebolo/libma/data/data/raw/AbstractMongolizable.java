package ebolo.libma.data.data.raw;

import ebolo.libma.data.data.utils.interfaces.Mongolizable;

/**
 * This is the abstract class that implements the inter face Mongolizable. Classes that extend this normally
 * be the demonstration prototype for the database Bson documents, so, each should contains an object ID for
 * convenient usage
 *
 * @author Ebolo
 * @version 20/06/2017
 * @see Mongolizable
 * @since 20/06/2017
 */

public abstract class AbstractMongolizable implements Mongolizable {
    protected String objectId;
    
    public String getObjectId() {
        return objectId;
    }
    
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
