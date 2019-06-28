package uniolunisaar.adam.ds.circuits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uniol.apt.util.Pair;

/**
 *
 * @author Manuel Gieseking
 */
public class AigerFileOptimizedGates extends AigerFile {

    private final Map<String, Pair<Gate, Integer>> andGates = new HashMap<>();
    private boolean withOpt = true; // currently with true the testsuite takes 6s longer
    private int nb_gates = -1;

    private List<IntGate> getIntGates() {
        List<IntGate> gates = new ArrayList<>();
        for (Map.Entry<String, Pair<Gate, Integer>> entry : andGates.entrySet()) {
            Pair<Gate, Integer> value = entry.getValue();
            gates.add(new IntGate(value.getSecond(), getIndex(value.getFirst().getIn1()), getIndex(value.getFirst().getIn2())));
        }
        return gates;
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
        List<IntGate> gates = getIntGates();
        if (withOpt) {
            gates = optimizeGates(gates);
        }

        return gates;
    }

    @Override
    int getGateIndex(String identifier) {
        if (andGates.containsKey(identifier)) {
            return andGates.get(identifier).getSecond();
        }
        return -1;
    }

    /**
     * Currently, we only replace gates which have twice the same input.
     *
     * The problem is that we reduce the number of gates, i.e., lines we write
     * into the file, but do not reduce the number of indices we use.
     *
     * @param gates
     * @return
     */
    private List<IntGate> optimizeGates(List<IntGate> gates) {
        List<Pair<Integer, IntGate>> toRemove = new ArrayList<>();
        do { // if there is still s.th. to remove repeat
            // safely (i.e. replace all the output of the gate using indizes with the replacement) delete all gates
            for (Pair<Integer, IntGate> pair : toRemove) {
                gates.remove(pair.getSecond());
                int out = pair.getSecond().out;
                int replace = pair.getFirst();
                for (IntGate gate : gates) {
                    if (gate.in1 == out) {
                        gate.in1 = replace;
                    }
                    if (gate.in2 == out) {
                        gate.in2 = replace;
                    }
                }
            }
            toRemove.clear();
            for (IntGate gate : gates) {
                // find gates with the same inputs
                if (gate.in1 == gate.in2) {
                    toRemove.add(new Pair<>(gate.out, gate));
                }
                // gates with in2 = !in1, ergo replace with false
            }
        } while (!toRemove.isEmpty());
        nb_gates = gates.size();
        return gates;
    }

    @Override
    public int getNbOfGates() {
        if (nb_gates == -1) {
            return andGates.entrySet().size();
        }
        return nb_gates;
    }

    @Override
    int getMaxVarIdx(List<IntGate> gates) {
        if (withOpt) {
            // cannot do return inputs.size() + latches.size() + getNbOfGates();
            // since we don't squash the indexes
            int max = 0;
            for (Integer value : getInputs().values()) {
                if (value > max) {
                    max = value;
                }
            }
            for (IntGate gate : gates) {
                if (gate.out > max) {
                    max = gate.out;
                }
            }
            return max / 2;
        }
        return super.getMaxVarIdx(gates);
    }

    @Override
    void putGate(String out, String in1, String in2) {
        andGates.put(out, new Pair<>(new Gate(out, in1, in2), idx));
        idx += 2;
    }

    public void setWithOpt(boolean withOpt) {
        this.withOpt = withOpt;
    }

}
