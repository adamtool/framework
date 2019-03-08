package uniolunisaar.adam.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Inspired by https://dzone.com/articles/java-cartesian-iterator-array
 *
 * @author Manuel Gieseking
 */
public class CartesianProduct<T> implements Iterable<List<T>> {

    private final List<List<T>> lists;

    public CartesianProduct(List<List<T>> lists) {
        this.lists = lists;
    }

    @Override
    public Iterator<List<T>> iterator() {
        return new CartesianProductIterator();
    }

    class CartesianProductIterator implements Iterator<List<T>> {

        private final int[] idxs;
        private final List<T> currentTuple;

        public CartesianProductIterator() {
            idxs = new int[lists.size()];
            currentTuple = new ArrayList<>(idxs.length);
            for (int i = 0; i < idxs.length - 1; i++) {
                idxs[i] = 0;
                currentTuple.add(lists.get(i).get(0));
            }
            int lastIdx = idxs.length - 1;
            idxs[lastIdx] = -1;
            currentTuple.add(lists.get(lastIdx).get(0));
        }

        @Override
        public boolean hasNext() {
            for (int i = 0; i < idxs.length; i++) {
                if (idxs[i] < lists.get(i).size() - 1) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<T> next() {
            // Find the first list which still has a successor
            int pos = -1;
            for (int i = idxs.length - 1; i >= 0; i--) {
                if (idxs[i] < lists.get(i).size() - 1) {
                    pos = i;
                    break;
                }
            }
            if (pos == -1) {
                throw new NoSuchElementException();
            }
            // from this index set all other lists to the first index
            for (int i = pos + 1; i < idxs.length; i++) {
                idxs[i] = 0;
            }
            // for the list itself do one step
            idxs[pos]++;
            // update the current tuple
            currentTuple.set(pos, lists.get(pos).get(idxs[pos]));
            return currentTuple;
        }

    }
}
