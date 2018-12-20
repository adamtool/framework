package uniolunisaar.adam.ds.petrinetwithtransits;

import java.util.Collection;
import java.util.HashSet;
import uniol.apt.adt.pn.Node;
import uniol.apt.adt.pn.Place;

/**
 * It's not really a tree, but a set of all places belonging to a token.
 *
 * @author Manuel Gieseking
 */
@Deprecated
public class TokenTree extends HashSet<Place> {

    public static final long serialVersionUID = 0x1l;

//    private Place lastElement = null;
    public TokenTree() {
    }

    public TokenTree(Collection<? extends Place> c) {
        super(c);
    }

//    @Override
//    public boolean add(Place e) {
//        boolean ret = super.add(e);
//        if (ret) {
//            lastElement = e;
//        }
//        return ret;
//    }
//
//    public Place getLastElement() {
//        return lastElement;
//    }
    public void addAllPlaces(TokenChain c) {
        for (Node node : c) {
            if (node instanceof Place) {
                add((Place) node);
            }
        }
    }

    public boolean notEmptyIntersection(Collection<? extends Place> c) {
        for (Place place : c) {
            if (contains(place)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (Node thi : this) {
            sb.append(thi.getId()).append(",");
        }
        sb.append("}");
        return sb.toString();
    }

}
