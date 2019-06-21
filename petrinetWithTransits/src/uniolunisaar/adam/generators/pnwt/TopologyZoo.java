package uniolunisaar.adam.generators.pnwt;

import java.io.File;
import java.io.IOException;
import uniol.apt.adt.extension.ExtensionProperty;
import uniol.apt.adt.pn.Place;
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
        // calculate the maximal number of switches.
        // All switch names have the format swXXX
        int max = 0;
        for (Place place : pn.getPlaces()) {
            String id = place.getId();
            if (id.startsWith("sw")) {
                int nb = Integer.parseInt(id.substring(2));
                if (nb > max) {
                    max = nb;
                }
            }
        }
        pn.putExtension("nb_switches", max, ExtensionProperty.WRITE_TO_FILE);
        return pn;
    }

}
