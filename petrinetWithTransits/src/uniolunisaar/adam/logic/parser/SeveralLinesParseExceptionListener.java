package uniolunisaar.adam.logic.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import uniolunisaar.adam.exceptions.pnwt.DocumentParseException;

/**
 *
 * Adapt parse error messages
 *
 * Adaption of vsp's uniol.apt.io.parser.impl.ThrowingErrorListener of APT
 *
 * @author Manuel Gieseking
 */
public class SeveralLinesParseExceptionListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        throw new DocumentParseException("line: " + line + " col " + charPositionInLine + ": " + msg, line, charPositionInLine, e);
    }
}
