package uniolunisaar.adam.ds.automata;

import java.util.List;

/**
 *
 * @author Manuel Gieseking
 */
public class BuchiAutomaton extends Automaton<BuchiState, LabeledEdge<BuchiState, StringLabel>> {

    public BuchiAutomaton(String name) {
        super(name);
    }

    @Override
    BuchiState createState(String id) {
        return new BuchiState(id);
    }

    @Override
    LabeledEdge<BuchiState, StringLabel> createEdge(String preId, String postId) {
        return new LabeledEdge<>(getState(preId), null, getState(postId));
    }

    public LabeledEdge<BuchiState, StringLabel> createAndAddEdge(String preId, StringLabel label, String postId, boolean check) {
        List<LabeledEdge<BuchiState, StringLabel>> postEdges = super.getPostEdges(preId, postId, check);
        LabeledEdge<BuchiState, StringLabel> edge = new LabeledEdge<>(getState(preId), label, getState(postId));
        postEdges.add(edge);
        return edge;
    }

    public void setBuchi(boolean buchi, BuchiState... states) {
        for (BuchiState state : states) {
            state.setBuchi(buchi);
        }
    }

}
