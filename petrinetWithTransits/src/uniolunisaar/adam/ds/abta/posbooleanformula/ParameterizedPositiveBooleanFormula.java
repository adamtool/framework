package uniolunisaar.adam.ds.abta.posbooleanformula;

import java.util.List;

/**
 *
 * @author Manuel Gieseking
 */
public class ParameterizedPositiveBooleanFormula implements IPositiveBooleanFormula {

    private final int bound;
    private final PositiveBooleanFormulaOperators.Binary op;
    private final IPositiveBooleanFormula element;

    ParameterizedPositiveBooleanFormula(int bound, PositiveBooleanFormulaOperators.Binary op, IPositiveBooleanFormula element) {
        this.bound = bound;
        this.op = op;
        this.element = element;
    }

    @Override
    public String toString() {
        String boundText = "" + bound;
        if (bound == -1) {
            boundText = "k-1";
        }
        return op.toString() + "_c=0^" + boundText + " " + element.toString();
    }

    public int getBound() {
        return bound;
    }

    public PositiveBooleanFormulaOperators.Binary getOp() {
        return op;
    }

    public IPositiveBooleanFormula getElement() {
        return element;
    }

    @Override
    public List<IPositiveBooleanFormulaAtom> getAtoms() {
        return element.getAtoms();
    }

}
