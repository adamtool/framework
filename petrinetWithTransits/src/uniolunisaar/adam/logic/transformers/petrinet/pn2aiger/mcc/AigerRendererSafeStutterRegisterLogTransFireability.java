package uniolunisaar.adam.logic.transformers.petrinet.pn2aiger.mcc;

import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.circuits.AigerFile;
import uniolunisaar.adam.ds.circuits.CircuitRendererSettings;
import static uniolunisaar.adam.logic.transformers.petrinet.pn2aiger.AigerRenderer.ENABLED_PREFIX;
import static uniolunisaar.adam.logic.transformers.petrinet.pn2aiger.AigerRenderer.INIT_LATCH;
import static uniolunisaar.adam.logic.transformers.petrinet.pn2aiger.AigerRenderer.OUTPUT_PREFIX;
import static uniolunisaar.adam.logic.transformers.petrinet.pn2aiger.AigerRendererSafeStutterRegister.STUTT_LATCH;
import uniolunisaar.adam.logic.transformers.petrinet.pn2aiger.AigerRendererSafeStutterRegisterLogTrans;

/**
 *
 * @author Manuel Gieseking
 */
public class AigerRendererSafeStutterRegisterLogTransFireability extends AigerRendererSafeStutterRegisterLogTrans {

    public AigerRendererSafeStutterRegisterLogTransFireability(PetriNet net, boolean max, CircuitRendererSettings.TransitionSemantics semantics) {
        super(net, max, semantics);
    }

    @Override
    protected void addOutputs(AigerFile file) {
        // here only the transitions are relevant
        for (Transition t : getNet().getTransitions()) {
            file.addOutput(OUTPUT_PREFIX + "{" + t.getId() + ">");
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
        // output the enabledness of the transitions
        for (Transition t : getNet().getTransitions()) {
            file.copyValues(OUTPUT_PREFIX + "{" + t.getId() + ">", ENABLED_PREFIX + t.getId());
        }
    }

}
