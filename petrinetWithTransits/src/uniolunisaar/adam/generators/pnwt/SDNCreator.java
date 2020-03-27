package uniolunisaar.adam.generators.pnwt;

import uniol.apt.adt.pn.Place;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.Update;
import uniolunisaar.adam.logic.parser.sdn.SDNTopologyParser;
import uniolunisaar.adam.logic.parser.sdn.SDNUpdateParser;
import uniolunisaar.adam.util.SDNTools;

/**
 *
 * @author Manuel Gieseking
 */
public class SDNCreator {

    public static PetriNetWithTransits parse(String topology, String update, boolean optimized) throws ParseException {
        PetriNetWithTransits top = SDNTopologyParser.parse(topology, optimized);
        Place updateStart = top.createPlace(SDNTools.updateStartID);
        updateStart.setInitialToken(1);
        Update up = SDNUpdateParser.parse(top, update, optimized);
        up.addUpdate(top, updateStart);
        return top;
    }
}
