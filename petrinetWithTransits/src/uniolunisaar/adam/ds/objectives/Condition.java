package uniolunisaar.adam.ds.objectives;

import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 *
 * @author Manuel Gieseking
 */
public abstract class Condition {

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

    public abstract void buffer(PetriNetWithTransits net);

    public abstract Objective getObjective();

    public abstract <W extends Condition> W getCopy();
}
