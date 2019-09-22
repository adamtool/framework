package uniolunisaar.adam.logic.transformers.pn2aiger;

import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.circuits.AigerFile;
import static uniolunisaar.adam.ds.circuits.AigerFile.NEW_VALUE_OF_LATCH_SUFFIX;

/**
 * This class is not used? How are the false inputs handled? No stuttering latch
 * (which only allows not enabled transitions for finite runs at the end) Not
 * sure for what this was for or how it could be used.
 *
 * @author Manuel Gieseking
 */
public class AigerRendererSafeOut extends AigerRenderer {

    public AigerRendererSafeOut(PetriNet net) {
        super(net);
    }

    public String renderToString() {
        return super.render().toString();
    }

    public String renderWithSavingTransitions() {
        AigerFile file = render();
        //%%%%%%%%%% Add the additional latches
        // the transitions (todo: save only the one relevant id)
        for (Transition t : net.getTransitions()) {
            file.addLatch(t.getId());
        }

        // %%%%%%%%%% Update additional latches
        // the transitions are the valid transitions
        for (Transition t : net.getTransitions()) {
            file.copyValues(t.getId() + NEW_VALUE_OF_LATCH_SUFFIX, VALID_TRANSITION_PREFIX + t.getId());
        }
        return file.toString();
    }

    @Override
    void setOutputs(AigerFile file) {
        // the valid transitions are already the output in the case that it is not init
        for (Transition t : net.getTransitions()) {
            file.addGate(OUTPUT_PREFIX + t.getId(), INIT_LATCH, VALID_TRANSITION_PREFIX + t.getId());
//            file.copyValues(OUTPUT_PREFIX + t.getId(), VALID_TRANSITION_PREFIX + t.getId());
        }
        // if it is not the initial step
        // the place outputs are the saved output of the place latches
        // otherwise it is the new value of the places
        for (Place p : net.getPlaces()) {
            file.addGate(OUTPUT_PREFIX + p.getId() + "_bufA", "!" + INIT_LATCH, "!" + p.getId() + NEW_VALUE_OF_LATCH_SUFFIX);
            file.addGate(OUTPUT_PREFIX + p.getId() + "_bufB", INIT_LATCH, "!" + p.getId());
            file.addGate(OUTPUT_PREFIX + p.getId(), "!" + OUTPUT_PREFIX + p.getId() + "_bufA", "!" + OUTPUT_PREFIX + p.getId() + "_bufB");
        }
    }

}
