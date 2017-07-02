package ebolo.libma.data.data.raw.book;

import ebolo.libma.data.data.raw.AbstractMongolizable;
import ebolo.libma.data.data.raw.book.utils.BookLanguage;
import ebolo.libma.data.data.utils.exceptions.BookNotFound;
import ebolo.libma.data.data.utils.exceptions.WrongISBN;
import ebolo.libma.generic.keys.KeyConfigs;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A data type for demonstrating the book entity of the system's database
 *
 * @author Ebolo
 * @version 21/06/2017
 * @see AbstractMongolizable
 * @since 06/06/2017
 */

@SuppressWarnings("unchecked")
public class Book extends AbstractMongolizable {
    /**
     * Total unit of the book
     */
    private int unitTotal;
    /**
     * Number of currently available unit of the book
     */
    private int unitAvailable;
    /**
     * Period of time that this book can be borrowed
     */
    private long period;
    /**
     * The status indicates whether the book can be reserved
     */
    private boolean isAvailable;
    /**
     * Bson document that contains information about the book
     */
    private Document infoDocument;
    
    public Book(Document document) {
        this.unitTotal = document.getInteger("unit_total");
        this.unitAvailable = document.getInteger("unit_available");
        this.period = document.getLong("period");
        this.infoDocument = (Document) document.get("info_doc");
        this.isAvailable = document.getBoolean("available");
    
        if (document.getObjectId("_id") != null)
            this.objectId = document.getObjectId("_id").toString();
    }
    
    public Book(String isbn, int unitTotal, long period, boolean isAvailable) throws WrongISBN, IOException, BookNotFound {
        if (isLegalIsbn(isbn)) {
            this.unitTotal = unitTotal;
            this.unitAvailable = unitTotal;
            this.period = period;
            this.infoDocument = getOnlineInfo(isbn);
            this.isAvailable = isAvailable;
        } else
            throw new WrongISBN();
    }
    
    public Book(
        String title, String author, String publisher, String publishedDate, String description,
        String categories, String isbn10, String isbn13, String language, int pages, int unitTotal,
        int unitAvailable, long period, boolean isAvailable) throws WrongISBN {
        this.period = period;
        this.unitAvailable = unitAvailable;
        this.unitTotal = unitTotal;
        this.isAvailable = isAvailable;
        
        infoDocument = new Document("title", title);
        infoDocument.put("authors", Arrays.asList(author.split("_")));
        infoDocument.put("publisher", publisher);
        infoDocument.put("publishedDate", publishedDate);
        infoDocument.put("description", description);
        if (!isbn10.isEmpty() && !isLegalIsbn(isbn10))
            throw new WrongISBN();
        Document isbn10Doc = new Document("type", "ISBN_10");
        isbn10Doc.put("identifier", isbn10);
        if (!isbn13.isEmpty() && !isLegalIsbn(isbn13))
            throw new WrongISBN();
        Document isbn13Doc = new Document("type", "ISBN_13");
        isbn13Doc.put("identifier", isbn13);
        infoDocument.put("industryIdentifiers", Arrays.asList(isbn10Doc, isbn13Doc));
        infoDocument.put("pageCount", pages);
        infoDocument.put("categories", Arrays.asList(categories.split("_")));
        infoDocument.put("language", language);
    }
    
    /**
     * This function would help retrieve the book info from Google Book database
     *
     * @param isbn the ISBN code of the book, either ISBN10 or ISBN13
     * @return the info Bson document of the book
     * @throws WrongISBN    is thrown if the ISBN is in wrong format
     * @throws BookNotFound is thrown when the book is not found in Google Book database
     * @throws IOException  is thrown when the link is not accessible
     * @author Ebolo
     */
    
    private static Document getOnlineInfo(final String isbn) throws WrongISBN, IOException, BookNotFound {
        if (isLegalIsbn(isbn)) {
            
            // setup connection to Google book to make a GET request
            HttpURLConnection connection = (HttpURLConnection) new URL(
                "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn
                    + "&key=" + KeyConfigs.getGoogleBookApiKey()
            ).openConnection();
            
            // add request header
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            // getting the GET result
            BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = buffer.readLine()) != null) {
                response.append(inputLine);
            }
            
