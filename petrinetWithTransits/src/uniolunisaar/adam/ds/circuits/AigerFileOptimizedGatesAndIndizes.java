package uniolunisaar.adam.ds.circuits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uniol.apt.util.Pair;

/**
 * NOt finished
 *
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
//           lists2BaseIdentifiers();
////        optimizeGatelist();
//        Map<String, Pair<Gate, Integer>> gates = createGateIds(idx);
//        // gates
//        //   OLD VERSION: directly output the set of gates
//        StringBuilder gateStrings = new StringBuilder();
//        for (Map.Entry<String, Pair<Gate, Integer>> entry : gates.entrySet()) {
//            Pair<Gate, Integer> value = entry.getValue();
//            gateStrings.append(value.getSecond()).append(" ").append(getIndex(value.getFirst().getIn1(), gates)).append(" ").append(getIndex(value.getFirst().getIn2(), gates)).append("\n");
//        }
//
//        StringBuilder sb = new StringBuilder();
//        int total = ((idx - 2) + gates.size() * 2) / 2;
//        sb.append("aag ").append(total).append(" ").append(inputs.size())
//                .append(" ").append(latches.size())
//                .append(" ").append(outputs.size())
//                .append(" ").append(gates.size()).append("\n");
//        sb.append(ins.toString());
//        sb.append(latis.toString());
//        sb.append(outs.toString());
//        sb.append(gateStrings.toString());
//        sb.append(symbols.toString());
//        return sb.toString();

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

    private void lists2BaseIdentifiers() {
        for (Gate gate : andGates.values()) {
            Pair<String, Integer> pair = getBaseIdentifier(gate.getIn1(), 0);
            String neg = (pair.getSecond() % 2 == 0) ? "" : "!";
            gate.setIn1(neg + pair.getFirst());
            pair = getBaseIdentifier(gate.getIn2(), 0);
            neg = (pair.getSecond() % 2 == 0) ? "" : "!";
            gate.setIn2(neg + pair.getFirst());
        }
    }

    private void optimizeGatelist() {
        List<Pair<String, Gate>> toRemove = new ArrayList<>();
        do { // if there is still s.th. to remove repeat
            // safely (i.e. replace all the output of the gate using indizes with the replacement) delete all gates
            for (Pair<String, Gate> pair : toRemove) {
                String out = pair.getSecond().getOut();
                boolean test = andGates.remove(out, pair.getSecond());
                if (!test) {
                    throw new RuntimeException("whooooaaat?");
                }
                replace(out, pair.getFirst());
            }
            toRemove.clear();
            // find gates with the same inputs
            for (Gate gate : andGates.values()) {
                if (gate.getIn1().equals(gate.getIn2())) {
//                    toRemove.add(new Pair<>(gate.getIn1(), gate));
                }
            }
            // find gates where one input is zero or one
            // find gates where one input is the negation of the other            
            // find gates which are commutativ or equal to another

        } while (!toRemove.isEmpty());
    }

    private void replace(String id, String with) {
        // in gates
        for (Gate gate : andGates.values()) {
            if (gate.getIn1().equals(id)) {
                gate.setIn1(with);
            }
            if (gate.getIn2().equals(id)) {
                gate.setIn2(with);
            }
            if (gate.getIn1().equals("!" + id)) {
                gate.setIn1("!" + with);
            }
            if (gate.getIn2().equals("!" + id)) {
                gate.setIn2("!" + with);
            }
        }
        // in copy
        for (Map.Entry<String, String> entry : copy.entrySet()) {
            if (entry.getValue().equals(id)) {
                copy.put(entry.getKey(), with);
            }
            if (entry.getValue().equals("!" + id)) {
                copy.put(entry.getKey(), "!" + with);
            }
        }
    }

    private Map<String, Pair<Gate, Integer>> createGateIds(int idx) {
        Map<String, Pair<Gate, Integer>> gates = new HashMap<>();
        for (Map.Entry<String, Gate> entry : andGates.entrySet()) {
            gates.put(entry.getKey(), new Pair<>(entry.getValue(), idx));
            idx += 2;
        }
        return gates;
    }

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
