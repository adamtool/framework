package uniolunisaar.adam.tests.transformers;

import java.io.File;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import uniolunisaar.adam.ds.automata.BuchiAutomaton;
import uniolunisaar.adam.ds.automata.BuchiState;
import uniolunisaar.adam.ds.automata.StringLabel;
import uniolunisaar.adam.logic.transformers.automata.NDet2DetAutomatonTransformer;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestNDet2Det {

    private static final String outputDir = System.getProperty("testoutputfolder") + "/ndet2det/";

    @BeforeClass
    public void createFolder() {
        (new File(outputDir)).mkdirs();
    }

    @BeforeClass
    public void silence() {
//        Logger.getInstance().setVerbose(true);
//        Logger.getInstance().setShortMessageStream(null);
//        Logger.getInstance().setVerboseMessageStream(null);
//        Logger.getInstance().setWarningStream(null);
    }

    @Test
    public void firstTest() throws Exception {
        BuchiAutomaton a = new BuchiAutomaton("test");
        BuchiState eins = a.createAndAddState("1", true);
        BuchiState zwei = a.createAndAddState("2", true);
        BuchiState drei = a.createAndAddState("3", true);
        BuchiState vier = a.createAndAddState("4", true);
        BuchiState fuenf = a.createAndAddState("5", false);
        BuchiState sechs = a.createAndAddState("6", false);
        a.setBuchi(true, sechs, fuenf);
        a.addInitialState(eins);
        a.createAndAddEdge("1", new StringLabel("a"), "2", true);
        a.createAndAddEdge("1", new StringLabel("b"), "3", false);
        a.createAndAddEdge("1", new StringLabel("a"), "4", true);
        a.createAndAddEdge("2", new StringLabel("a"), "5", false);
        a.createAndAddEdge("2", new StringLabel("b"), "3", true);
        a.createAndAddEdge("3", new StringLabel("b"), "3", true);
        a.createAndAddEdge("4", new StringLabel("a"), "6", true);

        Tools.save2DotAndPDF(outputDir + a.getName(), a);

        BuchiAutomaton detA = NDet2DetAutomatonTransformer.transform(a);
        Tools.save2DotAndPDF(outputDir + detA.getName(), detA);
    }
    
    
      @Test
    public void firstTest2() throws Exception {
        BuchiAutomaton a = new BuchiAutomaton("test2");
        BuchiState eins = a.createAndAddState("1", true);
        BuchiState zwei = a.createAndAddState("2", true);
        BuchiState drei = a.createAndAddState("3", true);
        BuchiState vier = a.createAndAddState("4", true);
        BuchiState fuenf = a.createAndAddState("5", false);
        BuchiState sechs = a.createAndAddState("6", false);
        a.setBuchi(true, sechs, fuenf);
        a.addInitialState(eins);
        a.addInitialState(drei);
        a.addInitialState(vier);
        a.createAndAddEdge("1", new StringLabel("a"), "2", true);
        a.createAndAddEdge("1", new StringLabel("b"), "3", false);
        a.createAndAddEdge("1", new StringLabel("a"), "4", true);
        a.createAndAddEdge("2", new StringLabel("a"), "5", false);
        a.createAndAddEdge("2", new StringLabel("b"), "3", true);
        a.createAndAddEdge("3", new StringLabel("b"), "3", true);
        a.createAndAddEdge("4", new StringLabel("a"), "6", true);
        a.createAndAddEdge("4", new StringLabel("a"), "1", true);
        a.createAndAddEdge("6", new StringLabel("a"), "6", true);
        a.createAndAddEdge("6", new StringLabel("b"), "6", true);
        a.createAndAddEdge("5", new StringLabel("a"), "5", true);

        Tools.save2DotAndPDF(outputDir + a.getName(), a);

        BuchiAutomaton detA = NDet2DetAutomatonTransformer.transform(a);
        Tools.save2DotAndPDF(outputDir + detA.getName(), detA);
    }
}
