package uniolunisaar.adam.util;

/**
 * This enum contains all by the framework used keys for saving object via the
 * Extensible interface of APT (PetriNet, Place, Transition, Flow).
 *
 * ATTENTION: Do not use them directly to add or get any property. Use the
 * uniolunisaar.adam.util.ExtensionManagement if you want to add and use your
 * own extensions. For the provided extensions there are specific methods within
 * the the class PetriNetExtensionHandler. For submodules the idea is to have
 * their own ExtensionHandler, which registers the own extensions and collects
 * the methods calling the ExtensionManagement.
 *
 * @author Manuel Gieseking
 */
public enum AdamExtensions implements IAdamExtensions {
    partialObservation,
    label,
    token,
    bad,
    reach,
    buchi,
    origID,
    xCoord,
    yCoord,
    strongFair,
    weakFair,
    inhibitor,
    boundedness,
    processFamilyID,
    original,
    binID
}
