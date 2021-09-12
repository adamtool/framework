package uniolunisaar.adam.ds.abta.posbooleanformula;

import java.util.HashSet;
import java.util.Set;

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
    public Set<IPositiveBooleanFormulaAtom> getAtoms() {
        return new HashSet<>();
    }
}
