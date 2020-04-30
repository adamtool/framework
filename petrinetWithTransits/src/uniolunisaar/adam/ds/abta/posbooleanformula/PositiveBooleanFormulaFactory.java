package uniolunisaar.adam.ds.abta.posbooleanformula;

/**
 *
 * @author Manuel Gieseking
 */
public class PositiveBooleanFormulaFactory {

    public static IPositiveBooleanFormula createBinaryFormula(IPositiveBooleanFormula phi1, PositiveBooleanFormulaOperators.Binary op, IPositiveBooleanFormula phi2) {
        if (phi1 instanceof PositiveBooleanConstants.True || phi2 instanceof PositiveBooleanConstants.False) {
            if (op == PositiveBooleanFormulaOperators.Binary.OR) {
                return phi1;
            } else {
                return phi2;
            }
        } else if (phi2 instanceof PositiveBooleanConstants.True || phi1 instanceof PositiveBooleanConstants.False) {
            if (op == PositiveBooleanFormulaOperators.Binary.OR) {
                return phi2;
            } else {
                return phi1;
            }
        }
        return new PositiveBooleanFormulaBinary(phi1, op, phi2);
    }

    public static PositiveBooleanConstants.True createTrue() {
        return new PositiveBooleanConstants.True();
    }

    public static PositiveBooleanConstants.False createFalse() {
        return new PositiveBooleanConstants.False();
    }

    public static ParameterizedPositiveBooleanFormula createParameterizedPositiveBooleanFormula(int bound, PositiveBooleanFormulaOperators.Binary op, IPositiveBooleanFormula element) {
        return new ParameterizedPositiveBooleanFormula(bound, op, element);
    }

}
