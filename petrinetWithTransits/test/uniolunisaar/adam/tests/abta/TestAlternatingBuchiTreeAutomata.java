package uniolunisaar.adam.tests.abta;

import java.util.HashSet;
import java.util.Set;
import org.testng.annotations.Test;
import uniolunisaar.adam.ds.abta.AlternatingBuchiTreeAutomaton;
import uniolunisaar.adam.ds.abta.TreeDirectionxState;
import uniolunisaar.adam.ds.abta.TreeState;
import uniolunisaar.adam.ds.abta.posbooleanformula.ParameterizedPositiveBooleanFormula;
import uniolunisaar.adam.ds.abta.posbooleanformula.PositiveBooleanConstants;
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
        AlternatingBuchiTreeAutomaton<Set<String>> abta = new AlternatingBuchiTreeAutomaton<>();
        abta.createAndAddStates("p", "!p");
        TreeState phi = abta.createAndAddState("A(false U' !p)");
        abta.setBuchi(phi, true);
        Set<String> emptySet = new HashSet<>();
        Set<String> p = new HashSet<>();
        p.add("p");
        abta.createAndAddEdge("p", emptySet, -1, new PositiveBooleanConstants.False());
        abta.createAndAddEdge("p", p, -1, new PositiveBooleanConstants.True());
        abta.createAndAddEdge("!p", emptySet, -1, new PositiveBooleanConstants.True());
        abta.createAndAddEdge("!p", p, -1, new PositiveBooleanConstants.False());
        abta.createAndAddEdge("A(false U' !p)", emptySet, -1, new ParameterizedPositiveBooleanFormula(-1, PositiveBooleanFormulaOperators.Binary.AND, new TreeDirectionxState(phi, -1)));
        abta.createAndAddEdge("A(false U' !p)", p, -1, new PositiveBooleanConstants.False());
        
        System.out.println(abta.toString());
    }

}
