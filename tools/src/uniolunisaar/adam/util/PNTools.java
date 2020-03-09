package uniolunisaar.adam.util;

import java.util.HashMap;
import java.util.Map;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.Marking;
import uniol.apt.adt.pn.Node;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
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

    /**
     * Creates a PetriNet which has automatically named nodes and the original
     * ids in the label of the node. This can be used to create a net which is
     * definitely readably be the APT parser.
     *
     * @param net
     * @return
     */
    public static PetriNet createPetriNetWithIDsInLabel(PetriNet net) {
        PetriNet out = new PetriNet(net.getName());
        addElementsForPetriNetWithIDsInLabel(net, out);
        return out;
    }

    public static Map<Node, Node> addElementsForPetriNetWithIDsInLabel(PetriNet net, PetriNet out) {
        Map<Node, Node> mapping = new HashMap<>();
        for (Place place : net.getPlaces()) {
            Place p = out.createPlace();
            p.copyExtensions(place);
            PetriNetExtensionHandler.setLabel(p, place.getId());
            mapping.put(place, p);
        }
        for (Transition transition : net.getTransitions()) {
            Transition t = out.createTransition();
            t.copyExtensions(transition);
            t.setLabel(transition.getId());
            mapping.put(transition, t);
        }
        for (Flow edge : net.getEdges()) {
            Flow f = out.createFlow(mapping.get(edge.getSource()), mapping.get(edge.getTarget()), edge.getWeight());
            f.copyExtensions(edge);
        }

        for (Marking m : net.getFinalMarkings()) {
            out.addFinalMarking(new Marking(out, m));
        }
        out.setInitialMarking(new Marking(out, net.getInitialMarking()));
        out.copyExtensions(net);
        return mapping;
    }

}
