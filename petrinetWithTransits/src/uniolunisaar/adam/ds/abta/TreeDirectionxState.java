package uniolunisaar.adam.ds.abta;

import java.util.ArrayList;
import java.util.List;
import uniolunisaar.adam.ds.abta.posbooleanformula.IPositiveBooleanFormulaAtom;

/**
 *
 * @author Manuel Gieseking
 */
public class TreeDirectionxState implements IPositiveBooleanFormulaAtom {

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

    public TreeState getState() {
        return state;
    }

    public int getDirection() {
        return direction;
    }

    @Override
    public List<IPositiveBooleanFormulaAtom> getAtoms() {
        List<IPositiveBooleanFormulaAtom> list = new ArrayList<>();
        list.add(this);
        return list;
    }

}
