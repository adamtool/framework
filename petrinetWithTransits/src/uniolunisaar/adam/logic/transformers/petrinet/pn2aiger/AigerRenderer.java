package uniolunisaar.adam.logic.transformers.petrinet.pn2aiger;

import java.util.Set;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.circuits.AigerFile;
import static uniolunisaar.adam.ds.circuits.AigerFile.NEW_VALUE_OF_LATCH_SUFFIX;
import uniolunisaar.adam.ds.circuits.AigerFileOptimizedGates;
import uniolunisaar.adam.ds.circuits.AigerFileOptimizedGatesAndIndizes;
import uniolunisaar.adam.ds.circuits.CircuitRendererSettings;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;

/**
 *
 * Attention in combination with McHyper the output is not as expected.
 * McHyper uses the output for the atomic propositions, but you don't 
 * have the output visual in the counter example. There you only have
 * access to the values of the latches (the old value) and the input
 * values (ergo the transitions).
 *
 * @author Manuel Gieseking
 */
public class AigerRenderer {

    public static final String INIT_LATCH = "#initLatch#";
    public static final String INPUT_PREFIX = "#in#_";
    public static final String OUTPUT_PREFIX = "#out#_";
    public static final String ENABLED_PREFIX = "#enabled#_";
    public static final String VALID_TRANSITION_PREFIX = "#chosen#_";
    public static final String ALL_TRANS_FALSE = "#allTransitionsNotTrue#";
    public static final String SUCCESSOR_REGISTER_PREFIX = "#succReg#_";
    public static final String SUCCESSOR_PREFIX = "#succ#_";

    public enum OptimizationsSystem {
        NONE,
        NB_GATES,
        NB_GATES_AND_EQCOM,
        NB_GATES_AND_INDICES,
        NB_GATES_AND_INDICES_AND_EQCOM,
        NB_GATES_AND_INDICES_EXTRA,
        NB_GATES_AND_INDICES_EXTRA_AND_EQCOM
    }

    public enum OptimizationsComplete {
        NONE,
        NB_GATES_BY_REGEX,
        NB_GATES_BY_REGEX_WITH_IDX_SQUEEZING,
        NB_GATES_BY_DS,
        NB_GATES_BY_DS_WITH_IDX_SQUEEZING,
        NB_GATES_BY_DS_WITH_IDX_SQUEEZING_AND_EXTRA_LIST
    }

    private OptimizationsSystem optimizationsSys = OptimizationsSystem.NONE;
    private OptimizationsComplete optimizationsComplete = OptimizationsComplete.NONE;

    final PetriNet net;
    private final CircuitRendererSettings.TransitionSemantics semantics;

    public AigerRenderer(PetriNet net, CircuitRendererSettings.TransitionSemantics semantics) {
        this.net = net;
        this.semantics = semantics;
    }

    /**
     * Adds inputs for all transitions.
     *
     * @param file
     */
    protected void addInputs(AigerFile file) {
        // Add an input for all transitions
        for (Transition t : net.getTransitions()) {
            file.addInput(INPUT_PREFIX + t.getId());
        }
    }

    /**
     * Adds latches for the init latch and all places
     *
     * @param file
     */
    protected void addLatches(AigerFile file) {
        // Add the latches
        // initialization latch
        file.addLatch(INIT_LATCH);
        // places
        for (Place p : net.getPlaces()) {
            file.addLatch(p.getId());
        }
    }

    /**
     * Adds the outputs for the places and transitions.
     *
     * @param file
     */
    protected void addOutputs(AigerFile file) {
        //Add outputs
        // for the places
        for (Place p : net.getPlaces()) {
            file.addOutput(OUTPUT_PREFIX + p.getId());
        }
        // and the transitions
        for (Transition t : net.getTransitions()) {
            file.addOutput(OUTPUT_PREFIX + t.getId());
        }
    }

