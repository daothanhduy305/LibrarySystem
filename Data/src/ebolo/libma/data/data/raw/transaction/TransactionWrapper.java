package ebolo.libma.data.data.raw.transaction;

import ebolo.libma.data.data.raw.AbstractMongolizable;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Transaction is made when a book being reserved for a student
 * This is the wrapper class a.k.a the human-readable wrapper for the <code>Transaction class</code>
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see Transaction
 * @see AbstractMongolizable
 * @since 20/06/2017
 */

public class TransactionWrapper extends AbstractMongolizable {
    /**
     * indicates student's full name
     *
     * @see Transaction#studentObjId
     */
    private String student;
    /**
     * indicates book's title
     *
     * @see Transaction#bookObjId
     */
    private String book;
    /**
     * @see Transaction#startTime
     */
    private long startTime;
    /**
     * @see Transaction#expireTime
     */
    private long expireTime;
    /**
     * @see Transaction#returned
     */
    private boolean returned;
    /**
     * @see Transaction#expired
     */
    private boolean expired;
    
    public TransactionWrapper(String student, String book, long startTime, long expireTime, boolean returned,
                              boolean expired, String transactionObjId) {
        this.student = student;
        this.book = book;
        this.startTime = startTime;
        this.expireTime = expireTime;
        this.returned = returned;
        this.expired = expired;
        this.objectId = transactionObjId;
    }
    
    public TransactionWrapper(Document document) {
        this.student = document.getString("student");
        this.book = document.getString("book");
        this.startTime = document.getLong("start_time");
        this.expireTime = document.getLong("expired_time");
        this.expired = document.getBoolean("expired");
        this.returned = document.getBoolean("returned");
        this.objectId = document.getObjectId("_id").toString();
    }
    
    public String getStudent() {
        return student;
    }
    
    public String getBook() {
        return book;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
    
    public boolean isReturned() {
        return returned;
    }
    
    public boolean isExpired() {
        return expired;
    }
    
    @Override
    public Document toMongoDocument() {
        Document returnDoc = new Document("student", this.student);
        returnDoc.put("book", this.book);
        returnDoc.put("start_time", this.startTime);
        returnDoc.put("expired_time", this.expireTime);
        returnDoc.put("returned", this.returned);
        returnDoc.put("expired", this.expired);
        returnDoc.put("_id", new ObjectId(this.objectId));
        return returnDoc;
    }
}
