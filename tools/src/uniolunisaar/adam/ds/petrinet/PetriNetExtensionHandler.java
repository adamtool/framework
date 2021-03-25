package uniolunisaar.adam.ds.petrinet;

import uniol.apt.adt.extension.ExtensionProperty;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.Node;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.util.AdamExtensions;
import uniolunisaar.adam.util.ExtensionManagement;

/**
 * Used to handle the extensions for all object corresponding to Petri nets,
 * i.e, the net itself, places, transitions, and arcs.
 *
 * @author Manuel Gieseking
 */
public class PetriNetExtensionHandler {

    // register the Extensions for the framework
    static {
        ExtensionManagement.getInstance().registerExtensions(true, AdamExtensions.values());
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% NET EXTENSIONS
    public static void setProcessFamilyID(PetriNet net, String id) {
        ExtensionManagement.getInstance().putExtension(net, AdamExtensions.processFamilyID, id);
    }

    public static String getProcessFamilyID(PetriNet net) {
        return ExtensionManagement.getInstance().getExtension(net, AdamExtensions.processFamilyID, String.class);
    }

    public static boolean hasProcessFamilyID(PetriNet net) {
        return ExtensionManagement.getInstance().hasExtension(net, AdamExtensions.processFamilyID);
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% PLACE EXTENSIONS
    public static boolean hasLabel(Place place) {
        return ExtensionManagement.getInstance().hasExtension(place, AdamExtensions.label);
    }

    public static void setLabel(Place place, String label) {
        ExtensionManagement.getInstance().putExtension(place, AdamExtensions.label, label, ExtensionProperty.WRITE_TO_FILE);
    }

    public static String getLabel(Place place) {
        return ExtensionManagement.getInstance().getExtension(place, AdamExtensions.label, String.class);
    }

    public static boolean isSpecial(Place place) {
//        PetriNet net = place.getGraph();
//        if (getWinningCondition(net).equals(WinningCondition.Objective.E_SAFETY)
//                || getWinningCondition(net).equals(WinningCondition.Objective.A_SAFETY)) {
//            return place.hasExtension(AdamExtensions.bad);
//        }
//        if (getWinningCondition(net).equals(WinningCondition.Objective.E_REACHABILITY)
//                || getWinningCondition(net).equals(WinningCondition.Objective.A_REACHABILITY)) {
//            return place.hasExtension(AdamExtensions.reach);
//        }
//        if (getWinningCondition(net).equals(WinningCondition.Objective.E_BUCHI)
//                || getWinningCondition(net).equals(WinningCondition.Objective.A_BUCHI)) {
//            return place.hasExtension(AdamExtensions.buchi);
//        }
//        return false;
        return ExtensionManagement.getInstance().hasExtension(place, AdamExtensions.bad)
                || ExtensionManagement.getInstance().hasExtension(place, AdamExtensions.reach)
                || ExtensionManagement.getInstance().hasExtension(place, AdamExtensions.buchi);
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
        return ExtensionManagement.getInstance().hasExtension(place, AdamExtensions.bad);
    }

    public static void setBad(Place place) {
        ExtensionManagement.getInstance().putExtension(place, AdamExtensions.bad, true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeBad(Place place) {
        ExtensionManagement.getInstance().removeExtension(place, AdamExtensions.bad);
    }

    public static boolean isReach(Place place) {
        return ExtensionManagement.getInstance().hasExtension(place, AdamExtensions.reach);
    }

    public static void setReach(Place place) {
        ExtensionManagement.getInstance().putExtension(place, AdamExtensions.reach, true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeReach(Place place) {
        ExtensionManagement.getInstance().removeExtension(place, AdamExtensions.reach);
    }

    public static boolean isBuchi(Place place) {
        return ExtensionManagement.getInstance().hasExtension(place, AdamExtensions.buchi);
    }

    public static void setBuchi(Place place) {
        ExtensionManagement.getInstance().putExtension(place, AdamExtensions.buchi, true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeBuchi(Place place) {
        ExtensionManagement.getInstance().removeExtension(place, AdamExtensions.buchi);
    }

    public static boolean hasXCoord(Node node) {
        return ExtensionManagement.getInstance().hasExtension(node, AdamExtensions.xCoord);
    }

    public static double getXCoord(Node node) {
        return ExtensionManagement.getInstance().getExtension(node, AdamExtensions.xCoord, Double.class);
    }

    public static void setXCoord(Node node, double xcoord) {
        ExtensionManagement.getInstance().putExtension(node, AdamExtensions.xCoord, xcoord, ExtensionProperty.WRITE_TO_FILE);
    }

    public static boolean hasYCoord(Node node) {
        return ExtensionManagement.getInstance().hasExtension(node, AdamExtensions.yCoord);
    }

    public static double getYCoord(Node node) {
        return ExtensionManagement.getInstance().getExtension(node, AdamExtensions.yCoord, Double.class);
    }

    public static void setYCoord(Node node, double ycoord) {
        ExtensionManagement.getInstance().putExtension(node, AdamExtensions.yCoord, ycoord, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void clearCoords(Place place) {
        ExtensionManagement.getInstance().removeExtension(place, AdamExtensions.xCoord);
        ExtensionManagement.getInstance().removeExtension(place, AdamExtensions.yCoord);
    }

    public static boolean hasBinID(Node node) {
        return ExtensionManagement.getInstance().hasExtension(node, AdamExtensions.binID);
    }

    public static String getBinID(Node node) {
        return ExtensionManagement.getInstance().getExtension(node, AdamExtensions.binID, String.class);
    }

    public static void setBinID(Node node, String binID) {
        ExtensionManagement.getInstance().putExtension(node, AdamExtensions.binID, binID, ExtensionProperty.WRITE_TO_FILE);
    }

    public static boolean isOriginal(Node node) {
        return ExtensionManagement.getInstance().hasExtension(node, AdamExtensions.original);
    }

    public static void setOriginal(Node node) {
        ExtensionManagement.getInstance().putExtension(node, AdamExtensions.original, true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeOriginal(Node node) {
        ExtensionManagement.getInstance().removeExtension(node, AdamExtensions.original);
    }

    public static boolean hasOrigID(Node node) {
        return ExtensionManagement.getInstance().hasExtension(node, AdamExtensions.origID);
    }

    public static String getOrigID(Node node) {
        return ExtensionManagement.getInstance().getExtension(node, AdamExtensions.origID, String.class);
    }

    public static void setOrigID(Node node, String id) {
        ExtensionManagement.getInstance().putExtension(node, AdamExtensions.origID, id);
    }

    public static int getPartition(Place place) {
        return ExtensionManagement.getInstance().getExtension(place, AdamExtensions.token, Integer.class);
    }

    public static boolean hasPartition(Place place) {
        return ExtensionManagement.getInstance().hasExtension(place, AdamExtensions.token);
    }

    public static void setPartition(Place place, int partitionID) {
        ExtensionManagement.getInstance().putExtension(place, AdamExtensions.token, partitionID, ExtensionProperty.WRITE_TO_FILE);
    }

    public static long getBoundedness(Place place) {
        return ExtensionManagement.getInstance().getExtension(place, AdamExtensions.boundedness, Long.class);
    }

    public static void setBoundedness(Place place, long k) {
        ExtensionManagement.getInstance().putExtension(place, AdamExtensions.boundedness, k, ExtensionProperty.WRITE_TO_FILE, ExtensionProperty.NOCOPY);
    }

    public static boolean hasBoundedness(Place place) {
        return ExtensionManagement.getInstance().hasExtension(place, AdamExtensions.boundedness);
    }
// %%%%%%%%%%%%%%%%%%%%%%%%% TRANSITION EXTENSIONS

    public static boolean isStrongFair(Transition t) {
        return ExtensionManagement.getInstance().hasExtension(t, AdamExtensions.strongFair);
    }

    public static void setStrongFair(Transition t) {
        ExtensionManagement.getInstance().putExtension(t, AdamExtensions.strongFair, true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeStrongFair(Transition t) {
        ExtensionManagement.getInstance().removeExtension(t, AdamExtensions.strongFair);
    }

    public static boolean isWeakFair(Transition t) {
        return ExtensionManagement.getInstance().hasExtension(t, AdamExtensions.weakFair);
    }

    public static void setWeakFair(Transition t) {
        ExtensionManagement.getInstance().putExtension(t, AdamExtensions.weakFair, true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeWeakFair(Transition t) {
        ExtensionManagement.getInstance().removeExtension(t, AdamExtensions.weakFair);
    }

// %%%%%%%%%%%%%%%%%%%%%%%%%% FLOW EXTENSIONS
    public static boolean isInhibitor(Flow f) {
        return ExtensionManagement.getInstance().hasExtension(f, AdamExtensions.inhibitor);
    }

    public static void setInhibitor(Flow f) {
        ExtensionManagement.getInstance().putExtension(f, AdamExtensions.inhibitor, true, ExtensionProperty.WRITE_TO_FILE);
    }

    public static void removeInhibitor(Flow f) {
        ExtensionManagement.getInstance().removeExtension(f, AdamExtensions.inhibitor);
    }

}
