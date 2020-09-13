package uniolunisaar.adam.ds.objectives.local;

import java.util.HashSet;
import java.util.Set;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;

/**
 *
 * @author Manuel Gieseking
 */
public class Reachability extends Condition<Reachability> {

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
    public void buffer(PetriNet net) {
        places2Reach.clear();
        for (Place place : net.getPlaces()) {
            if (PetriNetExtensionHandler.isReach(place)) {
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

    @Override
    public Reachability newObject() {
        return new Reachability();
    }

}
