package uniolunisaar.adam.exceptions.pnwt;

/**
 *
 * @author Manuel Gieseking
 */
public class TransitParseException extends RuntimeException {

    public static final long serialVersionUID = 0x1l;

    private final int column;

    public TransitParseException(String message, int column, Throwable cause) {
        super(message, cause);
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

}
