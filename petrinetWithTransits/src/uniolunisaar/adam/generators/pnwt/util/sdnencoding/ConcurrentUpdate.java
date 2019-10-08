package uniolunisaar.adam.generators.pnwt.util.sdnencoding;

import java.util.HashSet;
import java.util.Set;

import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 *
 * @author Jesko Hecking-Harbusch
 *
 */
public class ConcurrentUpdate implements Update {

    private final Set<Update> concurrent;

    public ConcurrentUpdate(Set<Update> setOfUpdates) {
        concurrent = setOfUpdates;
    }

    @Override
    public Place addUpdate(PetriNetWithTransits pn, Place start) {
        // split
        Transition split = pn.createTransition();
        pn.setWeakFair(split);
        pn.createFlow(start, split);
        Set<Place> merge = new HashSet<>();
        for (Update update : concurrent) {
            Place p = pn.createPlace();
            pn.createFlow(split, p);
            merge.add(update.addUpdate(pn, p));
        }
        Transition t = pn.createTransition();
        pn.setWeakFair(t);
        Place finish = pn.createPlace();
        for (Place p : merge) {
            pn.createFlow(p, t);
        }
        pn.createFlow(t, finish);
        return finish;
    }

    @Override
    public String toString() {
        return "ConcurrentUpdate{" + "concurrent=" + concurrent + '}';
    }

}