    private String addEnabled(AigerFile file, Transition t) {
        String outId = ENABLED_PREFIX + t.getId();
        Set<Flow> preset = t.getPresetEdges();
        String[] inputs = new String[preset.size()];
        int i = 0;
        for (Flow e : preset) {
            if (PetriNetExtensionHandler.isInhibitor(e)) {
                inputs[i++] = "!" + e.getPlace().getId();
            } else {
                inputs[i++] = e.getPlace().getId();
            }
        }
        file.addGate(outId, inputs);
        return outId;
    }

    private void addEnablednessOfTransitions(AigerFile file) {
        // Create the general circuits for getting the enabledness of a transition
        for (Transition t : net.getTransitions()) {
            addEnabled(file, t);
        }
    }

    protected void addChosingOfValidTransitions(AigerFile file) {
        //%%%%%%%%%%%%% Create the output for the transitions
        // Choose that only one transition at a time can be fired
        //
        // todo: add other semantics (choose every not conflicting transition)
        // or put this choosing in the formula (this would have an impact on the next operator)
        //
        // The transition is only choosen if it is enabled
        for (Transition t1 : net.getTransitions()) {
            String[] inputs = new String[net.getTransitions().size() + 1];
            int i = 0;
            inputs[i++] = INPUT_PREFIX + t1.getId();
            for (Transition t2 : net.getTransitions()) {
                if (!t1.getId().equals(t2.getId())) {
                    inputs[i++] = "!" + INPUT_PREFIX + t2.getId();
                }
            }
            inputs[i++] = ENABLED_PREFIX + t1.getId(); // this one added to have only the enabled choosen
            file.addGate(VALID_TRANSITION_PREFIX + t1.getId(), inputs);
        }
    }

    private void addUpdateInitLatch(AigerFile file) {
        // Update the init flag just means set it to true
        file.copyValues(INIT_LATCH + NEW_VALUE_OF_LATCH_SUFFIX, AigerFile.TRUE);
    }

    private void addNegationOfAllTransitions(AigerFile file) {
        String[] inputs = new String[net.getTransitions().size()];
        int i = 0;
        for (Transition t : net.getTransitions()) {
            inputs[i++] = "!" + VALID_TRANSITION_PREFIX + t.getId();
        }
        file.addGate(ALL_TRANS_FALSE, inputs);
    }

    private String createSuccessorRegister(AigerFile file, Place p) {
        String id = SUCCESSOR_REGISTER_PREFIX + p.getId();
        String[] inputs = new String[net.getTransitions().size()];
        int i = 0;
        for (Transition t : net.getTransitions()) {
            String firingResult;
            if (!t.getPreset().contains(p) && !t.getPostset().contains(p)) {
                firingResult = "!" + p.getId();
            } else if (t.getPreset().contains(p) && !t.getPostset().contains(p)) {
                firingResult = AigerFile.TRUE;
            } else {
                firingResult = AigerFile.FALSE;
            }
            file.addGate(id + "_" + t.getId() + "_buf", VALID_TRANSITION_PREFIX + t.getId(), firingResult);
            inputs[i++] = "!" + id + "_" + t.getId() + "_buf";
        }
        file.addGate(id, inputs);
        return id;
    }

    private String createSuccessor(AigerFile file, Place p) {
        String id = SUCCESSOR_PREFIX + p.getId();
        // create A
        String idA = id + "_A";
        file.addGate(idA, ALL_TRANS_FALSE, "!" + p.getId());
        // create B
        String idB = id + "_B";
        file.addGate(idB, "!" + ALL_TRANS_FALSE, "!" + SUCCESSOR_REGISTER_PREFIX + p.getId());
        // total
        file.addGate(id, "!" + idA, "!" + idB);
        return id;
    }

