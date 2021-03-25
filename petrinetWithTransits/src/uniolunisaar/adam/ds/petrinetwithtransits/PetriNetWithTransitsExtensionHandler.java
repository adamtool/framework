package uniolunisaar.adam.ds.petrinetwithtransits;

import uniol.apt.adt.extension.ExtensionProperty;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;
import uniolunisaar.adam.util.AdamPNWTExtensions;
import uniolunisaar.adam.util.ExtensionManagement;

/**
 *
 * @author Manuel Gieseking
 */
class PetriNetWithTransitsExtensionHandler extends PetriNetExtensionHandler {

    // register the Extensions for the framework
    static {
        ExtensionManagement.getInstance().registerExtensions(true, AdamPNWTExtensions.values());
    }

    static boolean isInitialTokenflow(Place place) {
        return ExtensionManagement.getInstance().hasExtension(place, AdamPNWTExtensions.itfl);
    }

    static void setInitialTransit(Place place) {
        ExtensionManagement.getInstance().putExtension(place, AdamPNWTExtensions.itfl, true, ExtensionProperty.WRITE_TO_FILE);
    }

    static void removeInitialTransit(Place place) {
        ExtensionManagement.getInstance().removeExtension(place, AdamPNWTExtensions.itfl);
    }

    static int getID(Place place) {
        return ExtensionManagement.getInstance().getExtension(place, AdamPNWTExtensions.id, Integer.class);
    }

    static void setID(Place place, int id) {
        ExtensionManagement.getInstance().putExtension(place, AdamPNWTExtensions.id, id);
    }

}
