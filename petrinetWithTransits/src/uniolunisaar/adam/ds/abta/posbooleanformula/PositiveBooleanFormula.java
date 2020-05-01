package uniolunisaar.adam.ds.abta.posbooleanformula;

import java.util.List;

/**
 *
 * @author Manuel Gieseking
 */
@Deprecated
public class PositiveBooleanFormula implements IPositiveBooleanFormula {

    private final IPositiveBooleanFormula phi;

    PositiveBooleanFormula(IPositiveBooleanFormula phi1, PositiveBooleanFormulaOperators.Binary op, IPositiveBooleanFormula phi2) {
        phi = new PositiveBooleanFormulaBinary(phi1, op, phi2);
    }

    @Override
    public List<IPositiveBooleanFormulaAtom> getAtoms() {
        return phi.getAtoms();
    }

}
