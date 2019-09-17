package uniolunisaar.adam.ds.petrinetwithtransits;

import uniol.apt.adt.extension.ExtensionProperty;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.Node;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.util.AdamExtensions;

/**
 *
 * @author Manuel Gieseking
 */
class PetriNetWithTransitsExtensionHandler {

// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PLACE EXTENSIONS
    static boolean isSpecial(Place place) {
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

    static void setSpecial(Place place, Condition.Objective con) {
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

    static void removeSpecial(Place place, Condition.Objective con) {
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

    static boolean isBad(Place place) {
        return place.hasExtension(AdamExtensions.bad.name());
    }

    static void setBad(Place place) {
        place.putExtension(AdamExtensions.bad.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    static void removeBad(Place place) {
        place.removeExtension(AdamExtensions.bad.name());
    }

    static boolean isReach(Place place) {
        return place.hasExtension(AdamExtensions.reach.name());
    }

    static void setReach(Place place) {
        place.putExtension(AdamExtensions.reach.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    static void removeReach(Place place) {
        place.removeExtension(AdamExtensions.reach.name());
    }

    static boolean isBuchi(Place place) {
        return place.hasExtension(AdamExtensions.buchi.name());
    }

    static void setBuchi(Place place) {
        place.putExtension(AdamExtensions.buchi.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    static void removeBuchi(Place place) {
        place.removeExtension(AdamExtensions.buchi.name());
    }

    static boolean isInitialTokenflow(Place place) {
        return place.hasExtension(AdamExtensions.itfl.name());
    }

    static void setInitialTransit(Place place) {
        place.putExtension(AdamExtensions.itfl.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    static void removeInitialTransit(Place place) {
        place.removeExtension(AdamExtensions.itfl.name());
    }

    static int getPartition(Place place) {
        return (Integer) place.getExtension(AdamExtensions.token.name());
    }

    static boolean hasPartition(Place place) {
        return place.hasExtension(AdamExtensions.token.name());
    }

    static void setPartition(Place place, int token) {
        place.putExtension(AdamExtensions.token.name(), token, ExtensionProperty.WRITE_TO_FILE);
    }

    static int getID(Place place) {
        return (Integer) place.getExtension(AdamExtensions.id.name());
    }

    static void setID(Place place, int id) {
        place.putExtension(AdamExtensions.id.name(), id);
    }

    static String getOrigID(Place place) {
        return (String) place.getExtension(AdamExtensions.origID.name());
    }

    static void setOrigID(Place place, String id) {
        place.putExtension(AdamExtensions.origID.name(), id);
    }

    static boolean hasXCoord(Node node) {
        return node.hasExtension(AdamExtensions.xCoord.name());
    }

    static double getXCoord(Node node) {
        return (Double) node.getExtension(AdamExtensions.xCoord.name());
    }

    static void setXCoord(Node node, double id) {
        node.putExtension(AdamExtensions.xCoord.name(), id, ExtensionProperty.WRITE_TO_FILE);
    }

    static boolean hasYCoord(Node node) {
        return node.hasExtension(AdamExtensions.yCoord.name());
    }

    static double getYCoord(Node node) {
        return (Double) node.getExtension(AdamExtensions.yCoord.name());
    }

    static void setYCoord(Node node, double id) {
        node.putExtension(AdamExtensions.yCoord.name(), id, ExtensionProperty.WRITE_TO_FILE);
    }

// %%%%%%%%%%%%%%%%%%%%%%%%% TRANSITION EXTENSIONS
    static boolean isStrongFair(Transition t) {
        return t.hasExtension(AdamExtensions.strongFair.name());
    }

    static void setStrongFair(Transition t) {
        t.putExtension(AdamExtensions.strongFair.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    static void removeStrongFair(Transition t) {
        t.removeExtension(AdamExtensions.strongFair.name());
    }

    static boolean isWeakFair(Transition t) {
        return t.hasExtension(AdamExtensions.weakFair.name());
    }

    static void setWeakFair(Transition t) {
        t.putExtension(AdamExtensions.weakFair.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    static void removeWeakFair(Transition t) {
        t.removeExtension(AdamExtensions.weakFair.name());
    }

// %%%%%%%%%%%%%%%%%%%%%%%%%% FLOW EXTENSIONS
    static boolean isInhibitor(Flow f) {
        return f.hasExtension(AdamExtensions.inhibitor.name());
    }

    static void setInhibitor(Flow f) {
        f.putExtension(AdamExtensions.inhibitor.name(), true, ExtensionProperty.WRITE_TO_FILE);
    }

    static void removeInhibitor(Flow f) {
        f.removeExtension(AdamExtensions.inhibitor.name());
    }
}
