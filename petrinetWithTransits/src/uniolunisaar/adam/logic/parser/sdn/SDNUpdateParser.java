package uniolunisaar.adam.logic.parser.sdn;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.exceptions.pnwt.DocumentParseException;
import static uniolunisaar.adam.logic.parser.ParsingUtils.handleErrors;
import static uniolunisaar.adam.logic.parser.ParsingUtils.walk;
import uniolunisaar.adam.logic.parser.sdn.antlr.SDNTopologyFormatLexer;
import uniolunisaar.adam.logic.parser.sdn.antlr.SDNTopologyFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
public class SDNUpdateParser {

    public static PetriNetWithTransits parse(String input) throws ParseException {
        try {
            SDNTopologyFormatLexer lexer = new SDNTopologyFormatLexer(new ANTLRInputStream(input));

            // Get a list of matched tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Pass the tokens to the parser
            SDNTopologyFormatParser parser = new SDNTopologyFormatParser(tokens);

            handleErrors(lexer, parser, false);

            // Specify our entry point
            SDNTopologyFormatParser.TsContext context = parser.ts();

            // Walk it and attach our listener
            SDNTopologyListener listener = new SDNTopologyListener(new PetriNetWithTransits(""));
            walk(listener, context);
            return listener.getPnwt();
        } catch (DocumentParseException e) {
            throw new ParseException("Error while parsing input '" + input + "'", e);
        }
    }

}
