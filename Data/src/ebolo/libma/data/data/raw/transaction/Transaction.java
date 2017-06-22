package ebolo.libma.data.data.raw.transaction;

import ebolo.libma.data.data.utils.interfaces.Mongolizable;
import org.bson.Document;

/**
 * Transaction is made when a book being reserved for a student
 * This is the demonstration class of the internal database transaction entity
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see Mongolizable
 * @since 20/06/2017
 */

public class Transaction implements Mongolizable {
    /**
     * indicates student's database object ID
     */
    private String studentObjId;
    /**
     * indicates book's database object ID
     */
    private String bookObjId;
    /**
     * time when the book is reserved, synchronized with stub's time
     */
    private long startTime;
    /**
     * time when the book should be returned, synchronized with stub's time
     */
    private long expireTime;
    /**
     * flag indicated whether the book is returned
     */
    private boolean returned;
    /**
     * flag indicates whether the transaction is overdue to expire time
     */
    private boolean expired;
    
    public Transaction(String studentObjId, String bookObjId) {
        this.studentObjId = studentObjId;
        this.bookObjId = bookObjId;
        this.returned = false;
        this.expired = false;
    }
    
    public Transaction(Document document) {
        this.studentObjId = document.getString("student");
        this.bookObjId = document.getString("book");
        this.startTime = document.getLong("start_time");
        this.expireTime = document.getLong("expired_time");
        this.returned = document.getBoolean("returned");
        this.expired = document.getBoolean("expired");
    }
    
    @Override
    public Document toMongoDocument() {
        Document transactionDoc = new Document("student", studentObjId);
        transactionDoc.put("book", bookObjId);
        transactionDoc.put("start_time", startTime);
        transactionDoc.put("expired_time", expireTime);
        transactionDoc.put("returned", returned);
        transactionDoc.put("expired", expired);
        return transactionDoc;
    }
    
    public String getStudentObjId() {
        return studentObjId;
    }
    
    public String getBookObjId() {
        return bookObjId;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
    
    public boolean isReturned() {
        return returned;
    }
    
    public boolean isExpired() {
        return expired;
    }
}
