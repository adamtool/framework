package uniolunisaar.adam.logic.transformers.petrinet;

import uniol.apt.adt.pn.PetriNet;

/**
 *
 * @author Manuel Gieseking
 */
public class PN2Tikz {

    public static String get(PetriNet net) {
        return new PetriNet2TikzRenderer<>().renderFromCoordinates(net);
    }
}
