package uniolunisaar.adam.exceptions;

/**
 *
 * @author Manuel Gieseking
 */
public class InconsistencyException extends RuntimeException {

    public static final long serialVersionUID = 0x1l;

    public InconsistencyException(String message) {
        super(message);
    }

    public InconsistencyException(String message, Throwable cause) {
        super(message, cause);
    }

}
