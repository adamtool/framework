package uniolunisaar.adam.logic.transformers.pn2aiger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.circuits.AigerFile;

/**
 *
 * @author Manuel Gieseking
 */
public class AigerRendererSafeOutStutterRegisterLogTransMaxInterleaving extends AigerRendererSafeOutStutterRegisterMaxInterleaving {

    private final Map<Transition, Integer> transIDs;

    public AigerRendererSafeOutStutterRegisterLogTransMaxInterleaving(PetriNet net) {
        super(net);
        transIDs = new HashMap<>(net.getTransitions().size());
        int id = 0;
        for (Iterator<Transition> iterator = net.getTransitions().iterator(); iterator.hasNext();) {
            Transition t = iterator.next();
            transIDs.put(t, id++);
        }
    }

    @Override
    void addInputs(AigerFile file) {
        // add log(|T|) input to code the transitions logarithmically
        int size = (int) (Math.log(net.getTransitions().size()) / Math.log(2)) + 1;
        for (int i = 0; i < size; i++) {
            file.addInput(INPUT_PREFIX + i);
        }
    }

    @Override
    void addChosingOfValidTransitions(AigerFile file) {
        // get the ID of the logarithmic input
        int digits = Integer.toBinaryString(net.getTransitions().size() - 1).length();
        for (Iterator<Transition> iterator = net.getTransitions().iterator(); iterator.hasNext();) {
            Transition t = iterator.next();
            // code the ID
            String bin = Integer.toBinaryString(transIDs.get(t));
            bin = String.format("%" + digits + "s", bin).replace(' ', '0');
            String[] gates = new String[bin.length() + 1];
            for (int i = 0; i < bin.length(); i++) {
                char var = bin.charAt(i);
                if (var == '0') {
                    gates[i] = "!" + INPUT_PREFIX + i;
                } else {
                    gates[i] = INPUT_PREFIX + i;
                }
            }
            // the transition is only true if it is also enabled
            gates[bin.length()] = ENABLED_PREFIX + t.getId();
            file.addGate(VALID_TRANSITION_PREFIX + t.getId(), gates);
        }
    }

}
