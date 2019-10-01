package uniolunisaar.adam.logic.parser;

import uniolunisaar.adam.exceptions.pnwt.LineParseException;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 *
 * Adapt parse error messages
 *
 * Adaption of vsp's uniol.apt.io.parser.impl.ThrowingErrorListener of APT
 * 
 * @author Manuel Gieseking
 */
public class OneLineParseExceptionListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        throw new LineParseException("col " + charPositionInLine + ": " + msg, charPositionInLine, e);
    }
}
