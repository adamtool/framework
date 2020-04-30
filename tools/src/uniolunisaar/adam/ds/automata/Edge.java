package uniolunisaar.adam.ds.automata;

import java.util.Objects;

/**
 *
 * @author Manuel Gieseking
 * @param <S>
 */
public class Edge<S extends IState> implements IEdge<S> {

    private final S pre;
    private final S post;

    public Edge(S pre, S post) {
        this.pre = pre;
        this.post = post;
    }

    @Override
    public S getPre() {
        return pre;
    }

    @Override
    public S getPost() {
        return post;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.pre.hashCode();
        hash = 67 * hash + this.post.hashCode();
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
        final Edge<?> other = (Edge<?>) obj;
        if (!Objects.equals(this.pre, other.pre)) {
            return false;
        }
        if (!Objects.equals(this.post, other.post)) {
            return false;
        }
        return true;
    }

    @Override
    public String toDot() {
        StringBuilder sb = new StringBuilder();
        sb.append(getPre().getId().hashCode()).append("->").append(getPost().getId().hashCode());
        return sb.toString();
    }

}
