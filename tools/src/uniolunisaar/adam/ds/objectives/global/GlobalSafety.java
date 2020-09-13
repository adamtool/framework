package uniolunisaar.adam.ds.objectives.global;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import uniol.apt.adt.pn.Marking;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.objectives.Condition;

/**
 *
 * @author Manuel Gieseking
 */
public class GlobalSafety extends Condition<GlobalSafety> {

    private final List<List<Place>> markings = new ArrayList<>();

    public GlobalSafety() {
    }

    public GlobalSafety(GlobalSafety win) {
        for (List<Place> marking : win.markings) {
            markings.add(new ArrayList<>(marking));
        }
    }

    @Override
    public void buffer(PetriNet net) {
        // the final markings of the net are abused to save the bad markings
        // to easily be able to use the APT parser
        Set<Marking> finalMarkings = net.getFinalMarkings();
        for (Marking finalMarking : finalMarkings) {
            List<Place> marking = new ArrayList<>();
            for (Place place : net.getPlaces()) {
                if (finalMarking.getToken(place).getValue() > 0) {
                    marking.add(place);
                }
            }
            markings.add(marking);
        }
    }

    @Override
    public Objective getObjective() {
        return Objective.GLOBAL_SAFETY;
    }

    @Override
    public GlobalSafety getCopy() {
        return new GlobalSafety(this);
    }

    @Override
    public GlobalSafety newObject() {
        return new GlobalSafety();
    }

    public List<List<Place>> getMarkings() {
        return markings;
    }

}
