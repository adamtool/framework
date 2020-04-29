package uniolunisaar.adam.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import uniol.apt.adt.pn.Marking;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;
import uniolunisaar.adam.exceptions.NoSuitableParameterException;
import uniolunisaar.adam.util.PNTools;

/**
 *
 * @author Manuel Gieseking
 */
public class Unfolder {

    private static Unfolder instance = null;

    public static Unfolder getInstance() {
        if (instance == null) {
            instance = new Unfolder();
        }
        return instance;
    }

    private Unfolder() {
    }

    /**
     * Attention: this method is only for safe Petri nets!
     *
     * @param net
     * @param nb_fired_transitions
     * @return
     * @throws uniolunisaar.adam.exceptions.NoSuitableParameterException
     */
    public PetriNet unfold(PetriNet net, long nb_fired_transitions) throws NoSuitableParameterException {
        if (nb_fired_transitions < 0) {
            throw new NoSuitableParameterException(nb_fired_transitions, "The number of transitions should be positive.");
        }
        long input_fired_transitions = nb_fired_transitions;
        PetriNet output = PNTools.createPetriNet("Unfolding of the net " + net.getName());
        LinkedList<List<Place>> todo = new LinkedList<>();
        // create the initial marking
        Set<Place> initialMarking = new HashSet<>();
        Marking init = net.getInitialMarking();
        for (Place place : net.getPlaces()) {
            if (init.getToken(place).getValue() > 0) {
                initialMarking.add(place);
            }
        }
        // create the initial cut
        List<Place> initCut = createAndAddPlaces(output, null, initialMarking);
        for (Place place : initCut) {
            place.setInitialToken(1);
        }
        todo.add(initCut);
        while (!todo.isEmpty()) {
            List<Place> cut = todo.pop();
            // create the corresponding marking
            List<Place> marking = new ArrayList<>();
            for (Place place : cut) {
                marking.add(net.getPlace(PetriNetExtensionHandler.getOrigID(place)));
            }
            // which transitions are firable
            transitions:
            for (Transition origTransition : net.getTransitions()) {
                //check firable
                boolean firable = marking.containsAll(origTransition.getPreset());
                if (firable) {
                    // if the maximum amount of transition is already fired, break everything
                    nb_fired_transitions--;
                    if (nb_fired_transitions < 0) {
                        todo.clear();
                        break;
                    }
                    // copy the marking and the cut
                    List<Place> myMarking = new ArrayList<>(marking);
                    List<Place> myCut = new ArrayList<>(cut);
                    // fire
                    Set<Place> preset = origTransition.getPreset();
                    Set<Place> postset = origTransition.getPostset();
                    myMarking.removeAll(preset);
                    myMarking.addAll(postset);
                    // add transition
                    Transition tout = output.createTransition();
                    tout.setLabel(origTransition.getId());
                    // and the preset
                    Set<Place> cutPreset = new HashSet<>();
                    for (Place place : myCut) {
                        if (preset.contains(net.getPlace(PetriNetExtensionHandler.getOrigID(place)))) {
                            output.createFlow(place, tout);
                            cutPreset.add(place);
                        }
                    }
                    // this transition is already handle for another cut (concurrent places)
                    for (Place place : cutPreset) {
                        for (Transition transition : place.getPostset()) {
                            if (transition != tout && transition.getLabel().equals(tout.getLabel()) && transition.getPreset().equals(cutPreset)) {
                                output.removeTransition(tout);
                                nb_fired_transitions++;
                                continue transitions;
                            }
                        }
                    }
                    // add places and the postset
                    List<Place> cutPostset = createAndAddPlaces(output, tout, postset);
                    // do the firing in the cut
                    myCut.removeAll(cutPreset);
                    myCut.addAll(cutPostset);
                    todo.add(myCut);
                }
            }
        }
        if (nb_fired_transitions >= 0) {
            Logger.getInstance().addMessage("The unfolding is finite. The maximal bound needed is " + (input_fired_transitions - nb_fired_transitions) + ".", false);
        } else {
            Logger.getInstance().addMessage("This is just an extract of the unfolding. There are still transitions to fire for the bound " + input_fired_transitions + ".", false);
        }
        return output;
    }

    private List<Place> createAndAddPlaces(PetriNet output, Transition transition, Set<Place> postset) {
        List<Place> cut = new ArrayList<>();
        for (Place place : postset) {
            Place out = output.createPlace();
            PetriNetExtensionHandler.setOrigID(out, place.getId());
            if (PetriNetExtensionHandler.isBad(place)) {
                PetriNetExtensionHandler.setBad(out);
            }
            if (PetriNetExtensionHandler.isReach(place)) {
                PetriNetExtensionHandler.setReach(out);
            }
            if (PetriNetExtensionHandler.isBuchi(place)) {
                PetriNetExtensionHandler.setBuchi(out);
            }
            cut.add(out);
            if (transition != null) {
                output.createFlow(transition, out);
            }
        }
        return cut;
    }

}
