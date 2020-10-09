package uniolunisaar.adam.ds.petrinetwithtransits;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import uniol.apt.adt.pn.Node;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;

/**
 *
 * @author Manuel Gieseking
 */
public class DataFlowTreeNode {

    private final Node node;
    private DataFlowTreeNode parent;
    private List<DataFlowTreeNode> children;

    public DataFlowTreeNode(DataFlowTreeNode parent, Node node) {
        this.node = node;
        this.parent = parent;
    }

    public Node getNode() {
        return node;
    }

    public void setParent(DataFlowTreeNode parent) {
        this.parent = parent;
    }

    public DataFlowTreeNode getParent() {
        return parent;
    }

    public List<DataFlowTreeNode> getChildren() {
        return children;
    }

    public DataFlowTreeNode addChild(Transition t) {
        if (children == null) {
            children = new ArrayList<>();
        }
        DataFlowTreeNode child = new DataFlowTreeNode(this, t);
        children.add(child);
        return child;
    }

    public void addChildren(Set<Place> postset) {
        if (children == null) {
            children = new ArrayList<>();
        }
        for (Place place : postset) {
            children.add(new DataFlowTreeNode(this, place));
        }
    }
}
