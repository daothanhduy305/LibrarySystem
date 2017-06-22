package ebolo.libma.data.data.ui.transaction;

import ebolo.libma.data.data.raw.transaction.TransactionWrapper;
import ebolo.libma.data.data.ui.ObjectUIWrapper;
import javafx.beans.property.*;

/**
 * A UI wrapper class for raw <code>TransactionWrapper</code> data type. All of its fields are properties that can be bound to
 * JavaFX UI components, and have the value from the host object.
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see TransactionWrapper
 * @see ObjectUIWrapper
 * @since 20/06/2017
 */

public class TransactionUIWrapper extends ObjectUIWrapper<TransactionWrapper> {
    private StringProperty student, book;
    private LongProperty startTime, expireTime;
    private BooleanProperty returned, expired;
    
    public TransactionUIWrapper(TransactionWrapper transactionWrapper, String objectId) {
        super(transactionWrapper, objectId);
        
        this.student = new SimpleStringProperty(transactionWrapper.getStudent());
        this.book = new SimpleStringProperty(transactionWrapper.getBook());
        this.startTime = new SimpleLongProperty(transactionWrapper.getStartTime());
        this.expireTime = new SimpleLongProperty(transactionWrapper.getExpireTime());
        this.returned = new SimpleBooleanProperty(transactionWrapper.isReturned());
        this.expired = new SimpleBooleanProperty(transactionWrapper.isExpired());
    }
    
    @Override
    public void update(TransactionWrapper transactionWrapper) {
        this.student.set(transactionWrapper.getStudent());
        this.book.set(transactionWrapper.getBook());
        this.startTime.set(transactionWrapper.getStartTime());
        this.expireTime.set(transactionWrapper.getExpireTime());
        this.returned.set(transactionWrapper.isReturned());
        this.expired.set(transactionWrapper.isExpired());
    }
    
    public String getStudent() {
        return student.get();
    }
    
    public StringProperty studentProperty() {
        return student;
    }
    
    public String getBook() {
        return book.get();
    }
    
    public StringProperty bookProperty() {
        return book;
    }
}
