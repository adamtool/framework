package uniolunisaar.adam.util;

/**
 * This enum contains all by ADAM used keys for saving object via the Extendable
 * interface of APT (PetriNet, Place, Transition, Graph, State, etc.).
 *
 * ATTENTION: Do not use them directly to add or get any property.
 * For everything there are specific methods within the belonging classes.
 * If you add some key, also provide suitable methods in the suitable classes
 * to access the extensions.
 * 
 * @author Manuel Gieseking
 */
public enum AdamExtensions {
    condition,
    winningCondition, // todo: this is only for the fallback to the just-sythesis-version.
    partialObservation,
    label,
    env,
    token,
    bad,
    reach,
    buchi,
    origID,
    id,
    strat_t,
    t,
    tfl,
    itfl,
    n,
    b,
    xCoord,
    yCoord,
    strongFair,
    weakFair,
    inhibitor,
    colorDomain,
    colorTokens,
    predicate,
    arcExpression,
    convOrigID,
    convColors,
    convValuation,
    processFamilyID,
    original,
    binID
}
