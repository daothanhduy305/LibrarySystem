package ebolo.libma.data.db.factories;

import ebolo.libma.data.data.raw.AbstractMongolizable;
import ebolo.libma.data.data.ui.ObjectUIWrapper;

/**
 * This interface is to be implemented for classes that have the characteristics of a factory class, which would
 * produce objects of which classes extends the ObjectUIWrapper class and use the objects of which class extends
 * AbstractMongolizable as an ingredient
 *
 * @author Ebolo
 * @version 20/06/2017
 * @see ObjectUIWrapper
 * @see AbstractMongolizable
 * @since 20/06/2017
 */

public interface ObjectUIWrapperFactory<I extends AbstractMongolizable, P extends ObjectUIWrapper> {
    P produce(I i, String objectId);
}
