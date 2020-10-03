package uniolunisaar.adam.util;

import uniol.apt.adt.IGraph;
import uniol.apt.adt.IGraphListener;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.Node;
import uniol.apt.adt.pn.PetriNet;

/**
 * This class is used to check and store all preconditions of a Petri net. It
 * adds itself as a listener to the Petri net to get informed about changes to
 * newly calculate the stored properties.
 *
 * No preconditions are checked in this checker.
 *
 * @author Manuel Gieseking
 */
public class PreconditionChecker implements IGraphListener<PetriNet, Flow, Node> {

    private final PetriNet net;
    private boolean isChanged = true;

    public PreconditionChecker(PetriNet net) {
        this.net = net;
        this.net.addListener(this);
    }

    public boolean check() throws Exception {
        return true;
    }

    public boolean isIsChanged() {
        return isChanged;
    }

    @Override
    public boolean changeOccurred(IGraph<PetriNet, Flow, Node> graph) {
        isChanged = true;
        return true;
    }

    public PetriNet getNet() {
        return net;
    }

}
