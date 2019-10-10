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
public abstract class AigerFile {

    public static final String NEW_VALUE_OF_LATCH_SUFFIX = "_#new#";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    private final Map<String, Integer> inputs = new HashMap<>();
    private final Map<String, Integer> uncontrollable_inputs = new HashMap<>();
    private final Map<String, Integer> latches = new HashMap<>();
    final Map<String, Gate> andGates = new HashMap<>();
    final List<String> outputs = new ArrayList<>();
    final Map<String, String> copy = new HashMap<>();
    int idx = 2;
    private static int uniqueIdentifier = 0;

    abstract void putGate(String out, String in1, String in2);

    public abstract int getNbOfGates();

    abstract int getGateIndex(String identifier);

    abstract StringBuilder renderGates();

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
                putGate(internalOut, in1, it.next());
                in1 = internalOut;
            }
        }
    }

    public void copyValues(String to, String from) {
        copy.put(to, from);
    }

    public String render(boolean withSymbols) {
        // render the possibly optimized gates (do it already here because this could also
        // mean that the output list and the latches ids must have been adapted)
        StringBuilder gateStrings = renderGates();
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
            latis.append(value).append(" ").append(getIndex(key + NEW_VALUE_OF_LATCH_SUFFIX)).append("\n");
            symbols.append("l").append(i++).append(" ").append(key).append("\n");
//            latis.append(value).append(" ").append(getIndex(key)).append("\n");
        }
        //outputs
        StringBuilder outs = new StringBuilder();
        for (int j = 0; j < outputs.size(); j++) {
            outs.append(getIndex(outputs.get(j))).append("\n");
            symbols.append("o").append(j).append(" ").append(outputs.get(j)).append("\n");
        }
        if (!outputs.isEmpty()) {
            symbols.deleteCharAt(symbols.lastIndexOf("\n"));
        }

        int maxVarIdx = getMaxVarIdx();
        StringBuilder sb = new StringBuilder();
        sb.append("aag ").append(maxVarIdx).append(" ").append(inputs.size())
                .append(" ").append(latches.size())
                .append(" ").append(outputs.size())
                .append(" ").append(getNbOfGates()).append("\n");
        sb.append(ins.toString());
        sb.append(latis.toString());
        sb.append(outs.toString());
        sb.append(gateStrings.toString());
        if (withSymbols) {
            sb.append(symbols.toString());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return render(true);
    }

    int getMaxVarIdx() {
        return idx / 2 - 1;
    }

    int getIndex(String identifier) {
        Pair<String, Integer> pair = getBaseIdentifier(identifier, 0);
        identifier = pair.getFirst();
        int sub = (pair.getSecond() % 2 == 0) ? 0 : -1;
        if (identifier.equals(TRUE)) {
            return 1 - sub;
        } else if (identifier.equals(FALSE)) {
            return 0 - sub;
        }
        int output;
        int gateIdx = getGateIndex(identifier);
        if (inputs.containsKey(identifier)) {
            output = inputs.get(identifier);
        } else if (latches.containsKey(identifier)) {
            output = latches.get(identifier);
        } else if (gateIdx != -1) {
            output = gateIdx;
        } else {
            throw new RuntimeException("Couldn't find an index for identifier " + identifier);
        }
        return output - sub;
    }

    Pair<String, Integer> getBaseIdentifier(String identifier, int countNegations) {
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

    Map<String, Integer> getInputs() {
        return inputs;
    }

    Map<String, Integer> getLatches() {
        return latches;
    }

    public Set<String> getInputNames() {
        return inputs.keySet();
    }
}
