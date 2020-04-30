package uniolunisaar.adam.ds.automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import uniol.apt.adt.exception.StructureException;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.util.IDotSaveable;

/**
 *
 * @author Manuel Gieseking
 * @param <S>
 * @param <E>
 */
public abstract class Automaton<S extends IState, E extends IEdge<S>> implements IDotSaveable {

    private final Map<String, S> states; // maps IDs to states
    private final Map<String, Set<E>> edges; // maps place IDs to edges
    private final String name;
    private final Set<S> inits; // initial states

    public abstract S createState(String id);

    public abstract E createEdge(String preId, String postId);

    public Automaton(String name) {
        states = new HashMap<>();
        edges = new HashMap<>();
        this.name = name;
        this.inits = new HashSet<>();
    }

    public Set<S> getInitialStates() {
        return inits;
    }

    public void clearInitialStates() {
        inits.clear();
    }

    public void addInitialState(S state) {
        inits.add(state);
    }

    public S createAndAddState(String id, boolean check) {
        if (check && states.containsKey(id)) {
            Logger.getInstance().addWarning("A state with the id '" + id + "' already exists. This one is returned.");
            return states.get(id);
        } else {
            return createAndAddStateUnchecked(id);
        }
    }

    private S createAndAddStateUnchecked(String id) {
        S state = createState(id);
        states.put(id, state);
        return state;
    }

    public E createAndAddEdge(String preId, String postId, boolean check) {
        if (check) {
            if (!states.containsKey(preId)) {
                throw new StructureException("A state with the id '" + preId + "' does not exists.");
            }
            if (!states.containsKey(postId)) {
                throw new StructureException("A state with the id '" + postId + "' does not exists.");
            }
        }
        E edge = createEdge(preId, postId);
        Set<E> postEdges = edges.get(preId);
        if (postEdges == null) {
            postEdges = new HashSet<>();
            edges.put(preId, postEdges);
        }
        postEdges.add(edge);
        return edge;
    }

    public boolean containsState(String id) {
        return states.containsKey(id);
    }

    public S getState(String id) {
        return states.get(id);
    }

    public Set<E> getPostset(String id) {
        return edges.get(id);
    }

    @Override
    public String toDot() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph Automaton {\n");

        // States
        sb.append("#states\n");
        int counter = 0;
        for (S init : inits) {
            sb.append(this.hashCode()).append("").append(counter++).append(" [label=\"\", shape=point]").append("\n"); // for showing an initial arc
        }
        for (String id : states.keySet()) {
            S state = states.get(id);
            sb.append(state.toDot()).append("\n");
        }

        // Edges
        sb.append("\n#flows\n");
        counter = 0;
        for (S init : inits) {
            sb.append(this.hashCode()).append("").append(counter++).append("->").append(init.getId().hashCode()).append("\n");// add the inits arc
        }
        for (Set<E> es : edges.values()) {
            for (E edge : es) {
                sb.append(edge.toDot()).append("\n");
            }
        }
        sb.append("overlap=false\n");
        sb.append("label=\"").append("Automaton: ").append(name).append("\"\n");
        sb.append("fontsize=12\n\n");
        sb.append("}");
        return sb.toString();
    }
}
