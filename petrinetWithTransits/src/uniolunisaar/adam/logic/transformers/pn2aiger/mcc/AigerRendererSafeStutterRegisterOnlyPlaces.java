package uniolunisaar.adam.logic.transformers.pn2aiger.mcc;

import uniolunisaar.adam.logic.transformers.pn2aiger.*;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.circuits.AigerFile;
import uniolunisaar.adam.ds.circuits.CircuitRendererSettings;
import static uniolunisaar.adam.logic.transformers.pn2aiger.AigerRenderer.OUTPUT_PREFIX;

/**
 *
 * @author Manuel Gieseking
 */
public class AigerRendererSafeStutterRegisterOnlyPlaces extends AigerRendererSafeStutterRegister {

    public AigerRendererSafeStutterRegisterOnlyPlaces(PetriNet net, boolean max, CircuitRendererSettings.TransitionSemantics semantics) {
        super(net, max, semantics);
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
        setInitOutput(file);
        setStuttterOutput(file);
        setPlaceOutputs(file);
    }

}
