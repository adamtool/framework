package uniolunisaar.adam.tests.pnwt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;
import uniolunisaar.adam.ds.petrinetwithtransits.DataFlowTree;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.ds.petrinetwithtransits.Transit;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.util.PNWTTools;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestDataFlowTrees {

    private static final String inputDir = System.getProperty("examplesfolder") + "/../modelchecking/ltl/";
    private static final String outputDir = System.getProperty("testoutputfolder") + "/dataflowtrees/";

    @BeforeClass
    public void createFolder() {
        Logger.getInstance().setVerbose(true);
//        Logger.getInstance().setShortMessageStream(null);
//        Logger.getInstance().setVerboseMessageStream(null);
//        Logger.getInstance().setWarningStream(null);
        (new File(outputDir)).mkdirs();
    }

    @Test
    public void testNoTree() throws IOException, InterruptedException, ParseException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final String path = inputDir + File.separator + "ATVA19_motivatingExample.apt";
        PetriNetWithTransits pnwt = PNWTTools.getPetriNetWithTransitsFromFile(path, false);
        // some firable, some with transits, some without, but never a chain is created
        List<Transition> firingSequence = new ArrayList<>();
        firingSequence.add(pnwt.getTransition("t7"));
        firingSequence.add(pnwt.getTransition("t3"));
        firingSequence.add(pnwt.getTransition("t2"));
        firingSequence.add(pnwt.getTransition("t0"));
        firingSequence.add(pnwt.getTransition("t9"));
        List<DataFlowTree> trees = PNWTTools.getDataFlowTrees(pnwt, firingSequence);
        Assert.assertTrue(trees.isEmpty());
        PNWTTools.saveDataFlowTreesToPDF(outputDir +pnwt.getName()+"_noTree", trees, PetriNetExtensionHandler.getProcessFamilyID(pnwt));
    }

    @Test
    public void testOneTree() throws IOException, InterruptedException, ParseException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final String path = inputDir + File.separator + "ATVA19_motivatingExample.apt";
        PetriNetWithTransits pnwt = PNWTTools.getPetriNetWithTransitsFromFile(path, false);
        // some firable, some with transits, some without, but never a chain is created
        List<Transition> firingSequence = new ArrayList<>();
        firingSequence.add(pnwt.getTransition("t7"));
        firingSequence.add(pnwt.getTransition("t3"));
        firingSequence.add(pnwt.getTransition("t2"));
        firingSequence.add(pnwt.getTransition("t0"));
        firingSequence.add(pnwt.getTransition("t9"));
        // now starting a chain        
        firingSequence.add(pnwt.getTransition("ingress"));
        // nothing moves
        firingSequence.add(pnwt.getTransition("t4"));
        firingSequence.add(pnwt.getTransition("t5"));
        firingSequence.add(pnwt.getTransition("t6"));
        firingSequence.add(pnwt.getTransition("t7"));
        // now next step        
        firingSequence.add(pnwt.getTransition("t1"));
        List<DataFlowTree> trees = PNWTTools.getDataFlowTrees(pnwt, firingSequence);
        Assert.assertEquals(trees.size(), 1);
        PNWTTools.saveDataFlowTreesToPDF(outputDir +pnwt.getName()+"_oneTree", trees, PetriNetExtensionHandler.getProcessFamilyID(pnwt));
    }

    @Test
    public void testSeveralTrees() throws IOException, InterruptedException, ParseException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final String path = inputDir + File.separator + "ATVA19_motivatingExample.apt";
        PetriNetWithTransits pnwt = PNWTTools.getPetriNetWithTransitsFromFile(path, false);
        // some firable, some with transits, some without, but never a chain is created
        List<Transition> firingSequence = new ArrayList<>();
        firingSequence.add(pnwt.getTransition("t7"));
        firingSequence.add(pnwt.getTransition("t3"));
        firingSequence.add(pnwt.getTransition("t2"));
        firingSequence.add(pnwt.getTransition("t0"));
        firingSequence.add(pnwt.getTransition("t9"));
        // now starting a chain        
        firingSequence.add(pnwt.getTransition("ingress"));
        // nothing moves
        firingSequence.add(pnwt.getTransition("t4"));
        firingSequence.add(pnwt.getTransition("t5"));
        firingSequence.add(pnwt.getTransition("t6"));
        firingSequence.add(pnwt.getTransition("t7"));
        // now next step        
        firingSequence.add(pnwt.getTransition("t1"));
        firingSequence.add(pnwt.getTransition("t2"));
        firingSequence.add(pnwt.getTransition("t6"));
        // start a new one
        // now starting a chain        
        firingSequence.add(pnwt.getTransition("ingress"));
        firingSequence.add(pnwt.getTransition("t0"));
        firingSequence.add(pnwt.getTransition("t0"));
        firingSequence.add(pnwt.getTransition("t0"));
        firingSequence.add(pnwt.getTransition("t4"));
        firingSequence.add(pnwt.getTransition("t4"));
        firingSequence.add(pnwt.getTransition("t3"));

        List<DataFlowTree> trees = PNWTTools.getDataFlowTrees(pnwt, firingSequence);
        Assert.assertEquals(trees.size(), 2);        
        PNWTTools.saveDataFlowTreesToPDF(outputDir +pnwt.getName()+"_twoTrees", trees, PetriNetExtensionHandler.getProcessFamilyID(pnwt));
    }

    @Test
    public void testRealTrees() throws IOException, InterruptedException, ParseException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final String path = inputDir + File.separator + "ATVA19_motivatingExample.apt";
        PetriNetWithTransits pnwt = PNWTTools.getPetriNetWithTransitsFromFile(path, false);  
        Place s = pnwt.getPlace("S");
        pnwt.createTransit(s, pnwt.getTransition("t0"), s);
        List<Transition> firingSequence = new ArrayList<>();
        // starting a chain        
        firingSequence.add(pnwt.getTransition("ingress"));
        firingSequence.add(pnwt.getTransition("t0"));
        firingSequence.add(pnwt.getTransition("t0"));
        firingSequence.add(pnwt.getTransition("t0"));
        
        List<DataFlowTree> trees = PNWTTools.getDataFlowTrees(pnwt, firingSequence);
        Assert.assertEquals(trees.size(), 1);        
        PNWTTools.saveDataFlowTreesToPDF(outputDir +pnwt.getName()+"_realTrees", trees, PetriNetExtensionHandler.getProcessFamilyID(pnwt));
    }

}
