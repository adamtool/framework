package uniolunisaar.adam.ds.abta.posbooleanformula;

/**
 *
 * @author Manuel Gieseking
 */
public class PositiveBooleanConstants implements IPositiveBooleanFormula {

    public static class True extends PositiveBooleanConstants {

        public String toSymbolString() {
            return "⊤";// "\u22A4";
        }

        @Override
        public String toString() {
            return "TRUE";
        }
    }

    public static class False extends PositiveBooleanConstants {

        public String toSymbolString() {
            return "⊥";// "\u22A5";
        }

        @Override
        public String toString() {
            return "FALSE";
        }

    }
}
