package uniolunisaar.adam.ds.abta.posbooleanformula;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Manuel Gieseking
 */
public class PositiveBooleanFormulaFactory {

    public static IPositiveBooleanFormula createBinaryFormula(PositiveBooleanFormulaOperators.Binary op, IPositiveBooleanFormula... phis) {
        // reduce it regarding the trues and falses
        List<IPositiveBooleanFormula> phis_red = new ArrayList<>();
        for (int i = 0; i < phis.length; i++) {
            if (phis[i] instanceof PositiveBooleanConstants.True) {
                if (op == PositiveBooleanFormulaOperators.Binary.AND) {
                    continue;
                } else {
                    return createTrue();
                }
            } else if (phis[i] instanceof PositiveBooleanConstants.False) {
                if (op == PositiveBooleanFormulaOperators.Binary.AND) {
                    return createFalse();
                } else {
                    continue;
                }
            }
            phis_red.add(phis[i]);
        }
        // special small cases
        if (phis_red.isEmpty()) {
            if (op == PositiveBooleanFormulaOperators.Binary.AND) {
                return createTrue();
            } else {
                return createFalse();
            }
        }
        if (phis_red.size() == 1) {
            return phis_red.get(0);
        }

        //general case
        IPositiveBooleanFormula last = createBinaryFormula(phis_red.get(0), op, phis_red.get(1));
        for (int i = 2; i < phis_red.size(); i++) {
            last = createBinaryFormula(phis_red.get(i), op, last);
        }
        return last;
    }

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
