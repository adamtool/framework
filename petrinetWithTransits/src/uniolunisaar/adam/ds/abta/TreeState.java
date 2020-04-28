package uniolunisaar.adam.ds.abta;

import java.util.Objects;

/**
 *
 * @author Manuel Gieseking
 */
public class TreeState {

    private final String id;
    private boolean buchi = false;

    TreeState(String id) {
        this.id = id;
    }

    void setBuchi(boolean buchi) {
        this.buchi = buchi;
    }

    public boolean isBuchi() {
        return buchi;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
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
        final TreeState other = (TreeState) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id;
    }

}
