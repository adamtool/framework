package uniolunisaar.adam.logic.transformers.petrinet;

import uniol.apt.adt.pn.PetriNet;

/**
 *
 * @author Manuel Gieseking
 */
public class PN2Dot {

    public static String get(PetriNet net, boolean withLabel, boolean withOrigPlaces) {
        return new PetriNet2DotRenderer<>().render(net, withLabel, withOrigPlaces);
    }

    public static String get(PetriNet net, boolean withLabel, boolean withOrigPlaces, Integer nb_partitions) {
        return new PetriNet2DotRenderer<>().render(net, withLabel, withOrigPlaces, nb_partitions);
    }
}
