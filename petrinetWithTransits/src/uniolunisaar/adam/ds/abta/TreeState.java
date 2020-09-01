package uniolunisaar.adam.ds.abta;

import uniolunisaar.adam.ds.automata.BuchiState;
import uniolunisaar.adam.ds.automata.ILabeled;

/**
 *
 * @author Manuel Gieseking
 */
public class TreeState extends BuchiState implements ILabeled {

    private String label = "";

    TreeState(String id) {
        super(id);
    }

    @Override
    protected void setBuchi(boolean buchi) {
        super.setBuchi(buchi);
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
