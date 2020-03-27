package uniolunisaar.adam.exceptions.pnwt;

/**
 *
 * @author Manuel Gieseking
 */
public class DocumentParseException extends RuntimeException {

    public static final long serialVersionUID = 0x1l;

    private final int line;
    private final int column;

    public DocumentParseException(String message, int line, int column, Throwable cause) {
        super(message, cause);
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

}
