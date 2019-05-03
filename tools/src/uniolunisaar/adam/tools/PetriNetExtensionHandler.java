package uniolunisaar.adam.tools;

import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.util.AdamExtensions;

/**
 *
 * @author Manuel Gieseking
 */
public class PetriNetExtensionHandler {

    public static void setProcessFamilyID(PetriNet net, String id) {
        net.putExtension(AdamExtensions.processFamilyID.name(), id);
    }

    public static String getProcessFamilyID(PetriNet net) {
        return (String) net.getExtension(AdamExtensions.processFamilyID.name());
    }

    public static boolean hasProcessFamilyID(PetriNet net) {
        return net.hasExtension(AdamExtensions.processFamilyID.name());
    }
}
