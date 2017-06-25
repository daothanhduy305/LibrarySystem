package ebolo.libma.data.data.ui.book;

import ebolo.libma.data.data.raw.book.Book;
import ebolo.libma.data.data.ui.ObjectUIWrapper;
import javafx.beans.property.*;

/**
 * A UI wrapper class for raw <code>Book</code> data type. All of its fields are properties that can be bound to JavaFX
 * UI components, and have the value from the host object.
 *
 * @author Ebolo
 * @version 15/06/2017
 * @see Book
 * @see ObjectUIWrapper
 * @since 06/06/2017
 */

public class BookUIWrapper extends ObjectUIWrapper<Book> {
    private StringProperty title, isbn10, isbn13, author, publisher, publishedDate, description, categories, language;
    private IntegerProperty unitTotal, unitAvailable, pages;
    private LongProperty period;
    private BooleanProperty availability;
    
    public BookUIWrapper(Book book, String objectId) {
        super(book, objectId);
        
        title = new SimpleStringProperty(book.getTitle());
        isbn10 = new SimpleStringProperty(book.getIsbn(10));
        isbn13 = new SimpleStringProperty(book.getIsbn(13));
        author = new SimpleStringProperty(book.getAuthors());
        publisher = new SimpleStringProperty(book.getPublisher());
        publishedDate = new SimpleStringProperty(book.getPublishedDate());
        description = new SimpleStringProperty(book.getDescription());
        categories = new SimpleStringProperty(book.getCategories());
        language = new SimpleStringProperty(book.getLanguage());
        
        unitTotal = new SimpleIntegerProperty(book.getUnitTotal());
        unitAvailable = new SimpleIntegerProperty(book.getUnitAvailable());
        pages = new SimpleIntegerProperty(book.getPages());
        
        period = new SimpleLongProperty(book.getPeriod());
        
        availability = new SimpleBooleanProperty(book.isAvailable());
    }
    
    public String getTitle() {
        return title.get();
    }
    
    public StringProperty titleProperty() {
        return title;
    }
    
    public String getIsbn10() {
        return isbn10.get();
    }
    
    public StringProperty isbn10Property() {
        return isbn10;
    }
    
    public String getIsbn13() {
        return isbn13.get();
    }
    
    public StringProperty isbn13Property() {
        return isbn13;
    }
    
    public String getAuthor() {
        return author.get();
    }
    
    public StringProperty authorProperty() {
        return author;
    }
    
    public String getPublisher() {
        return publisher.get();
    }
    
    public StringProperty publisherProperty() {
        return publisher;
    }
    
    public String getPublishedDate() {
        return publishedDate.get();
    }
    
    public StringProperty publishedDateProperty() {
        return publishedDate;
    }
    
    public String getDescription() {
        return description.get();
    }
    
    public String getCategories() {
        return categories.get();
    }
    
    public StringProperty categoriesProperty() {
        return categories;
    }
    
    public String getLanguage() {
        return language.get();
    }
    
    public StringProperty languageProperty() {
        return language;
    }
    
    public int getUnitTotal() {
        return unitTotal.get();
    }
    
    public int getUnitAvailable() {
        return unitAvailable.get();
    }
    
    public int getPages() {
        return pages.get();
    }
    
    public long getPeriod() {
        return period.get();
    }
    
    public boolean isAvailability() {
        return availability.get();
    }
    
    @Override
    public void update(Book book) {
        title.set(book.getTitle());
        isbn10.set(book.getIsbn(10));
        isbn13.set(book.getIsbn(13));
        author.set(book.getAuthors());
        publisher.set(book.getPublisher());
        publishedDate.set(book.getPublishedDate());
        description.set(book.getDescription());
        categories.set(book.getCategories());
        language.set(book.getLanguage());
        
        unitTotal.set(book.getUnitTotal());
        unitAvailable.set(book.getUnitAvailable());
        pages.set(book.getPages());
        
        period.set(book.getPeriod());
        
        availability.set(book.isAvailable());
    }
}
