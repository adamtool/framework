package uniolunisaar.adam.ds.automata;

/**
 *
 * @author Manuel Gieseking
 * @param <S>
 * @param <L>
 */
public interface ILabeledEdge<S extends IState, L extends ILabel> extends IEdge<S> {

    public L getLabel();
}
