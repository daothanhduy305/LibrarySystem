package ebolo.libma.data.data.utils.exceptions;

/**
 * The exception is thrown when the book is not found in the Google book database
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class BookNotFound extends Exception {
    @Override
    public void printStackTrace() {
        System.err.println("Cannot find this book!");
    }
}
