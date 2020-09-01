package uniolunisaar.adam.ds.abta.posbooleanformula;

/**
 *
 * @author Manuel Gieseking
 */
public class PositiveBooleanFormulaOperators {

    public enum Binary {
        AND {
            @Override
            public String toString() {
                return "⋏"; //" \u22CF " " \u2227 ";
            }

        },
        OR {
            @Override
            public String toString() {
                return "⋎"; //" \u22CE " " \u2228 "
            }

        }
    }
}
