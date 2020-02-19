package uniolunisaar.adam.ds.petrinet.objectives;

import java.util.HashSet;
import java.util.Set;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;

/**
 *
 * @author Manuel Gieseking
 */
public class Safety extends Condition<Safety> {

    private final Set<Place> badPlaces;
    private boolean existential;

    public Safety() {
        this(true);
    }

    public Safety(boolean existential) {
        badPlaces = new HashSet<>();
        this.existential = existential;
    }

    public Safety(Safety safety) {
        this.existential = safety.existential;
        this.badPlaces = new HashSet<>(safety.badPlaces);
    }

    @Override
    public void buffer(PetriNet net) {
        for (Place place : net.getPlaces()) {
            if (PetriNetExtensionHandler.isBad(place)) {
                badPlaces.add(place);
            }
        }
        // java 1.8
//        game.getNet().getPlaces().stream().filter((place) -> (place.hasExtension("bad"))).forEach((place) -> {
//            badPlaces.add(place);
//        });
    }

    public Set<Place> getBadPlaces() {
        return badPlaces;
    }

    public void setExistential(boolean existential) {
        this.existential = existential;
    }

    @Override
    public Objective getObjective() {
        return existential ? Objective.E_SAFETY : Objective.A_SAFETY;
    }

    @Override
    public Safety getCopy() {
        return new Safety(this);
    }

}
