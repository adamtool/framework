package uniolunisaar.adam.ds.automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import uniol.apt.adt.exception.StructureException;
import uniolunisaar.adam.tools.Logger;

/**
 *
 * @author Manuel Gieseking
 * @param <S>
 * @param <E>
 */
public abstract class Automaton<S extends IState, E extends IEdge<S>> {

    private final Map<String, S> states; // maps IDs to states
    private final Map<String, Set<E>> edges; // maps place IDs to edges

    public abstract S createState(String id);

    public abstract E createEdge(String preId, String postId);

    public Automaton() {
        states = new HashMap<>();
        edges = new HashMap<>();
    }

    public S createAndAddState(String id) {
        if (states.containsKey(id)) {
            Logger.getInstance().addWarning("A state with the id '" + id + "' already exists. This one is returned.");
            return states.get(id);
        } else {
            S state = createState(id);
            states.put(id, state);
            return state;
        }
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
}
