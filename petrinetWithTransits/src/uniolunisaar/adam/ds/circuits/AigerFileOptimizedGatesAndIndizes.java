package uniolunisaar.adam.ds.circuits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import uniol.apt.util.Pair;

/**
 *
 * @author Manuel Gieseking
 */
public class AigerFileOptimizedGatesAndIndizes extends AigerFile {

    private final Map<String, String> mapping = new HashMap<>();
    private final boolean extraList;
    private final boolean withEqCom;
    private int nb_gates = -1;

    public AigerFileOptimizedGatesAndIndizes(boolean withExtraList, boolean withEqCom) {
        this.extraList = withExtraList;
        this.withEqCom = withEqCom;
    }

    @Override
    void putGate(String out, String in1, String in2) {
        andGates.put(out, new Gate(out, in1, in2));
    }

    /**
     * Attention: For optimization reasons this methods changes the andGates
     * Map!
     *
     * @return
     */
    @Override
    StringBuilder renderGates() {
        // crop the gate list to the basic identifiers (remove unnecessary copying and negations)
        gatelist2BaseIdentifiers();
        StringBuilder gateStrings = new StringBuilder();
        if (!extraList) {
            // squize the gate list to only necessary gates (i.e., do some optimizations)
            // all deleted gate identifiers are saved in the mapping list
            optimizeGatelist();
            // indice the left over gates
            int idx = super.getMaxVarIdx() * 2;
            for (Gate gate : andGates.values()) {
                idx += 2;
                gate.setIdx(idx);
            }
            // build the string
            for (Gate gate : andGates.values()) {
                gateStrings.append(gate.getIdx()).append(" ").append(getIndex(gate.getIn1())).append(" ").append(getIndex(gate.getIn2())).append("\n");
            }
        } else {
            // squize the gate list to only necessary gates (i.e., do some optimizations)
            // all deleted gate identifiers are saved in the mapping list
            List<Gate> gates = calculateOptimizeGatelist();
            // indice the left over gates
            int idx = super.getMaxVarIdx() * 2;
            for (Gate gate : gates) {
                idx += 2;
                gate.setIdx(idx);
            }
            // build the string
            for (Gate gate : gates) {
                gateStrings.append(gate.getIdx()).append(" ").append(getIndex(gate.getIn1())).append(" ").append(getIndex(gate.getIn2())).append("\n");
            }
            nb_gates = gates.size();
        }
        return gateStrings;
    }

    private void gatelist2BaseIdentifiers() {
        for (Gate gate : andGates.values()) {
            Pair<String, Integer> pair = getBaseIdentifier(gate.getIn1(), 0);
            String neg = (pair.getSecond() % 2 == 0) ? "" : "!";
            gate.setIn1(neg + pair.getFirst());
            pair = getBaseIdentifier(gate.getIn2(), 0);
            neg = (pair.getSecond() % 2 == 0) ? "" : "!";
            gate.setIn2(neg + pair.getFirst());
        }
    }

    private List<Gate> calculateOptimizeGatelist() {
        List<Gate> gates = new ArrayList<>(super.andGates.values());
        List<Pair<String, Gate>> toRemove = new ArrayList<>();
        do { // if there is still s.th. to remove repeat
            // safely (i.e. replace all the output of the gate using indizes with the replacement) delete all gates
            for (Pair<String, Gate> pair : toRemove) {
                String out = pair.getSecond().getOut();
                gates.remove(pair.getSecond());
                replace(out, pair.getFirst());
                mapping.put(out, pair.getFirst());
            }
            toRemove.clear();
            for (int i = 0; i < gates.size(); i++) {
                Gate gate = gates.get(i);
                if (gate.getIn1().equals(gate.getIn2())) {
                    // find gates with the same inputs
                    toRemove.add(new Pair<>(gate.getIn1(), gate));
                    break;
                } else if (gate.getIn1().equals("!" + gate.getIn2())
                        || gate.getIn2().equals("!" + gate.getIn1())) {
                    // find gates where one input is the negation of the other,
                    // ergo replace with false
                    toRemove.add(new Pair<>(AigerFile.FALSE, gate));
                    break;
                } else if (gate.getIn1().equals(AigerFile.FALSE) || gate.getIn2().equals(AigerFile.FALSE)) {
                    // find gates where one input is zero 
                    toRemove.add(new Pair<>(AigerFile.FALSE, gate));
                    break;
                } else if (gate.getIn1().equals(AigerFile.TRUE)) {
                    // find gates where first input is one
                    toRemove.add(new Pair<>(gate.getIn2(), gate));
                    break;
                } else if (gate.getIn2().equals(AigerFile.TRUE)) {
                    // find gates where second input is one
                    toRemove.add(new Pair<>(gate.getIn1(), gate));
                    break;
                } else if (withEqCom) {
                    // find gates which are commutativ or equal to another
                    for (int j = i + 1; j < gates.size(); j++) {
                        Gate gate1 = gates.get(j);
                        if (((gate1.getIn1().equals(gate.getIn1()) && gate1.getIn2().equals(gate.getIn2())) // check equal
                                || (gate1.getIn1().equals(gate.getIn2()) && gate1.getIn2().equals(gate.getIn1())))) // commutative
                        {
//                            toRemove.add(new Pair<>(gate.getOut(), gate1)); // the higher ids would be preserved
                            toRemove.add(new Pair<>(gate1.getOut(), gate));
                        }
                    }
                    if (!toRemove.isEmpty()) {
                        break;
                    }
                }
            }

        } while (!toRemove.isEmpty());
        return gates;
    }

