package uniolunisaar.adam.util;

import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;

/**
 *
 * @author Manuel Gieseking
 */
public class PNTools {

    public static void annotateProcessFamilyID(PetriNet net) {
        PetriNetExtensionHandler.setProcessFamilyID(net, net.getName() + Thread.currentThread().getName());
    }

    public static PetriNet createPetriNet(String name) {
        PetriNet net = new PetriNet(name);
        PetriNetExtensionHandler.setProcessFamilyID(net, name + Thread.currentThread().getName());
        return net;
    }

}
