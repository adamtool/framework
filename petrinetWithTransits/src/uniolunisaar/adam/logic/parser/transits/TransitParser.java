package uniolunisaar.adam.logic.parser.transits;

import uniolunisaar.adam.exceptions.pnwt.LineParseException;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.ds.petrinetwithtransits.Transit;
import uniolunisaar.adam.logic.parser.ParsingUtils;
import uniolunisaar.adam.logic.parser.transits.antlr.TransitFormatLexer;
import uniolunisaar.adam.logic.parser.transits.antlr.TransitFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
public class TransitParser {

    public static List<Transit> parse(PetriNetWithTransits net, Transition t, String flows) throws ParseException {
        try {
            TransitFormatLexer lexer = new TransitFormatLexer(new ANTLRInputStream(flows));

            // Get a list of matched tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Pass the tokens to the parser
            TransitFormatParser parser = new TransitFormatParser(tokens);

            ParsingUtils.handleErrors(lexer, parser, true);

            // Specify our entry point
            TransitFormatParser.TflContext context = parser.tfl();

            // Walk it and attach our listener
            TransitListener listener = new TransitListener(net, t);
            ParsingUtils.walk(listener, context);
            return listener.getTokenflows();
        } catch (LineParseException e) {
            throw new ParseException("Error while parsing the tokenflow for transition '" + t.getId() + "'", e);
        }
    }
}
