package uniolunisaar.adam.ds.objectives;

import java.util.HashSet;
import java.util.Set;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 *
 * @author Manuel Gieseking
 */
public class Reachability extends Condition {

    private final Set<Place> places2Reach;
    private boolean existential;

    public Reachability() {
        this(true);
    }

    public Reachability(boolean existential) {
        places2Reach = new HashSet<>();
        this.existential = existential;
    }

    public Reachability(Reachability reachability) {
        this.existential = reachability.existential;
        this.places2Reach = new HashSet<>(reachability.places2Reach);
    }

    @Override
    public void buffer(PetriNetWithTransits net) {
        for (Place place : net.getPlaces()) {
            if (net.isReach(place)) {
                places2Reach.add(place);
            }
        }
        // java 1.8
//        game.getNet().getPlaces().stream().filter((place) -> (place.hasExtension("reach"))).forEach((place) -> {
//            badPlaces.add(place);
//        });
    }

    public Set<Place> getPlaces2Reach() {
        return places2Reach;
    }

    @Override
    public Objective getObjective() {
        return existential ? Objective.E_REACHABILITY : Objective.A_REACHABILITY;
    }

    @Override
    public Reachability getCopy() {
        return new Reachability(this);
    }

}
