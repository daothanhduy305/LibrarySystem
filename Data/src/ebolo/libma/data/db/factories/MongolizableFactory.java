package ebolo.libma.data.db.factories;

import ebolo.libma.data.data.raw.AbstractMongolizable;
import org.bson.Document;

/**
 * This interface is to be implemented for classes that have the characteristics of a factory class, which would
 * produce objects of which classes extends the AbstractMongolizable class
 *
 * @author Ebolo
 * @version 20/06/2017
 * @see AbstractMongolizable
 * @since 20/06/2017
 */

public interface MongolizableFactory<T extends AbstractMongolizable> {
    T produce(Document document);
}
