package uniolunisaar.adam.tests.transformers;

import java.io.File;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.logic.transformers.petrinet.PN2Tikz;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestTikzExport {

    private static final String inputDir = System.getProperty("examplesfolder") + "/synthesis/forallsafety/";
    private static final String outputDir = System.getProperty("testoutputfolder") + "/tikzExport/";

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
        PetriNet pn = Tools.getPetriNet(inputDir + "mutex.apt");
        Tools.saveFile(outputDir+"mutex.tex", PN2Tikz.get(pn));
    }   
    
   
}
