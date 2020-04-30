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
    public String toDot() {
        String dot = super.toDot();
        if (buchi) {
            dot = dot.replace("circle", "doublecircle");
        }
        return dot;
    }

}
