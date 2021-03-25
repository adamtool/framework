package uniolunisaar.adam.logic.transformers.pnwt;

import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 *
 * @author Manuel Gieseking
 */
public class PNWT2Dot {

    public static String get(PetriNetWithTransits net, boolean withLabel, boolean withOrigPlaces) {
        return new PNWT2DotRenderer<>().render(net, withLabel, withOrigPlaces);
    }

    public static String get(PetriNetWithTransits net, boolean withLabel, boolean withOrigPlaces, Integer nb_partitions) {
        return new PNWT2DotRenderer<>().render(net, withLabel, withOrigPlaces, nb_partitions);
    }
}
