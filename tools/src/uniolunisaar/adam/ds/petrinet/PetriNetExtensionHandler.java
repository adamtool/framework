package uniolunisaar.adam.ds.petrinet;

import uniol.apt.adt.extension.ExtensionProperty;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.Node;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.util.AdamExtensions;

/**
 *
 * @author Manuel Gieseking
 */
public class PetriNetExtensionHandler {

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% NET EXTENSIONS
    public static void setProcessFamilyID(PetriNet net, String id) {
        net.putExtension(AdamExtensions.processFamilyID.name(), id);
    }

    public static String getProcessFamilyID(PetriNet net) {
        return (String) net.getExtension(AdamExtensions.processFamilyID.name());
    }

    public static boolean hasProcessFamilyID(PetriNet net) {
        return net.hasExtension(AdamExtensions.processFamilyID.name());
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PLACE EXTENSIONS
    public static boolean hasLabel(Place place) {
        return place.hasExtension(AdamExtensions.label.name());
    }

    public static void setLabel(Place place, String label) {
        place.putExtension(AdamExtensions.label.name(), label, ExtensionProperty.WRITE_TO_FILE);
    }

    public static String getLabel(Place place) {
        return (String) place.getExtension(AdamExtensions.label.name());
    }

    public static boolean isSpecial(Place place) {
//        PetriNet net = place.getGraph();
//        if (getWinningCondition(net).equals(WinningCondition.Objective.E_SAFETY)
//                || getWinningCondition(net).equals(WinningCondition.Objective.A_SAFETY)) {
//            return place.hasExtension(AdamExtensions.bad.name());
//        }
//        if (getWinningCondition(net).equals(WinningCondition.Objective.E_REACHABILITY)
//                || getWinningCondition(net).equals(WinningCondition.Objective.A_REACHABILITY)) {
//            return place.hasExtension(AdamExtensions.reach.name());
//        }
//        if (getWinningCondition(net).equals(WinningCondition.Objective.E_BUCHI)
//                || getWinningCondition(net).equals(WinningCondition.Objective.A_BUCHI)) {
//            return place.hasExtension(AdamExtensions.buchi.name());
//        }
//        return false;
        return place.hasExtension(AdamExtensions.bad.name())
                || place.hasExtension(AdamExtensions.reach.name())
                || place.hasExtension(AdamExtensions.buchi.name());
    }

    public static void setSpecial(Place place, Condition.Objective con) {
        switch (con) {
            case A_SAFETY:
            case E_SAFETY:
                setBad(place);
                break;
            case A_REACHABILITY:
            case E_REACHABILITY:
                setReach(place);
                break;
            case A_BUCHI:
            case E_BUCHI:
                setBuchi(place);
                break;
        }
    }

    public static void removeSpecial(Place place, Condition.Objective con) {
        switch (con) {
            case A_SAFETY:
            case E_SAFETY:
                removeBad(place);
                break;
            case A_REACHABILITY:
            case E_REACHABILITY:
                removeReach(place);
                break;
            case A_BUCHI:
            case E_BUCHI:
                removeBuchi(place);
                break;
        }
    }

    public static boolean isBad(Place place) {
        return place.hasExtension(AdamExtensions.bad.name());
    }

    public static void setBad(Place place) {
        place.putExtension(AdamExtensions.bad.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeBad(Place place) {
        place.removeExtension(AdamExtensions.bad.name());
    }

    public static boolean isReach(Place place) {
        return place.hasExtension(AdamExtensions.reach.name());
    }

    public static void setReach(Place place) {
        place.putExtension(AdamExtensions.reach.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeReach(Place place) {
        place.removeExtension(AdamExtensions.reach.name());
    }

    public static boolean isBuchi(Place place) {
        return place.hasExtension(AdamExtensions.buchi.name());
    }

    public static void setBuchi(Place place) {
        place.putExtension(AdamExtensions.buchi.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeBuchi(Place place) {
        place.removeExtension(AdamExtensions.buchi.name());
    }

    public static boolean hasXCoord(Node node) {
        return node.hasExtension(AdamExtensions.xCoord.name());
    }

    public static double getXCoord(Node node) {
        return (Double) node.getExtension(AdamExtensions.xCoord.name());
    }

    public static void setXCoord(Node node, double id) {
        node.putExtension(AdamExtensions.xCoord.name(), id, ExtensionProperty.WRITE_TO_FILE);
    }

    public static boolean hasYCoord(Node node) {
        return node.hasExtension(AdamExtensions.yCoord.name());
    }

    public static double getYCoord(Node node) {
        return (Double) node.getExtension(AdamExtensions.yCoord.name());
    }

    public static void setYCoord(Node node, double id) {
        node.putExtension(AdamExtensions.yCoord.name(), id, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void clearCoords(Place place) {
        place.removeExtension(AdamExtensions.xCoord.name());
        place.removeExtension(AdamExtensions.yCoord.name());
    }

    public static boolean hasBinID(Node node) {
        return node.hasExtension(AdamExtensions.binID.name());
    }

    public static String getBinID(Node node) {
        return (String) node.getExtension(AdamExtensions.binID.name());
    }

    public static void setBinID(Node node, String binID) {
        node.putExtension(AdamExtensions.binID.name(), binID, ExtensionProperty.WRITE_TO_FILE);
    }

    public static boolean isOriginal(Node node) {
        return node.hasExtension(AdamExtensions.original.name());
    }

    public static void setOriginal(Node node) {
        node.putExtension(AdamExtensions.original.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeOriginal(Node node) {
        node.removeExtension(AdamExtensions.original.name());
    }

    public static boolean hasOrigID(Place place) {
        return place.hasExtension(AdamExtensions.origID.name());
    }

    public static String getOrigID(Place place) {
        return (String) place.getExtension(AdamExtensions.origID.name());
    }

    public static void setOrigID(Place place, String id) {
        place.putExtension(AdamExtensions.origID.name(), id);
    }

    public static int getPartition(Place place) {
        return (Integer) place.getExtension(AdamExtensions.token.name());
    }

    public static boolean hasPartition(Place place) {
        return place.hasExtension(AdamExtensions.token.name());
    }

    public static void setPartition(Place place, int token) {
        place.putExtension(AdamExtensions.token.name(), token, ExtensionProperty.WRITE_TO_FILE);
    }
// %%%%%%%%%%%%%%%%%%%%%%%%% TRANSITION EXTENSIONS

    public static boolean isStrongFair(Transition t) {
        return t.hasExtension(AdamExtensions.strongFair.name());
    }

    public static void setStrongFair(Transition t) {
        t.putExtension(AdamExtensions.strongFair.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeStrongFair(Transition t) {
        t.removeExtension(AdamExtensions.strongFair.name());
    }

    public static boolean isWeakFair(Transition t) {
        return t.hasExtension(AdamExtensions.weakFair.name());
    }

    public static void setWeakFair(Transition t) {
        t.putExtension(AdamExtensions.weakFair.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeWeakFair(Transition t) {
        t.removeExtension(AdamExtensions.weakFair.name());
    }

// %%%%%%%%%%%%%%%%%%%%%%%%%% FLOW EXTENSIONS
    public static boolean isInhibitor(Flow f) {
        return f.hasExtension(AdamExtensions.inhibitor.name());
    }

    public static void setInhibitor(Flow f) {
        f.putExtension(AdamExtensions.inhibitor.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeInhibitor(Flow f) {
        f.removeExtension(AdamExtensions.inhibitor.name());
    }

}
