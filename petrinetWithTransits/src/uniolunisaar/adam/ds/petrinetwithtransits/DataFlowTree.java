package uniolunisaar.adam.ds.petrinetwithtransits;

import uniol.apt.adt.pn.Transition;

/**
 *
 * @author Manuel Gieseking
 */
public class DataFlowTree {

    private DataFlowTreeNode root;

    public DataFlowTree(Transition root) {
        this.root = new DataFlowTreeNode(null, root);
    }

    public DataFlowTreeNode getRoot() {
        return root;
    }

}
