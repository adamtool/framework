package uniolunisaar.adam.ds.abta.posbooleanformula;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Manuel Gieseking
 */
public class PositiveBooleanConstants implements IPositiveBooleanFormula {

    public static class True extends PositiveBooleanConstants {

        True() {
        }

        public String toSymbolString() {
            return "⊤";// "\u22A4";
        }

        @Override
        public String toString() {
            return "TRUE";
        }
    }

    public static class False extends PositiveBooleanConstants {

        False() {
        }

        public String toSymbolString() {
            return "⊥";// "\u22A5";
        }

        @Override
        public String toString() {
            return "FALSE";
        }

    }

    @Override
    public List<IPositiveBooleanFormulaAtom> getAtoms() {
        return new ArrayList<>();
    }
}