    private void addSuccessors(AigerFile file) {
        // needed for createSuccessor
        addNegationOfAllTransitions(file);

        // Create for each place the chosing and the test if s.th. has fired
        for (Place p : net.getPlaces()) {
            // Create for each place the choosing of the transition
//            createChooseTransition(file, net, p); // use this when not already checked that the transition is enabled
            createSuccessorRegister(file, p); // F2
            // Create for each place the check if s.th. has fired
            createSuccessor(file, p); // F1
        }

        // Do the final update for the places
        for (Place p : net.getPlaces()) {
            if (p.getInitialToken().getValue() > 0) { // is initial place
                // !(!init_latch AND !F)
                file.addGate(p.getId() + "_new_buf", INIT_LATCH, "!" + SUCCESSOR_PREFIX + p.getId());
                file.copyValues(p.getId() + NEW_VALUE_OF_LATCH_SUFFIX, "!" + p.getId() + "_new_buf");
            } else {
                file.addGate(p.getId() + NEW_VALUE_OF_LATCH_SUFFIX, INIT_LATCH, SUCCESSOR_PREFIX + p.getId());
            }
        }
    }

    protected void setTransitionOutputs(AigerFile file) {
        // the valid transitions are already the output (initially it is not important what the output is)
        for (Transition t : net.getTransitions()) {
            file.copyValues(OUTPUT_PREFIX + t.getId(), VALID_TRANSITION_PREFIX + t.getId());
//            System.out.println("all true");
//            file.copyValues(OUTPUT_PREFIX + t.getId(), AigerFile.TRUE);
        }
//        // the valid transitions are already the output in the case that it is not init
//        for (Transition t : net.getTransitions()) {
//            file.addGate(OUTPUT_PREFIX + t.getId(), INIT_LATCH, VALID_TRANSITION_PREFIX + t.getId());
//        }
    }

    protected void setPlaceOutputs(AigerFile file) {
        switch (semantics) {
            case INGOING:
                // for the ingoing semantics the places are already the new values
                for (Place p : net.getPlaces()) {
                    file.copyValues(OUTPUT_PREFIX + p.getId(), p.getId() + NEW_VALUE_OF_LATCH_SUFFIX);
//                    file.copyValues(OUTPUT_PREFIX + p.getId(), AigerFile.TRUE);
                }
                break;
            case OUTGOING:
                // for the outgoing semantics 
                // if it is not the initial step
                // the place outputs are the saved output of the place latches
                // otherwise it is the new value of the places
                for (Place p : net.getPlaces()) {
                    file.addGate(OUTPUT_PREFIX + p.getId() + "_bufA", "!" + INIT_LATCH, "!" + p.getId() + NEW_VALUE_OF_LATCH_SUFFIX);
                    file.addGate(OUTPUT_PREFIX + p.getId() + "_bufB", INIT_LATCH, "!" + p.getId());
                    file.addGate(OUTPUT_PREFIX + p.getId(), "!" + OUTPUT_PREFIX + p.getId() + "_bufA", "!" + OUTPUT_PREFIX + p.getId() + "_bufB");
                }
                break;
            default:
                throw new RuntimeException("The semantics " + semantics.name() + " is not yet implemented.");
        }
    }

    protected void setOutputs(AigerFile file) {
        setTransitionOutputs(file);
        setPlaceOutputs(file);
    }

    public AigerFile render() {
        AigerFile file = AigerRenderer.getFile(optimizationsSys);
        //%%%%%%%%% Add inputs -> all transitions
        addInputs(file);
        //%%%%%%%%%% Add the latches -> init + all places
        addLatches(file);
        //%%%%%%%%% Add outputs -> all places and transitions
        addOutputs(file);

        //%%%%%%%%%%%%% Create the output for the transitions
        // Create the general circuits for getting the enabledness of a transition
        addEnablednessOfTransitions(file);

        // Choose that only one transition at a time can be fired
        //
        // todo: add other semantics (choose every not conflicting transition)
        // or put this choosing in the formula
        //
        // The transition is only choosen if it is enabled
        addChosingOfValidTransitions(file);

        // %%%%%%%%%% Update the latches
        // the init flag
        addUpdateInitLatch(file);
        // the places
        addSuccessors(file);

        // %%%%%%%%% Set the outputs
        setOutputs(file);

        return file;
    }

