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
public class Buchi extends Condition<Buchi> {

    private final Set<Place> buchiPlaces;
    private boolean existential;

    public Buchi() {
        this(true);
    }

    public Buchi(boolean existential) {
        buchiPlaces = new HashSet<>();
        this.existential = existential;
    }

    public Buchi(Buchi buchi) {
        this.existential = buchi.existential;
        this.buchiPlaces = new HashSet<>(buchi.buchiPlaces);
    }

    @Override
    public void buffer(PetriNet net) {
        for (Place place : net.getPlaces()) {
            if (PetriNetExtensionHandler.isBuchi(place)) {
                buchiPlaces.add(place);
            }
        }
        // java 1.8
//        game.getNet().getPlaces().stream().filter((place) -> (place.hasExtension("buchi"))).forEach((place) -> {
//            buchiPlaces.add(place);
//        });
    }

    public Set<Place> getBuchiPlaces() {
        return buchiPlaces;
    }

    @Override
    public Objective getObjective() {
        return existential ? Objective.E_BUCHI : Objective.A_BUCHI;
    }

    @Override
    public Buchi getCopy() {
        return new Buchi(this);
    }

}
