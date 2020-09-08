package uniolunisaar.adam.exceptions;


/**
 *
 * @author Manuel Gieseking
 */
public class NoSuitableParameterException extends Exception {

    private static final long serialVersionUID = 1L;

    public NoSuitableParameterException(Object parameter) {
        super("The given parameter (" + parameter.toString() + ") is not suitable!");
    }

    public NoSuitableParameterException(Object parameter, String message) {
        super("The given parameter (" + parameter.toString() + ") is not suitable! " + message);
    }

    public NoSuitableParameterException(String message) {
        super(message);
    }

    public NoSuitableParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuitableParameterException(Throwable cause) {
        super(cause);
    }

    public NoSuitableParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
