package uniolunisaar.adam.tests.abta;

import java.util.HashSet;
import java.util.Set;
import org.testng.annotations.Test;
import uniolunisaar.adam.ds.abta.AlternatingBuchiTreeAutomaton;
import uniolunisaar.adam.ds.abta.TreeDirectionxState;
import uniolunisaar.adam.ds.abta.TreeState;
import uniolunisaar.adam.ds.abta.posbooleanformula.PositiveBooleanFormulaFactory;
import uniolunisaar.adam.ds.abta.posbooleanformula.PositiveBooleanFormulaOperators;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestAlternatingBuchiTreeAutomata {

//       private static final String inputDir = System.getProperty("examplesfolder") + "/forallreachability/";
    @Test
    public void firstTests() {
        // The tree for A(false U' not p)
        AlternatingBuchiTreeAutomaton<Set<String>> abta = new AlternatingBuchiTreeAutomaton<>("A(false U' !p)", "A(false U' !p)");
        abta.createAndAddStates("p", "!p");
//        TreeState phi = abta.createAndAddState("A(false U' !p)");
        TreeState phi = abta.getInitialState();
        abta.setBuchi(phi, true);
        Set<String> emptySet = new HashSet<>();
        Set<String> p = new HashSet<>();
        p.add("p");
        abta.createAndAddEdge("p", emptySet, -1, PositiveBooleanFormulaFactory.createFalse());
        abta.createAndAddEdge("p", p, -1, PositiveBooleanFormulaFactory.createTrue());
        abta.createAndAddEdge("!p", emptySet, -1, PositiveBooleanFormulaFactory.createTrue());
        abta.createAndAddEdge("!p", p, -1, PositiveBooleanFormulaFactory.createFalse());
        abta.createAndAddEdge("A(false U' !p)", emptySet, -1, PositiveBooleanFormulaFactory.createParameterizedPositiveBooleanFormula(-1, PositiveBooleanFormulaOperators.Binary.AND, new TreeDirectionxState(phi, -1)));
        abta.createAndAddEdge("A(false U' !p)", p, -1, PositiveBooleanFormulaFactory.createFalse());

        System.out.println(abta.toString());
    }

}
