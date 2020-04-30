package uniolunisaar.adam.ds.automata;

/**
 *
 * @author Manuel Gieseking
 */
public class BuchiState extends State {

    private boolean buchi = false;

    public BuchiState(String id) {
        super(id);
    }

    protected void setBuchi(boolean buchi) {
        this.buchi = buchi;
    }

    public boolean isBuchi() {
        return buchi;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 79 * hash + (this.buchi ? 1 : 0);
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
        final BuchiState other = (BuchiState) obj;
        if (this.buchi != other.buchi || !getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

}