    private void optimizeGatelist() {
        List<Pair<String, Gate>> toRemove = new ArrayList<>();
        do { // if there is still s.th. to remove repeat
            // safely (i.e. replace all the output of the gate using indizes with the replacement) delete all gates
            for (Pair<String, Gate> pair : toRemove) {
                String out = pair.getSecond().getOut();
                andGates.remove(out, pair.getSecond());
                replace(out, pair.getFirst());
                mapping.put(out, pair.getFirst());
            }
            toRemove.clear();
//            for (Gate gate : andGates.values().) {
            int i = 0;
            for (Iterator<Gate> it = andGates.values().iterator(); it.hasNext();) {
                Gate gate = it.next();
                ++i;
                if (gate.getIn1().equals(gate.getIn2())) {
                    // find gates with the same inputs
                    toRemove.add(new Pair<>(gate.getIn1(), gate));
                    break;
                } else if (gate.getIn1().equals("!" + gate.getIn2())
                        || gate.getIn2().equals("!" + gate.getIn1())) {
                    // find gates where one input is the negation of the other,
                    // ergo replace with false
                    toRemove.add(new Pair<>(AigerFile.FALSE, gate));
                    break;
                } else if (gate.getIn1().equals(AigerFile.FALSE) || gate.getIn2().equals(AigerFile.FALSE)) {
                    // find gates where one input is zero 
                    toRemove.add(new Pair<>(AigerFile.FALSE, gate));
                    break;
                } else if (gate.getIn1().equals(AigerFile.TRUE)) {
                    // find gates where first input is one
                    toRemove.add(new Pair<>(gate.getIn2(), gate));
                    break;
                } else if (gate.getIn2().equals(AigerFile.TRUE)) {
                    // find gates where second input is one
                    toRemove.add(new Pair<>(gate.getIn1(), gate));
                    break;
                } else if (withEqCom) {
                    // find gates which are commutativ or equal to another
                    // could be cheaper to convert this list before the method once and for all 
                    // and then iterate here only over the rest (compare AigerFileOptimizedGates)
                    int j = 0;
                    for (Iterator<Gate> it2 = andGates.values().iterator(); it2.hasNext();) {
                        Gate gate1 = it2.next();
                        ++j;
                        if (j > i) {
                            if (((gate1.getIn1().equals(gate.getIn1()) && gate1.getIn2().equals(gate.getIn2())) // check equal
                                    || (gate1.getIn1().equals(gate.getIn2()) && gate1.getIn2().equals(gate.getIn1())))) // commutative
                            {
//                            toRemove.add(new Pair<>(gate.getOut(), gate1)); // the higher ids would be preserved
                                if (!toRemove.contains(new Pair<>(gate.getOut(), gate1))) { // this now ensures that the commutative part is not also added.
                                    toRemove.add(new Pair<>(gate1.getOut(), gate));
                                }
                            }
                        }
                    }
                    if (!toRemove.isEmpty()) {
                        break;
                    }
                }
            }

        } while (!toRemove.isEmpty());
    }

    private void replace(String id, String with) {
        String negWith = (with.equals(AigerFile.TRUE)) ? AigerFile.FALSE
                : (with.equals(AigerFile.FALSE)) ? AigerFile.TRUE
                : (with.startsWith("!")) ? with.substring(1) : "!" + with;
//        String negWith = "!" + with;
//        System.out.println(negWith);
        // in gates
        for (Gate gate : andGates.values()) {
            if (gate.getIn1().equals(id)) {
                gate.setIn1(with);
            }
            if (gate.getIn2().equals(id)) {
                gate.setIn2(with);
            }
            if (gate.getIn1().equals("!" + id)) {
                gate.setIn1(negWith);
            }
            if (gate.getIn2().equals("!" + id)) {
                gate.setIn2(negWith);
            }
        }
        // in copy
        for (Map.Entry<String, String> entry : copy.entrySet()) {
            if (entry.getValue().equals(id)) {
                copy.put(entry.getKey(), with);
            }
        }
        // in replacement
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            if (entry.getValue().equals(id)) {
                mapping.put(entry.getKey(), with);
            }
        }
    }

    @Override
    public int getNbOfGates() {
        if (nb_gates == -1) { // we optimized the currentlist (extraList==false)
            return andGates.size();
        }
        return nb_gates;
    }

    @Override
    int getMaxVarIdx() {
        return super.getMaxVarIdx() + getNbOfGates();
    }

    @Override
    int getIndex(String identifier) {
        if (mapping.containsKey(identifier)) {
            identifier = mapping.get(identifier);
        }
        return super.getIndex(identifier);
    }

    @Override
    int getGateIndex(String identifier) {
        if (mapping.containsKey(identifier)) {
            identifier = mapping.get(identifier);
        }
        if (!andGates.containsKey(identifier)) {
            return -1;
        }
        return andGates.get(identifier).getIdx();
    }

}
