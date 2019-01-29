package uniolunisaar.adam.ds.circuits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import uniol.apt.util.Pair;
import uniolunisaar.adam.tools.Logger;

/**
 *
 * @author Manuel Gieseking
 */
public class AigerFile {

    public static final String NEW_VALUE_OF_LATCH_SUFFIX = "_#new#";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    private final Map<String, Integer> inputs = new HashMap<>();
    private final Map<String, Integer> uncontrollable_inputs = new HashMap<>();
    private final Map<String, Integer> latches = new HashMap<>();
    private final List<String> outputs = new ArrayList<>();
    private final Map<String, Gate> andGates = new HashMap<>();
    private final Map<String, String> copy = new HashMap<>();
    private int idx = 2;
    private static int uniqueIdentifier = 0;

    private class IntGate {

        int in1;
        int in2;
        int out;

        public IntGate(int out, int in1, int in2) {
            this.in1 = in1;
            this.in2 = in2;
            this.out = out;
        }

    }

    public void addInput(String in) {
        inputs.put(in, idx);
        idx += 2;
    }

    public void addInputs(String... in) {
        for (int i = 0; i < in.length; i++) {
            addInput(in[i]);
        }
    }

    public void addUncontrollableInput(String in) {
        uncontrollable_inputs.put(in, idx);
        idx += 2;
    }

    public void addLatch(String latch) {
        latches.put(latch, idx);
        idx += 2;
    }

    public void addLatches(String... latches) {
        for (int i = 0; i < latches.length; i++) {
            addLatch(latches[i]);
        }
    }

    public boolean addOutput(String out) {
        return outputs.add(out);
    }

    public void addOutputs(String... out) {
        for (int i = 0; i < out.length; i++) {
            addOutput(out[i]);
        }
    }

    public void addGate(String out, String... in) {
        // first delete dublicates
        Set<String> ids = new HashSet<>(Arrays.asList(in));
        if (ids.isEmpty()) {
            copyValues(out, TRUE);
            Logger.getInstance().addMessage("[WARNING] Created gates without inputs. Output: " + out, true);
        } else if (ids.size() == 1) {
            copyValues(out, in[0]);
        } else {
            Iterator<String> it = ids.iterator();
            String in1 = it.next();
            for (int i = 1; i < ids.size(); i++) {
                String internalOut = (i == (ids.size() - 1)) ? out : out + "_#" + uniqueIdentifier++;
                andGates.put(internalOut, new Gate(internalOut, in1, it.next()));
                in1 = internalOut;
            }
        }
    }

    public void copyValues(String to, String from) {
        copy.put(to, from);
    }

    @Override
    public String toString() {
//        lists2BaseIdentifiers();
//        optimizeGatelist();
        Map<String, Pair<Gate, Integer>> gates = createGateIds(idx);
//          System.out.println(this.andGates.toString());
        StringBuilder symbols = new StringBuilder();
        // inputs
        StringBuilder ins = new StringBuilder();
        int i = 0;
//        // uncontrollable
//        for (Map.Entry<String, Integer> entry : uncontrollable_inputs.entrySet()) {
//            String key = entry.getKey();
//            Integer value = entry.getValue();
//            ins.append(value).append("\n");
//            symbols.append("i").append(i++).append(" ").append(key).append("\n");
//        }
        // controllable
        for (Map.Entry<String, Integer> entry : inputs.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            ins.append(value).append("\n");
            symbols.append("i").append(i++).append(" ").append(key).append("\n");
        }
        // latches
        StringBuilder latis = new StringBuilder();
        i = 0;
        for (Map.Entry<String, Integer> entry : latches.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            latis.append(value).append(" ").append(getIndex(key + NEW_VALUE_OF_LATCH_SUFFIX, gates)).append("\n");
            symbols.append("l").append(i++).append(" ").append(key).append("\n");
//            latis.append(value).append(" ").append(getIndex(key)).append("\n");
        }
        //outputs
        StringBuilder outs = new StringBuilder();
        for (int j = 0; j < outputs.size(); j++) {
            outs.append(getIndex(outputs.get(j), gates)).append("\n");
            symbols.append("o").append(j).append(" ").append(outputs.get(j)).append("\n");
        }
        if (!outputs.isEmpty()) {
            symbols.deleteCharAt(symbols.lastIndexOf("\n"));
        }
        // gates
        //   OLD VERSION: directly output the set of gates
        StringBuilder gateStrings = new StringBuilder();
        for (Map.Entry<String, Pair<Gate, Integer>> entry : gates.entrySet()) {
            Pair<Gate, Integer> value = entry.getValue();
            gateStrings.append(value.getSecond()).append(" ").append(getIndex(value.getFirst().getIn1(), gates)).append(" ").append(getIndex(value.getFirst().getIn2(), gates)).append("\n");
        }

        StringBuilder sb = new StringBuilder();
        int total = ((idx - 2) + gates.size() * 2) / 2;
        sb.append("aag ").append(total).append(" ").append(inputs.size())
                .append(" ").append(latches.size())
                .append(" ").append(outputs.size())
                .append(" ").append(gates.size()).append("\n");
        sb.append(ins.toString());
        sb.append(latis.toString());
        sb.append(outs.toString());
        sb.append(gateStrings.toString());
        sb.append(symbols.toString());
        return sb.toString();
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
//
//    private List<IntGate> getIntGates() {
//        List<IntGate> gates = new ArrayList<>();
//        for (Map.Entry<String, Pair<Gate, Integer>> entry : andGates.entrySet()) {
//            Pair<Gate, Integer> value = entry.getValue();
//            gates.add(new IntGate(value.getSecond(), getIndex(value.getFirst().getIn1()), getIndex(value.getFirst().getIn2())));
//        }
//        return gates;
//    }

    private int getIndex(String identifier, Map<String, Pair<Gate, Integer>> gates) {
        Pair<String, Integer> pair = getBaseIdentifier(identifier, 0);
        identifier = pair.getFirst();
        int sub = (pair.getSecond() % 2 == 0) ? 0 : -1;
        if (identifier.equals(TRUE)) {
            return 1 - sub;
        } else if (identifier.equals(FALSE)) {
            return 0 - sub;
        }
        int output;
        if (inputs.containsKey(identifier)) {
            output = inputs.get(identifier);
        } else if (latches.containsKey(identifier)) {
            output = latches.get(identifier);
        } else if (andGates.containsKey(identifier)) {
            output = gates.get(identifier).getSecond();
        } else {
            throw new RuntimeException("Couldn't find an index for identifier " + identifier);
        }
        return output - sub;
    }

    private Pair<String, Integer> getBaseIdentifier(String identifier, int countNegations) {
        while (identifier.startsWith("!")) {
            identifier = identifier.substring(1);
            ++countNegations;
        }
        if (copy.containsKey(identifier)) {
            identifier = copy.get(identifier);
            return getBaseIdentifier(identifier, countNegations);// todo: could be looping by bad setting of copy
        } else {
            return new Pair<>(identifier, countNegations);
        }
    }

    public int getNbOfLatches() {
        return latches.size();
    }

    public int getNbOfGates() {
        return andGates.entrySet().size();
    }

}
