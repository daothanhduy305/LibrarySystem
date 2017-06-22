package ebolo.libma.commons.utils.exceptions;

/**
 * The exception is thrown when the authenticating process failed
 *
 * @author Ebolo
 * @version 08/06/2017
 * @since 08/06/2017
 */

public class AuthenticationException extends Exception {
    @Override
    public void printStackTrace() {
        System.err.println("Wrong identity! Please check username and password!");
    }
}