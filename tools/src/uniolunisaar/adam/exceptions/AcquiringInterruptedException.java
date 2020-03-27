package uniolunisaar.adam.exceptions;

/**
 *
 * @author Manuel Gieseking
 */
public class AcquiringInterruptedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AcquiringInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AcquiringInterruptedException(Throwable cause) {
        super(cause);
    }

    public AcquiringInterruptedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
