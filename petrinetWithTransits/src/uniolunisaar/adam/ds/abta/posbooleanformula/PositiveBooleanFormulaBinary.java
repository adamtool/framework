package uniolunisaar.adam.ds.abta.posbooleanformula;

/**
 *
 * @author Manuel Gieseking
 */
public class PositiveBooleanFormulaBinary implements IPositiveBooleanFormula {

    private final IPositiveBooleanFormula phi1;
    private final PositiveBooleanFormulaOperators.Binary op;
    private final IPositiveBooleanFormula phi2;

    public PositiveBooleanFormulaBinary(IPositiveBooleanFormula phi1, PositiveBooleanFormulaOperators.Binary op, IPositiveBooleanFormula phi2) {
        this.phi1 = phi1;
        this.op = op;
        this.phi2 = phi2;
    }

    @Override
    public String toString() {
        return phi1.toString() + op.toString()+ phi2.toString();
    }

}