    public static AigerFile getFile(OptimizationsSystem opt) {
        AigerFile file;
        switch (opt) {
            case NONE:
                file = new AigerFileOptimizedGates(false, false);
                break;
            case NB_GATES:
                file = new AigerFileOptimizedGates(true, false);
                break;
            case NB_GATES_AND_EQCOM:
                file = new AigerFileOptimizedGates(true, true);
                break;
            case NB_GATES_AND_INDICES:
                file = new AigerFileOptimizedGatesAndIndizes(false, false);
                break;
            case NB_GATES_AND_INDICES_AND_EQCOM:
                file = new AigerFileOptimizedGatesAndIndizes(false, true);
                break;
            case NB_GATES_AND_INDICES_EXTRA:
                file = new AigerFileOptimizedGatesAndIndizes(true, false);
                break;
            case NB_GATES_AND_INDICES_EXTRA_AND_EQCOM:
                file = new AigerFileOptimizedGatesAndIndizes(true, true);
                break;
            default:
                file = new AigerFileOptimizedGates(false, false);
        }
        return file;
    }

    public void setSystemOptimizations(OptimizationsSystem optimizations) {
        this.optimizationsSys = optimizations;
    }

    public OptimizationsSystem getSystemOptimizations() {
        return optimizationsSys;
    }

    public void setMCHyperResultOptimizations(OptimizationsComplete optimizations) {
        this.optimizationsComplete = optimizations;
    }

    public OptimizationsComplete getMCHyperResultOptimizations() {
        return optimizationsComplete;
    }

    public PetriNet getNet() {
        return net;
    }

    /**
     * Not needed in the situation when we already only chose enabled
     * transitions
     *
     * @param file
     * @param net
     * @return
     * @deprecated
     */
    @Deprecated
    void addFiring(AigerFile file, PetriNet net) {
        // Create for each place and each transition what happens, when "firing"
        for (Place p : net.getPlaces()) {
            for (Transition t : net.getTransitions()) {
                createDoFiring(file, p, t);
            }
        }
    }

    /**
     * Used if the enabledness is checked directly here and not at the beginning
     *
     * @param file
     * @param p
     * @param t
     * @return
     * @deprecated
     */
    @Deprecated
    private String createDoFiring(AigerFile file, Place p, Transition t) {
        String id = p.getId() + "_" + t.getId() + "_fired";
        if (!t.getPreset().contains(p) && !t.getPostset().contains(p)) {
            file.copyValues(id, p.getId());
        } else if (t.getPreset().contains(p) && !t.getPostset().contains(p)) {
            file.addGate(id, "!" + ENABLED_PREFIX + t.getId(), p.getId());
        } else {
            file.addGate(id + "_buf", "!" + ENABLED_PREFIX + t.getId(), "!" + p.getId());
            file.copyValues(id, "!" + id + "_buf");
        }
        return id;
    }

    /**
     * Used if the enabledness is checked directly here and not at the beginning
     *
     * @param file
     * @param net
     * @param p
     * @return
     */
    @Deprecated
    private String createChooseTransition(AigerFile file, PetriNet net, Place p) {
        String id = "#transChoosen#_" + p.getId();
        String[] inputs = new String[net.getTransitions().size()];
        int i = 0;
        for (Transition t : net.getTransitions()) {
            file.addGate(id + "_" + t.getId() + "_buf", VALID_TRANSITION_PREFIX + t.getId(), "!" + p.getId() + "_" + t.getId() + "_fired");
            inputs[i++] = "!" + id + "_" + t.getId() + "_buf";
        }
        file.addGate(id, inputs);
        return id;
    }

}
