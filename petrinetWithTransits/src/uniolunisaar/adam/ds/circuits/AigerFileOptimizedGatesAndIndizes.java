package uniolunisaar.adam.ds.circuits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uniol.apt.util.Pair;

/**
 * NOt finished
 * @author Manuel Gieseking
 */
public class AigerFileOptimizedGatesAndIndizes extends AigerFile {

    private final Map<String, Gate> andGates = new HashMap<>();

    @Override
    void putGate(String out, String in1, String in2) {
        andGates.put(out, new Gate(out, in1, in2));
    }

    @Override
    List<IntGate> getGates() {
        // gates
        // OLD VERSION: directly output the set of gates
//        StringBuilder gates = new StringBuilder();
//        for (Map.Entry<String, Pair<Gate, Integer>> entry : andGates.entrySet()) {
//            Pair<Gate, Integer> value = entry.getValue();
//            gates.append(value.getSecond()).append(" ").append(getIndex(value.getFirst().getIn1())).append(" ").append(getIndex(value.getFirst().getIn2())).append("\n");
//        }      
        // NEW Version first delete some unneccessary gates
        gatelist2BaseIdentifiers();
//        List<IntGate> gates = getIntGates();
//        gates = optimizeGates(gates);
//        StringBuilder gateStrings = new StringBuilder();
//        for (IntGate gate : gates) {
//            gateStrings.append(gate.out).append(" ").append(gate.in1).append(" ").append(gate.in2).append("\n");
//        }
//        return gateStrings.toString();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void gatelist2BaseIdentifiers() {
        for (Gate gate : andGates.values()) {
            Pair<String, Integer> pair = getBaseIdentifier(gate.getIn1(), 0);
            String neg = (pair.getSecond() % 2 == 0) ? "" : "!";
            gate.setIn1(neg + pair.getFirst());
        }
    }

//    private void optimizeGatelist() {
//        List<Pair<String, Gate>> toRemove = new ArrayList<>();
//        do { // if there is still s.th. to remove repeat
//            // safely (i.e. replace all the output of the gate using indizes with the replacement) delete all gates
//            for (Pair<String, Gate> pair : toRemove) {
//                andGates.remove(pair.getFirst(), pair.getSecond());
//                String out = pair.getSecond().getOut();
//                String replace = pair.getFirst();
//                for (Gate gate : andGates) {
//                    if (gate.in1 == out) {
//                        gate.in1 = replace;
//                    }
//                    if (gate.in2 == out) {
//                        gate.in2 = replace;
//                    }
//                }
//            }
//            toRemove.clear();
//            // find gates with the same inputs
//            for (IntGate gate : gates) {
//                if (gate.in1 == gate.in2) {
//                    toRemove.add(new Pair<>(gate.out, gate));
//                }
//            }
//        } while (!toRemove.isEmpty());
//    }

//    private List<IntGate> getIntGates() {
//        List<IntGate> gates = new ArrayList<>();
//        for (Map.Entry<String, Pair<Gate, Integer>> entry : andGates.entrySet()) {
//            Pair<Gate, Integer> value = entry.getValue();
//            gates.add(new IntGate(value.getSecond(), getIndex(value.getFirst().getIn1()), getIndex(value.getFirst().getIn2())));
//        }
//        return gates;
//    }

  
    @Override
    public int getNbOfGates() {
        return andGates.entrySet().size();
    }

    @Override
    int getMaxVarIdx(List<IntGate> gates) {        
//        return inputs.size() + latches.size() + getNbOfGates();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    @Override
    int getGateIndex(String identifier) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
