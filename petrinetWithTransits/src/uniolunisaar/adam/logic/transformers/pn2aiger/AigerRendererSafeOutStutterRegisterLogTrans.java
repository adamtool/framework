package uniolunisaar.adam.logic.transformers.pn2aiger;

import java.util.Iterator;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.circuits.AigerFile;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;

/**
 *
 * @author Manuel Gieseking
 */
public class AigerRendererSafeOutStutterRegisterLogTrans extends AigerRendererSafeOutStutterRegister {

    public final static String BIN_COD_ID = "#bin#_";

    public AigerRendererSafeOutStutterRegisterLogTrans(PetriNet net, boolean max) {
        super(net, max);
        // Give unique binary ids for the transitions        
        int digits = Integer.toBinaryString(net.getTransitions().size() - 1).length();
        int id = 0;
        for (Iterator<Transition> iterator = net.getTransitions().iterator(); iterator.hasNext();) {
            Transition t = iterator.next();
            // code the ID
            String bin = Integer.toBinaryString(id++);
            bin = String.format("%" + digits + "s", bin).replace(' ', '0');
            PetriNetExtensionHandler.setBinID(t, bin);
        }
    }

    @Override
    void addInputs(AigerFile file) {
        // add log(|T|) input to code the transitions logarithmically
//        int size1 = (int) (Math.log(net.getTransitions().size()) / Math.log(2)) + 1;
        int size = Integer.toBinaryString(net.getTransitions().size() - 1).length();
        for (int i = 0; i < size; i++) {
            file.addInput(INPUT_PREFIX + BIN_COD_ID + i);
        }
    }

    @Override
    void addChosingOfValidTransitions(AigerFile file) {
        // get the ID of the logarithmic input
        for (Iterator<Transition> iterator = net.getTransitions().iterator(); iterator.hasNext();) {
            Transition t = iterator.next();
            String bin = PetriNetExtensionHandler.getBinID(t);
            String[] gates = new String[bin.length() + 1];
            for (int i = 0; i < bin.length(); i++) {
                char var = bin.charAt(i);
                if (var == '0') {
                    gates[i] = "!" + INPUT_PREFIX + BIN_COD_ID + i;
                } else {
                    gates[i] = INPUT_PREFIX + BIN_COD_ID + i;
                }
            }
            // the transition is only true if it is also enabled
            gates[bin.length()] = ENABLED_PREFIX + t.getId();
            file.addGate(VALID_TRANSITION_PREFIX + t.getId(), gates);
        }
    }

}
