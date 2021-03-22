package uniolunisaar.adam.logic.transformers.petrinet.kbounded2safe;

import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.Marking;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;
import uniolunisaar.adam.exceptions.NotReducableException;
import uniolunisaar.adam.util.PNTools;

/**
 *
 * @author Manuel Gieseking
 */
public class Pipelining {

    public static final String TRANSITION_LABEL = "tau";
    public static final String DELIM = "_";
    public static final String COUNTER_ID = "!";

    public static PetriNet reduce(PetriNet in) throws NotReducableException {
        // Create an empty net for the output
        PetriNet out = new PetriNet(in.getName() + "_safe");
        // annotate the places with their bound
        PNTools.addsBoundedness2Places(in);
        // save the initial marking
        Marking initialMarking = in.getInitialMarking();

        // multiply the places and add the pipeline
        for (Place place : in.getPlaces()) {
            long k = PetriNetExtensionHandler.getBoundedness(place);
            // stop if an unbounded place is reached
            if (k == -1) {
                throw new NotReducableException("The net '" + in.getName() + "' is not k-bounded. Witness: place " + place.getId());
            }
            // create one place for each bound 
            for (int i = 0; i < k; i++) {
                Place pout = out.createPlace(place.getId() + DELIM + i);
                // add the iniital  tokens
                if (initialMarking.getToken(place.getId()).getValue() > 0) {
                    pout.setInitialToken(1);
                }
                pout.copyExtensions(place);
            }
            // do the special case for unreachable places
            if (k == 0) {
                Place pout = out.createPlace(place.getId() + DELIM + 0);
                pout.copyExtensions(place);
            }
            // for an unsafe place add the corresponding counter places and pipeline
            if (k > 1) {
                // create the places
                for (int i = 0; i < k; i++) {
                    Place counter = out.createPlace(COUNTER_ID + place.getId() + DELIM + i);
                    // they are all activated
                    counter.setInitialToken(1);
                }
                // if place is initial the first counter place should not be marked
                if (initialMarking.getToken(place.getId()).getValue() > 0) {
                    out.getPlace(COUNTER_ID + place.getId() + DELIM + 0).setInitialToken(0);
                }
                // create the pipeline, i.e., k-1 transitions inbetween the created places                
                for (int i = 1; i < k; i++) {
                    Transition t = out.createTransition(TRANSITION_LABEL + DELIM + place.getId() + DELIM + i);
                    // create the forward moving of the pipeline                    
                    Place pre = out.getPlace(place.getId() + DELIM + (i - 1));
                    Place post = out.getPlace(place.getId() + DELIM + i);
                    out.createFlow(pre, t);
                    out.createFlow(t, post);
                    // create the counter place movement
                    Place preNot = out.getPlace(COUNTER_ID + pre.getId());
                    Place postNot = out.getPlace(COUNTER_ID + post.getId());
                    out.createFlow(postNot, t);
                    out.createFlow(t, preNot);
                }
            }
        }

        // create the original transitions and flows + the additional flows for starting/ending the pipeline
        for (Transition t : in.getTransitions()) {
            Transition tout = out.createTransition(t);
            for (Flow presetEdge : t.getPresetEdges()) {
                Place pre = presetEdge.getPlace();
                long bound = PetriNetExtensionHandler.getBoundedness(pre);
                int weight = presetEdge.getWeight();
                for (int i = 0; i < weight; i++) { // take them of the last places of the pipeline
                    if (weight > bound) { // this transition is dead (needs more places in the preset as can ever reached
                        throw new NotReducableException("Transition '" + t.getId() + "' is dead. "
                                + "It wants to take '" + weight + "' token from a " + bound
                                + "-bounded place '" + pre.getId() + "'.");

                    }
                    out.createFlow(out.getPlace(pre.getId() + DELIM + ((bound - 1) - weight)), tout);
                }
            }
            for (Flow postsetEdge : t.getPostsetEdges()) {
                Place post = postsetEdge.getPlace();
                long bound = PetriNetExtensionHandler.getBoundedness(post);
                int weight = postsetEdge.getWeight();
                for (int i = 0; i < weight; i++) { // put them to the first places of the pipeline                      
                    Place pout = out.getPlace(post.getId() + DELIM + weight);
                    out.createFlow(tout, pout);
                    // if original postset place is not safe it (or the last tau transition) can only fire 
                    // when the places are free
                    if (bound > 1) {
                        out.createFlow(out.getPlace(COUNTER_ID + pout.getId()), tout);
                    }
                }
            }
        }
        return out;
    }
}
