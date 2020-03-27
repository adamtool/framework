package uniolunisaar.adam.ds.petrinetwithtransits;

import java.util.HashSet;
import java.util.Set;
import uniol.apt.adt.CollectionToUnmodifiableSetAdapter;
import uniol.apt.adt.exception.NoSuchNodeException;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.exceptions.pnwt.InconsistencyException;

/**
 *
 * @author Manuel Gieseking
 */
public class Transit {

    public static final String INIT_KEY = ">";

    private final PetriNetWithTransits net;
    private final Place prePlace;
    private final Transition transition;
    private final Set<Place> postset = new HashSet<>();

    Transit(PetriNetWithTransits net, Place prePlace, Transition transition, Set<Place> postset) {
        this.net = net;
        this.prePlace = prePlace;
        this.transition = transition;
        this.postset.addAll(postset);
    }

    Transit(PetriNetWithTransits net, Transition transition, Set<Place> postset) {
        this(net, null, transition, postset);
    }

    void addPostsetPlace(String placeID) {
        addPostsetPlace(net.getPlace(placeID));
    }

    void addPostsetPlace(Place place) {
        try {
            if (!transition.getPostset().contains(place)) {
                throw new InconsistencyException(place.getId() + " is not in the postset of transition " + transition.getId());
            }
            postset.add(place);
        } catch (NoSuchNodeException e) {
            throw new InconsistencyException(place.getId() + " does not point to an existing node of the net '" + net.getName() + "'.", e);
        }
    }

    boolean removePostsetPlace(String placeID) {
        return removePostsetPlace(net.getPlace(placeID));
    }

    boolean removePostsetPlace(Place place) {
        return postset.remove(place);
    }
//    private void setPresetPlace(Place p) {
//        try {
//            String placeID = p.getId();
//            if (!t.getPreset().contains(p)) {
//                throw new InconsistencyException(placeID + " is not in the preset of transition " + t.getId());
//            }
//            pre = p;
//        } catch (NoSuchNodeException e) {
//            throw new InconsistencyException(p.getId() + " does not point to an existing node of the net '" + game.getName() + "'.", e);
//        }
//    }
//
//    public void addPostsetPlace(Place p) {
//        try {
//            String placeID = p.getId();
//            if (!t.getPostset().contains(p)) {
//                throw new InconsistencyException(placeID + " is not in the postset of transition " + t.getId());
//            }
//            postset.put(p.getId(), p);
//        } catch (NoSuchNodeException e) {
//            throw new InconsistencyException(p.getId() + " does not point to an existing node of the net '" + game.getName() + "'.", e);
//        }
//    }

    public boolean isInitial() {
        return prePlace == null;
    }

    public boolean isEmpty() {
//        return prePlace == null && postset.isEmpty();
        return postset.isEmpty();
    }

    public Transition getTransition() {
        return transition;
    }

    public Place getPresetPlace() {
//        return prePlace == null ? null : game.getPlace(prePlace);
        return prePlace;
    }

    public Set<Place> getPostset() {
        // This really behaves like a Set, but the Map doesn't know that its values are unique.
        return new CollectionToUnmodifiableSetAdapter<>(postset);
    }

    @Override
    public String toString() {
        return "Transit{" + "preset=" + ((prePlace == null) ? ">" : prePlace.getId()) + ", postset=" + postset.toString() + ", net=" + net.getName() + ", t=" + transition.getId() + '}';
    }
}
