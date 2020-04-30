package uniolunisaar.adam.ds.automata;

import java.util.Objects;

/**
 *
 * @author Manuel Gieseking
 */
public class State implements IState {

    private final String id;

    public State(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String toDot() {
        StringBuilder sb = new StringBuilder();
        String color = "black";
        sb.append(id.hashCode()).append("[shape=circle, color=").append(color);
        sb.append(", height=0.5, width=0.5, fixedsize=false,  penwidth=").append(1);
        sb.append(", label=\"").append(id).append("\"");
        sb.append("];");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.id.hashCode();
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
        final State other = (State) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
