package ebolo.libma.commons.utils.exceptions;

/**
 * The exception is thrown when the command Bson document communicating between stub and client is in wrong format
 *
 * @author Ebolo
 * @version 08/06/2017
 * @since 08/06/2017
 */
public class CommandFormatException extends Exception {
    @Override
    public void printStackTrace() {
        System.err.println("Command format is wrong! Please check the arguments!");
    }
}