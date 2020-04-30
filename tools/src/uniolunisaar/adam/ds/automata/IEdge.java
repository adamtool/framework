package uniolunisaar.adam.ds.automata;

/**
 *
 * @author Manuel Gieseking
 * @param <S>
 */
public interface IEdge<S extends IState> {

    public S getPre();

    public S getPost();

}
