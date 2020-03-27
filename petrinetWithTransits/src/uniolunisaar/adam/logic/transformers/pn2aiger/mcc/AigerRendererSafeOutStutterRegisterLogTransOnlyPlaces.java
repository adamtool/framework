package uniolunisaar.adam.logic.transformers.pn2aiger.mcc;

import uniolunisaar.adam.logic.transformers.pn2aiger.*;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.circuits.AigerFile;
import static uniolunisaar.adam.ds.circuits.AigerFile.NEW_VALUE_OF_LATCH_SUFFIX;
import static uniolunisaar.adam.logic.transformers.pn2aiger.AigerRenderer.OUTPUT_PREFIX;

/**
 *
 * @author Manuel Gieseking
 */
public class AigerRendererSafeOutStutterRegisterLogTransOnlyPlaces extends AigerRendererSafeOutStutterRegisterLogTrans {

    public AigerRendererSafeOutStutterRegisterLogTransOnlyPlaces(PetriNet net, boolean max) {
        super(net, max);
    }

    @Override
    protected void addOutputs(AigerFile file) {
        // here we are only interested in the places
        for (Place p : getNet().getPlaces()) {
            file.addOutput(OUTPUT_PREFIX + p.getId());
        }
        // add the init latch as output
        file.addOutput(OUTPUT_PREFIX + INIT_LATCH);
        // add the stuttering latch as output
        file.addOutput(OUTPUT_PREFIX + STUTT_LATCH);
    }

    @Override
    protected void setOutputs(AigerFile file) {
        // init latch is the old value
        file.copyValues(OUTPUT_PREFIX + INIT_LATCH, INIT_LATCH);
        if (!super.asError) {
            // for stuttering it is the old value
            file.copyValues(OUTPUT_PREFIX + STUTT_LATCH, STUTT_LATCH);
        } else {
            // for the error case it is the new value
            file.copyValues(OUTPUT_PREFIX + STUTT_LATCH, STUTT_LATCH + NEW_VALUE_OF_LATCH_SUFFIX);
        }
        // if it is not the initial step
        // the place outputs are the saved output of the place latches
        // otherwise it is the new value of the places
        for (Place p : getNet().getPlaces()) {
            file.addGate(OUTPUT_PREFIX + p.getId() + "_bufA", "!" + INIT_LATCH, "!" + p.getId() + NEW_VALUE_OF_LATCH_SUFFIX);
            file.addGate(OUTPUT_PREFIX + p.getId() + "_bufB", INIT_LATCH, "!" + p.getId());
            file.addGate(OUTPUT_PREFIX + p.getId(), "!" + OUTPUT_PREFIX + p.getId() + "_bufA", "!" + OUTPUT_PREFIX + p.getId() + "_bufB");
        }
    }

}
