package ebolo.libma.data.data.utils.interfaces;

import org.bson.Document;

import java.io.Serializable;

/**
 * This interface is to be implemented for classes that should both being serializable and
 * have the ability to be able to export a relevant Bson document in need
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public interface Mongolizable extends Serializable {
    Document toMongoDocument();
}
