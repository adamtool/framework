package uniolunisaar.adam.ds.abta;

import uniolunisaar.adam.ds.abta.posbooleanformula.IPositiveBooleanFormula;

/**
 *
 * @author Manuel Gieseking
 */
public class TreeDirectionxState implements IPositiveBooleanFormula {

    private final TreeState state;
    private final int direction;

    public TreeDirectionxState(TreeState state, int direction) {
        this.state = state;
        this.direction = direction;
    }

    @Override
    public String toString() {
        String directionText = "" + direction;
        if (direction == -1) {
            directionText = "c";
        }
        return "(" + directionText + "," + state.toString() + ")";
    }

}
