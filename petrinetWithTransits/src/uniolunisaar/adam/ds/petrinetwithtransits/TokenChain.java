package uniolunisaar.adam.ds.petrinetwithtransits;

import java.util.Collection;
import java.util.LinkedList;
import uniol.apt.adt.pn.Node;

/**
 *
 * @author Manuel Gieseking //
 */
////public class TokenChain extends LinkedHashSet<Node> {
@Deprecated
public class TokenChain extends LinkedList<Node> {

    public static final long serialVersionUID = 0x1l;
//    private Node lastElement = null;
    private boolean finished = false;

    public TokenChain() {
    }

    public TokenChain(Collection<? extends Node> c) {
        super(c);
    }

    public TokenChain(TokenChain c) {
        super(c);
        this.finished = c.finished;
    }
//
//    @Override
//    public boolean add(Node e) {
//        boolean ret = super.add(e);
//        if (ret) {
//            lastElement = e;
//        }
//        return ret;
//    }
//
//    public Node getLastElement() {
//        return lastElement;
//    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<");
        for (Node thi : this) {
            sb.append(thi.getId()).append(",");
        }
        sb.append(">");
        return sb.toString();
    }

}
