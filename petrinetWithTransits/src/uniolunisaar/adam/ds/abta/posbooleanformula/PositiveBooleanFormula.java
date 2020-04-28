package uniolunisaar.adam.ds.abta.posbooleanformula;

/**
 *
 * @author Manuel Gieseking
 */
public class PositiveBooleanFormula implements IPositiveBooleanFormula {

    private final IPositiveBooleanFormula phi;

    public PositiveBooleanFormula(IPositiveBooleanFormula phi1, PositiveBooleanFormulaOperators.Binary op, IPositiveBooleanFormula phi2) {
        phi = new PositiveBooleanFormulaBinary(phi1, op, phi2);
    }

}
