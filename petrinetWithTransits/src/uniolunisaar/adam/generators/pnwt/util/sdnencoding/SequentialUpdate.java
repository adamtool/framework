package uniolunisaar.adam.generators.pnwt.util.sdnencoding;

import java.util.List;

import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 *
 * @author Jesko Hecking-Harbusch
 *
 */
public class SequentialUpdate implements Update {

    private final List<Update> sequential;

    public SequentialUpdate(List<Update> listOfUpdates) {
        sequential = listOfUpdates;
    }

    @Override
    public Place addUpdate(PetriNetWithTransits pn, Place start) {
        for (Update update : sequential) {
            start = update.addUpdate(pn, start);
        }
        return start;
    }

    @Override
    public String toString() {
        return "SequentialUpdate{" + "sequential=" + sequential + '}';
    }

}
