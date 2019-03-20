package uniolunisaar.adam.logic.parser.transits;

import uniolunisaar.adam.exceptions.pnwt.TransitParseException;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.ds.petrinetwithtransits.Transit;
import uniolunisaar.adam.logic.parser.transits.antlr.TransitFormatLexer;
import uniolunisaar.adam.logic.parser.transits.antlr.TransitFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
public class TransitParser {

    public static List<Transit> parse(PetriNetWithTransits net, Transition t, String flows) throws ParseException {
        try {
            TransitParseExceptionListener errorlist = new TransitParseExceptionListener();
            TransitFormatLexer lexer = new TransitFormatLexer(new ANTLRInputStream(flows));
            lexer.removeErrorListeners(); // don't spam on stderr
            lexer.addErrorListener(errorlist);

            // Get a list of matched tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Pass the tokens to the parser
            TransitFormatParser parser = new TransitFormatParser(tokens);
            parser.removeErrorListeners(); // don't spam on stderr
            parser.addErrorListener(errorlist);

            // Specify our entry point
            TransitFormatParser.TflContext context = parser.tfl();

            // Walk it and attach our listener
            ParseTreeWalker walker = new ParseTreeWalker();
            TransitListener listener = new TransitListener(net, t);
            walker.walk(listener, context);
            return listener.getTokenflows();
        } catch (TransitParseException e) {
            throw new ParseException("Error while parsing the tokenflow for transition '" + t.getId() + "'", e);
        }
    }
}
