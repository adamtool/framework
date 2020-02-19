package uniolunisaar.adam.ds.petrinet.objectives;

import uniol.apt.adt.pn.PetriNet;

/**
 *
 * @author Manuel Gieseking
 * @param <W>
 */
public abstract class Condition<W extends Condition<W>> {

    public enum Objective {
        E_REACHABILITY,
        A_REACHABILITY,
        E_SAFETY,
        A_SAFETY,
        E_BUCHI,
        A_BUCHI,
        E_PARITY,
        A_PARITY,
        LTL
    }

    public abstract void buffer(PetriNet net);

    public abstract Objective getObjective();

//    public abstract <W extends Condition> W getCopy();
//    public abstract Condition getCopy();
    public abstract W getCopy();
}
