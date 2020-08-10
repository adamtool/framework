package uniolunisaar.adam.ds.automata;

import uniolunisaar.adam.util.IDotSaveable;

/**
 *
 * @author Manuel Gieseking
 * @param <S>
 */
public interface IEdge<S extends IState> extends IDotSaveable {

    public S getPre();

    public S getPost();

}
