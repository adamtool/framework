package uniolunisaar.adam.generators.pnwt.util.sdnencoding;

import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.util.SDNTools;

/**
 *
 * @author Jesko Hecking-Harbusch
 *
 */
public class SwitchUpdate implements Update {

    String sw;
    String before = null;
    String after;

    /**
     * For the case that there wasn't any forwarding rule for this switch
     * before.
     *
     * @param switchToUpdate
     * @param newDestination
     */
    public SwitchUpdate(String switchToUpdate, String newDestination) {
        sw = switchToUpdate;
        after = newDestination;
    }

    public SwitchUpdate(String switchToUpdate, String oldDestination, String newDestination) {
        sw = switchToUpdate;
        before = oldDestination;
        after = newDestination;
    }

    @Override
    public Place addUpdate(PetriNetWithTransits pn, Place start) {
        Place end = pn.createPlace();
        Transition t = pn.createTransition();
        pn.setWeakFair(t);
        pn.createFlow(start, t);
        pn.createFlow(t, end);
        if (before != null) {
            pn.createFlow(sw + SDNTools.infixActPlace + before, t.getId());
        }
        if (after != null) {
            pn.createFlow(t.getId(), sw + SDNTools.infixActPlace + after);
        }
        return end;
    }

    @Override
    public String toString() {
        return "SwitchUpdate{" + "sw=" + sw + ", before=" + before + ", after=" + after + '}';
    }
    
    
}
