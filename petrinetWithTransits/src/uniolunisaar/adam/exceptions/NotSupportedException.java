package uniolunisaar.adam.exceptions;

/**
 *
 * Super class for all problems concerning not the right class 
 *
 * @author Manuel Gieseking
 */
public class NotSupportedException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotSupportedException(String message) {
        super(message);
    }

}
