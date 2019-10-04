package uniolunisaar.adam.ds.petrinetwithtransits;

import uniol.apt.adt.extension.ExtensionProperty;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;
import uniolunisaar.adam.util.AdamExtensions;

/**
 *
 * @author Manuel Gieseking
 */
class PetriNetWithTransitsExtensionHandler extends PetriNetExtensionHandler {

    static boolean isInitialTokenflow(Place place) {
        return place.hasExtension(AdamExtensions.itfl.name());
    }

    static void setInitialTransit(Place place) {
        place.putExtension(AdamExtensions.itfl.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    static void removeInitialTransit(Place place) {
        place.removeExtension(AdamExtensions.itfl.name());
    }

    static int getPartition(Place place) {
        return (Integer) place.getExtension(AdamExtensions.token.name());
    }

    static boolean hasPartition(Place place) {
        return place.hasExtension(AdamExtensions.token.name());
    }

    static void setPartition(Place place, int token) {
        place.putExtension(AdamExtensions.token.name(), token, ExtensionProperty.WRITE_TO_FILE);
    }

    static int getID(Place place) {
        return (Integer) place.getExtension(AdamExtensions.id.name());
    }

    static void setID(Place place, int id) {
        place.putExtension(AdamExtensions.id.name(), id);
    }

    static String getOrigID(Place place) {
        return (String) place.getExtension(AdamExtensions.origID.name());
    }

    static void setOrigID(Place place, String id) {
        place.putExtension(AdamExtensions.origID.name(), id);
    }
    
    
}
