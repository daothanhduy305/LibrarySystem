package ebolo.libma.data.data.utils.exceptions;

/**
 * The exception is thrown when the ISBN is in wrong format
 *
 * @author Ebolo
 * @version 06/06/2017
 * @since 06/06/2017
 */

public class WrongISBN extends Exception {
    @Override
    public void printStackTrace() {
        System.err.println("Wrong ISBN format: 10 or 13 digits only!");
    }
}
