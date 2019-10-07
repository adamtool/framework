package uniolunisaar.adam.logic.parser.sdn;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.exceptions.pnwt.LineParseException;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.Update;
import static uniolunisaar.adam.logic.parser.ParsingUtils.handleErrors;
import static uniolunisaar.adam.logic.parser.ParsingUtils.walk;
import uniolunisaar.adam.logic.parser.sdn.antlr.SDNUpdateFormatLexer;
import uniolunisaar.adam.logic.parser.sdn.antlr.SDNUpdateFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
public class SDNUpdateParser {

    public static Update parse(PetriNetWithTransits pnwt, String input, boolean optimized) throws ParseException {
        try {
            SDNUpdateFormatLexer lexer = new SDNUpdateFormatLexer(new ANTLRInputStream(input));

            // Get a list of matched tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Pass the tokens to the parser
            SDNUpdateFormatParser parser = new SDNUpdateFormatParser(tokens);

            handleErrors(lexer, parser, true);

            // Specify our entry point
            SDNUpdateFormatParser.ResultContext context = parser.result();

            // Walk it and attach our listener
            if (optimized) {
                SDNUpdateListenerOptimized listener = new SDNUpdateListenerOptimized(pnwt);
                walk(listener, context);
                return listener.getUpdate();
            } else {
                SDNUpdateListener listener = new SDNUpdateListener(pnwt);
                walk(listener, context);
                return listener.getUpdate();
            }
        } catch (LineParseException e) {
            throw new ParseException("Error while parsing the update '" + input + "'", e);
        }
    }

}