            buffer.close();
            connection.disconnect();
            
            // retrieve the Bson info document from the GET result
            try {
                @SuppressWarnings("unchecked")
                Document infoDoc = (Document)
                    ((List<Document>) Document
                        .parse(response.toString())
                        .get("items"))
                        .get(0)
                        .get("volumeInfo");
                return infoDoc;
            } catch (NullPointerException e) {
                // this book is not found in the Google Book database
                throw new BookNotFound();
            }
        } else
            throw new WrongISBN();
    }
    
    private static boolean isLegalIsbn(final String isbn) {
        return isbn.length() == 10 || isbn.length() == 13;
    }
    
    @Override
    public org.bson.Document toMongoDocument() {
        org.bson.Document returnDocument = new org.bson.Document("info_doc", this.infoDocument);
        returnDocument.put("unit_total", this.unitTotal);
        returnDocument.put("unit_available", this.unitAvailable);
        returnDocument.put("period", this.period);
        returnDocument.put("available", this.isAvailable);
        return returnDocument;
    }
    
    public int getUnitTotal() {
        return unitTotal;
    }
    
    public int getUnitAvailable() {
        return unitAvailable;
    }
    
    public long getPeriod() {
        return period;
    }
    
    public String getTitle() {
        return infoDocument.getString("title");
    }
    
    public String getFullTitle() {
        return infoDocument.getString("title") +
            (infoDocument.getString("subtitle") == null ? "" : " - " + infoDocument.getString("subtitle"));
    }
    
    public String getAuthors() {
        List<String> authors = (List<String>) infoDocument.get("authors");
        return authors == null ? "" : authors.parallelStream().collect(Collectors.joining(", "));
    }
    
    public List<String> getAuthorsList() {
        return (List<String>) infoDocument.get("authors");
    }
    
    public String getCategories() {
        List<String> categories = (List<String>) infoDocument.get("categories");
        return categories == null ? "" : categories
            .parallelStream()
            .map(s -> s.replace(" &", ","))
            .collect(Collectors.joining(", "));
    }
    
    public String getPublisher() {
        String publisher = infoDocument.getString("publisher");
        return publisher == null ? "" : publisher;
    }
    
    public String getPublishedDate() {
        String publishedDate = infoDocument.getString("publishedDate");
        return publishedDate == null ? "" : publishedDate;
    }
    
    public String getDescription() {
        String description = infoDocument.getString("description");
        return description == null ? "" : description;
    }
    
    public String getIsbn(int type) {
        if (type == 10 || type == 13) {
            StringBuilder isbn10 = new StringBuilder(""), isbn13 = new StringBuilder("");
            ((List<Document>) infoDocument.get("industryIdentifiers")).forEach(document -> {
                if (document.getString("type").equals("ISBN_10"))
                    isbn10.append(document.getString("identifier"));
                else
                    isbn13.append(document.getString("identifier"));
            });
            return type == 10 ? isbn10.toString() : isbn13.toString();
        }
        return "";
    }
    
    public int getPages() {
        Integer pageCount = infoDocument.getInteger("pageCount");
        return pageCount == null ? 0 : pageCount;
    }
    
    public String getLanguage() {
        String language = infoDocument.getString("language");
        return language == null ? "" : BookLanguage.getFullLangTerm(language);
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        
        Book book = (Book) o;
        
        return unitTotal == book.unitTotal &&
            unitAvailable == book.unitAvailable &&
            period == book.period &&
            isAvailable == book.isAvailable &&
            infoDocument.equals(book.infoDocument);
    }
    
    @Override
    public int hashCode() {
        int result = unitTotal;
        result = 31 * result + unitAvailable;
        result = 31 * result + (int) (period ^ (period >>> 32));
        result = 31 * result + infoDocument.hashCode();
        result = 31 * result + (isAvailable ? 1 : 0);
        return result;
    }
}
