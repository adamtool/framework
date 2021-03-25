package uniolunisaar.adam.logic.transformers.petrinet.pn2aiger;

import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.circuits.AigerFile;
import static uniolunisaar.adam.ds.circuits.AigerFile.NEW_VALUE_OF_LATCH_SUFFIX;
import uniolunisaar.adam.ds.circuits.CircuitRendererSettings;

/**
 *
 * Attention in combination with McHyper the output is not as expected. McHyper
 * uses the output for the atomic propositions, but you don't have the output
 * visual in the counter example. There you only have access to the values of
 * the latches (the old value) and the input values (ergo the transitions).
 *
 * @author Manuel Gieseking
 */
public class AigerRendererSafeStutterRegister extends AigerRenderer {

    public static final String STUTT_LATCH = "#stutt#";
    private final boolean max;
    protected final boolean asError = false;
//    private final boolean asError = true; // currently there is still an error for the binary coding, but didn't seem 
    // to make such a difference anyhow.

    public AigerRendererSafeStutterRegister(PetriNet net, boolean max, CircuitRendererSettings.TransitionSemantics semantics) {
        super(net, semantics);
        this.max = max;
    }

    public String renderToString() {
        return render().toString();
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
    public AigerFile render() {
        AigerFile f = super.render();
        updateStuttering(f);
        return f;
    }

    @Override
    protected void addLatches(AigerFile file) {
        super.addLatches(file);
        // add the stuttering latch
        file.addLatch(STUTT_LATCH);
    }

    @Override
    protected void addOutputs(AigerFile file) {
        super.addOutputs(file);
        // add the init latch as output
//        file.addOutput(OUTPUT_PREFIX + INIT_LATCH); //I don't use it in the formula, so I don't need it as output
        // add the stuttering latch as output
        file.addOutput(OUTPUT_PREFIX + STUTT_LATCH);
    }

    protected void setInitOutput(AigerFile file) {
        // init latch is the old value
        file.copyValues(OUTPUT_PREFIX + INIT_LATCH, INIT_LATCH);
    }

    protected void setStuttterOutput(AigerFile file) {
        if (!asError) {
            // for stuttering it is the old value
            file.copyValues(OUTPUT_PREFIX + STUTT_LATCH, STUTT_LATCH);
        } else {
            // for the error case it is the new value
            file.copyValues(OUTPUT_PREFIX + STUTT_LATCH, STUTT_LATCH + NEW_VALUE_OF_LATCH_SUFFIX);
        }
    }

    @Override
    protected void setOutputs(AigerFile file) {
//        setInitOutput(file); I don't use it in the formula, so I don't need it as output
        setStuttterOutput(file);
        setTransitionOutputs(file);
        setPlaceOutputs(file);
    }

    void updateStuttering(AigerFile file) {
        if (!max) {
            if (asError) { // it's an error iff the input was not false
                String[] inputs = new String[net.getTransitions().size()];
                int i = 0;
                for (String id : file.getInputNames()) {
                    inputs[i++] = "!" + id;
                }
                file.addGate("inputAllZero", inputs);
                file.addGate(STUTT_LATCH + NEW_VALUE_OF_LATCH_SUFFIX + "buf", "!inputAllZero", ALL_TRANS_FALSE);
                file.addGate(STUTT_LATCH + NEW_VALUE_OF_LATCH_SUFFIX, INIT_LATCH, STUTT_LATCH + NEW_VALUE_OF_LATCH_SUFFIX + "buf");
            } else {
                // old version: setting stutt =1 iff several transition are chosen or a not enable transition is chosen
                //              not for when the input is all Zero
//        String[] inputs = new String[net.getTransitions().size()];
//        int i = 0;
//        for (Transition t : net.getTransitions()) {
//            inputs[i++] = "!" + INPUT_PREFIX + t.getId();
//        }        
//        file.addGate(STUTT_LATCH + "_buf", inputs);
//        file.addGate(STUTT_LATCH + NEW_VALUE_OF_LATCH_SUFFIX, ALL_TRANS_NOT_TRUE, "!" + STUTT_LATCH + "_buf");
                file.addGate(STUTT_LATCH + NEW_VALUE_OF_LATCH_SUFFIX, INIT_LATCH, ALL_TRANS_FALSE);
            }
        } else {
            // here the error version is exactly the stuttering version since the maximality is done here.            
            // old version: setting stutt =1 iff several transition are chosen or a not enable transition is chosen
            //              not for when the input is all Zero
            String[] inputs = new String[net.getTransitions().size()];
            int i = 0;
            for (Transition t : net.getTransitions()) {
                inputs[i++] = "!" + ENABLED_PREFIX + t.getId();
            }
            file.addGate(STUTT_LATCH + "_buf", inputs);
//            file.addGate(STUTT_LATCH + "_buf", AigerFile.TRUE);
            file.addGate(STUTT_LATCH + NEW_VALUE_OF_LATCH_SUFFIX, ALL_TRANS_FALSE, "!" + STUTT_LATCH + "_buf");
//        file.addGate(STUTT_LATCH + NEW_VALUE_OF_LATCH_SUFFIX, INIT_LATCH, ALL_TRANS_NOT_TRUE);
        }
    }
}
