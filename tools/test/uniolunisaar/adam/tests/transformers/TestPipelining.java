package uniolunisaar.adam.tests.transformers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.analysis.bounded.Bounded;
import uniolunisaar.adam.logic.transformers.petrinet.kbounded2safe.Pipelining;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.util.PNTools;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestPipelining {

    private static final String inputDir = System.getProperty("examplesfolder") + "/modelchecking/kbounded";
    private static final String outputDir = System.getProperty("testoutputfolder") + "/pipelining/";

    private static final List<String> skip = new ArrayList<>(Arrays.asList( // %%%% Examples skipped     
            ));

    @BeforeClass
    public void createFolder() {
        Logger.getInstance().setVerbose(false);
//        Logger.getInstance().setShortMessageStream(null);
//        Logger.getInstance().setVerboseMessageStream(null);
//        Logger.getInstance().setWarningStream(null);
        (new File(outputDir)).mkdirs();
    }

    @DataProvider(name = "files")
    public static Object[][] allExamples() {
        Collection<File> files = FileUtils.listFiles(
                new File(inputDir),
                new RegexFileFilter(".*\\.apt"),
                DirectoryFileFilter.DIRECTORY);
        Object[][] out = new Object[files.size() - skip.size()][1];
        int i = 0;
        for (File file : files) {
            if (!skip.contains(file.getName())) {
                out[i][0] = file;
                i++;
            }
        }
        return out;
    }

    @Test(dataProvider = "files")
    public void testFile(File file) throws Exception {
        Logger.getInstance().addMessage("Testing file: " + file.getAbsolutePath(), false);
        PetriNet net = Tools.getPetriNet(file.getAbsolutePath());
        PNTools.savePN2PDF(outputDir + net.getName(), net, true, true);
        PetriNet reduced = Pipelining.reduce(net, false);
        Tools.savePN(outputDir + net.getName() + "_safe", reduced);
        PNTools.savePN2PDF(outputDir + net.getName() + "_safe", reduced, true, true);
        Assert.assertTrue(Bounded.checkBounded(reduced).isSafe(), "Check 1-bounded");
        PetriNet reducedInhitior = Pipelining.reduce(net, true);
        Tools.savePN(outputDir + net.getName() + "_safeInhibi", reducedInhitior);
        PNTools.savePN2PDF(outputDir + net.getName() + "_safeInhibi", reducedInhitior, true, true);
        // sadly APT does not support empty labels nor has the possibility to jump over tau transitions
//        TransitionSystem rgKBounded = CoverabilityGraph.getReachabilityGraph(net).toReachabilityLTS();
//        TransitionSystem rgSafe = CoverabilityGraph.getReachabilityGraph(reduced).toReachabilityLTS();
//        Word equiv = LanguageEquivalence.checkLanguageEquivalence(rgKBounded, rgSafe); 
    }
}
