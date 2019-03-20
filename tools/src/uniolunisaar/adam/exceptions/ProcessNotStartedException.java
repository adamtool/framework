package uniolunisaar.adam.exceptions;

/**
 *
 * @author Manuel Gieseking
 */
public class ProcessNotStartedException extends Exception {

    private static final long serialVersionUID = 1L;

    public ProcessNotStartedException() {
        super("Process has not been started yet.");
    }

    public ProcessNotStartedException(String message) {
        super(message);
    }

    public ProcessNotStartedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessNotStartedException(Throwable cause) {
        super(cause);
    }

    public ProcessNotStartedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
