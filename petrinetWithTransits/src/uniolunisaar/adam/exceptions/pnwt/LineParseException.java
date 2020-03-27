package uniolunisaar.adam.exceptions.pnwt;

/**
 *
 * @author Manuel Gieseking
 */
public class LineParseException extends RuntimeException {

    public static final long serialVersionUID = 0x1l;

    private final int column;

    public LineParseException(String message, int column, Throwable cause) {
        super(message, cause);
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

}
