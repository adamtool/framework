package uniolunisaar.adam.util;

import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.tools.PetriNetExtensionHandler;

/**
 *
 * @author Manuel Gieseking
 */
public class PNTools {

    public static PetriNet createPetriNet(String name) {
        PetriNet net = new PetriNet(name);
        PetriNetExtensionHandler.setProcessFamilyID(net, name + Thread.currentThread().getName());
        return net;
    }

}
