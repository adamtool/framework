package uniolunisaar.adam.logic.transformers.automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import uniol.apt.util.Pair;
import uniolunisaar.adam.ds.automata.BuchiAutomaton;
import uniolunisaar.adam.ds.automata.BuchiState;
import uniolunisaar.adam.ds.automata.LabeledEdge;
import uniolunisaar.adam.ds.automata.StringLabel;

/**
 *
 * @author Manuel Gieseking
 */
public class NDet2DetAutomatonTransformer {

    public static BuchiAutomaton transform(BuchiAutomaton in) {
        BuchiAutomaton out = new BuchiAutomaton("Det_" + in.getName());

        LinkedList<Pair<BuchiState, Set<BuchiState>>> todo = new LinkedList<>();
        // initial states
        for (BuchiState init : in.getInitialStates()) {
            BuchiState state = out.createAndAddState("[" + init.getId() + "]", false);
            out.addInitialState(state);
            if (init.isBuchi()) {
                out.setBuchi(true, state);
            }
            Set<BuchiState> label = new HashSet<>();
            label.add(init);
            todo.add(new Pair<>(state, label));
        }

        while (!todo.isEmpty()) {
            Pair<BuchiState, Set<BuchiState>> state = todo.pop();
            // collect the successor for each label in a set;
            Map<StringLabel, Set<BuchiState>> succ = new HashMap<>();
            for (BuchiState pre : state.getSecond()) { // for each original state add the successors
                List<LabeledEdge<BuchiState, StringLabel>> post = in.getPostset(pre.getId()); // the postset edges of this state
                if (post != null) {
                    for (LabeledEdge<BuchiState, StringLabel> labeledEdge : post) { // each original each
                        Set<BuchiState> newPostOfLabel = succ.get(labeledEdge.getLabel());
                        if (newPostOfLabel == null) {
                            newPostOfLabel = new HashSet<>();
                            succ.put(labeledEdge.getLabel(), newPostOfLabel);
                        }
                        newPostOfLabel.add(labeledEdge.getPost());
                    }
                }
            }
            // create the new successors
            for (Map.Entry<StringLabel, Set<BuchiState>> entry : succ.entrySet()) {
                StringLabel label = entry.getKey();
                Set<BuchiState> successors = entry.getValue();
                String key = successors.toString();
                BuchiState newPost;
                if (!out.containsState(key)) {
                    newPost = out.createAndAddState(key, false);
                    todo.add(new Pair<>(newPost, successors));
                } else {
                    newPost = out.getState(key);
                }
                out.createAndAddEdge(state.getFirst().getId(), label, newPost.getId(), false);
            }
        }

        return out;
    }

}
