package uniolunisaar.adam.ds.objectives;

import java.util.HashSet;
import java.util.Set;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 *
 * @author Manuel Gieseking
 */
public class Buchi extends Condition {

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
    public void buffer(PetriNetWithTransits net) {
        for (Place place : net.getPlaces()) {
            if (net.isBuchi(place)) {
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
