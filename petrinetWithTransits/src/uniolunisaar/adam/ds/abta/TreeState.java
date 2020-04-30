package uniolunisaar.adam.ds.abta;

import uniolunisaar.adam.ds.automata.BuchiState;

/**
 *
 * @author Manuel Gieseking
 */
public class TreeState extends BuchiState {

    TreeState(String id) {
        super(id);
    }

    @Override
    protected void setBuchi(boolean buchi) {
        super.setBuchi(buchi);
    }

}
