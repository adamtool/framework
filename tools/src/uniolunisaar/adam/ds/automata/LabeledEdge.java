package uniolunisaar.adam.ds.automata;

import java.util.Objects;

/**
 *
 * @author Manuel Gieseking
 * @param <L>
 * @param <S>
 */
public class LabeledEdge<S extends IState, L extends ILabel> extends Edge<S> implements ILabeledEdge<S, L> {

    private L label;

    public LabeledEdge(S pre, L label, S post) {
        super(pre, post);
        this.label = label;
    }

    public void setLabel(L label) {
        this.label = label;
    }

    @Override
    public L getLabel() {
        return label;
    }

    @Override
    public String toDot() {
        StringBuilder sb = new StringBuilder(super.toDot());
        sb.append("[label=\"").append(label.toString()).append("\"]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 19 * hash + this.label.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LabeledEdge<?, ?> other = (LabeledEdge<?, ?>) obj;
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.getPre(), other.getPre())) {
            return false;
        }
        if (!Objects.equals(this.getPost(), other.getPost())) {
            return false;
        }

        return true;
    }

}
