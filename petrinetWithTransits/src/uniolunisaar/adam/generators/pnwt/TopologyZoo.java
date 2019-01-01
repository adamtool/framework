package uniolunisaar.adam.generators.pnwt;

import java.io.File;
import java.io.IOException;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.TopologyToPN;

/**
 *
 * @author Manuel Gieseking
 */
public class TopologyZoo {

    public static PetriNetWithTransits createTopologyFromFile(String path) throws ParseException, IOException {
        TopologyToPN ttp = new TopologyToPN(new File(path));
        PetriNetWithTransits pn = ttp.generatePetriNet();
        ttp.setUpdate(pn);
        return pn;
    }

}
