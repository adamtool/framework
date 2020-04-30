package uniolunisaar.adam.ds.abta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import uniol.apt.adt.exception.StructureException;
import uniol.apt.util.Pair;
import uniolunisaar.adam.ds.abta.posbooleanformula.IPositiveBooleanFormula;
import uniolunisaar.adam.tools.Logger;

/**
 * A deterministic alternating Buchi automaton
 *
 * @author Manuel Gieseking
 * @param <SIGMA>
 */
public class AlternatingBuchiTreeAutomaton<SIGMA> {

    private final Set<SIGMA> alphabet;
    private final Map<String, TreeState> states;
//    private final Map<Pair<String, SIGMA>, Set<TreeEdge<SIGMA>>> edges = new HashMap<>(); // for ndet
    private final Map<Pair<String, SIGMA>, TreeEdge<SIGMA>> edges;
//    private final List<TreeState> buchiStates = new ArrayList<>();
    private final String name;
    private final TreeState initialState;

    public AlternatingBuchiTreeAutomaton(String name, String initialStateId) {
        this.name = name;
        this.alphabet = new HashSet<>();
        this.states = new HashMap<>();
        this.edges = new HashMap<>();
        TreeState init = new TreeState(initialStateId);
        states.put(initialStateId, init);
        this.initialState = init;
    }

    public void setBuchi(TreeState s, boolean buchi) {
        s.setBuchi(true);
    }

    public TreeState createAndAddState(String id) {
        if (states.containsKey(id)) {
            Logger.getInstance().addWarning("A state with the id '" + id + "' already exists. This one is returned.");
            return states.get(id);
        } else {
            TreeState state = new TreeState(id);
            states.put(id, state);
            return state;
        }
    }

    public List<TreeState> createAndAddStates(String... ids) {
        List<TreeState> ret = new ArrayList<>();
        for (String id : ids) {
            if (states.containsKey(id)) {
                Logger.getInstance().addWarning("A state with the id '" + id + "' already exists. This one is returned.");
                ret.add(states.get(id));
            } else {
                TreeState state = new TreeState(id);
                ret.add(state);
                states.put(id, state);
            }
        }
        return ret;
    }

    public TreeEdge<SIGMA> createAndAddEdge(String stateID, SIGMA sigma, int degree, IPositiveBooleanFormula successor) {
        if (states.containsKey(stateID)) {
            TreeEdge<SIGMA> edge = new TreeEdge<>(states.get(stateID), sigma, degree, successor);
            Pair<String, SIGMA> key = new Pair<>(stateID, sigma);
            // for ndet
//                 Set<TreeEdge<SIGMA>> es = edges.get(key);
//            if (es == null) {
//                es = new HashSet<>();
//                edges.put(key, es);
//            }
//            es.add(edge);
            edge = edges.put(key, edge);
            if (!alphabet.contains(sigma)) {
                alphabet.add(sigma);
            }
            return edge;
        } else {
            throw new StructureException("There is no state with ID '" + stateID + "'");
        }
    }

    public TreeEdge<SIGMA> getEdge(String treeStateID, SIGMA sigma) {
        return edges.get(new Pair<>(treeStateID, sigma));
    }

    public String getName() {
        return name;
    }

    public Set<SIGMA> getAlphabet() {
        return alphabet;
    }

    public TreeState getInitialState() {
        return initialState;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("alphabet=" + alphabet + "\n");
        sb.append("q");
        for (Iterator<SIGMA> iterator = alphabet.iterator(); iterator.hasNext();) {
            SIGMA sigma = iterator.next();
            sb.append("          &    delta(q,").append(sigma).append(",k)");
        }
        sb.append("\n");
        for (String stateId : states.keySet()) {
            sb.append(stateId);
            for (Iterator<SIGMA> iterator = alphabet.iterator(); iterator.hasNext();) {
                SIGMA sigma = iterator.next();
                sb.append("           &  ").append(edges.get(new Pair<>(stateId, sigma)).getSuccessor().toString());
            }
            sb.append("\n");
        }
        List<TreeState> buchi = new ArrayList<>();
        for (TreeState state : states.values()) {
            if (state.isBuchi()) {
                buchi.add(state);
            }
        }
        sb.append("F={").append(buchi.toString()).append("}");
        return sb.toString();
    }

}
