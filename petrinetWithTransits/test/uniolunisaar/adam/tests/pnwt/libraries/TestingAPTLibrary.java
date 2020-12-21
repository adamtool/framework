package uniolunisaar.adam.tests.pnwt.libraries;

import java.io.File;
import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.Test;
import uniol.apt.adt.exception.NoSuchNodeException;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.adt.ts.State;
import uniol.apt.adt.ts.TransitionSystem;
import uniol.apt.analysis.bounded.Bounded;
import uniol.apt.analysis.bounded.BoundedResult;
import uniol.apt.analysis.coverability.CoverabilityGraph;
import uniol.apt.io.parser.ParseException;
import uniol.apt.io.parser.impl.AptLTSParser;
import uniol.apt.io.parser.impl.AptPNParser;
import uniol.apt.io.renderer.RenderException;
import uniol.apt.io.renderer.impl.AptLTSRenderer;
import uniol.apt.io.renderer.impl.DotLTSRenderer;
import uniol.apt.io.renderer.impl.PnmlPNRenderer;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.util.PNWTTools;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestingAPTLibrary {

    private static final String inputDir = System.getProperty("examplesfolder");

    @Test
    public void reachVsCover() throws Exception {
        File file = null;
//        File file = new File(inputDir + "/forallsafety/reversible/wf_2_3_pg_reversible.apt"); // not bounded
//        File file = new File(inputDir + "/forallsafety/scalable/documentWorkFlow/standard/15_DW.apt");  // takes too long
        PetriNet net = new AptPNParser().parseFile(file);
        long start = System.currentTimeMillis();
        CoverabilityGraph cg = CoverabilityGraph.get(net);
        int nb_nodes_cg = cg.calculateNodes();
        long end = System.currentTimeMillis();
        System.out.println("CG: nodes: " + nb_nodes_cg + " needed: " + (end - start) / 100 + "s ");
        
        BoundedResult checkBounded = Bounded.checkBounded(net);
        System.out.println("it is bounded "+ checkBounded.isBounded());

        start = System.currentTimeMillis();
        CoverabilityGraph reach = CoverabilityGraph.getReachabilityGraph(net);
        int nb_nodes_reach = reach.calculateNodes();
        end = System.currentTimeMillis();
        System.out.println("Reach: nodes: " + nb_nodes_reach + " needed: " + (end - start) / 100 + "s ");
    }

    @Test
    public void testTransitionSystems() throws RenderException, ParseException, IOException {
        TransitionSystem ts = new TransitionSystem("asdf");
        State[] states = ts.createStates(5);
        State initial = ts.createState("init");
        ts.setInitialState(initial);
        ts.createArc(initial, states[0], "");
        ts.createArc(states[0], states[4], "label");
        // save to APT
        String apt = new AptLTSRenderer().render(ts);
        new DotLTSRenderer().renderFile(ts, ts.getName());

        String aptInput = ".name \"testLTS.aut\"\n"
                + ".type LTS\n"
                + ".states\n"
                + "s0[initial]\n"
                + "s1\n"
                + "s2\n"
                + "s3\n"
                + "s4\n"
                + "s5\n"
                + ".labels\n"
                + "a[key=\"value\"]\n"
                + "c\n"
                + "e\n"
                + ".arcs\n"
                + "s0 a s1\n"
                + "s1 a s2\n"
                + "s2 c s3\n"
                + "s3 c s4\n"
                + "s4 e s5\n"
                + "s5 e s0";

        TransitionSystem ts2 = new AptLTSParser().parseString(aptInput);
        new DotLTSRenderer().renderFile(ts, ts2.getName());
        // for files: new AptLTSParser().parseFile
    }

    @Test
    public void testReachability() {
//        PetriGame game = new PetriGame("asdf");
//        for (Place p : game.getPlaces()) {
//            if(p.getInitialToken().getValue()>0) {
//                
//            }
//        }
//        Marking m = game.getInitialMarking();
//        Token t = m.getToken("asdf");
//        Place p = game.createEnvPlace();
//        p.setInitialToken(1);
//
//        Place pa = game.createPlace("peter");
//        game.setEnvironment(pa);
//
//        Transition t = game.createTransition();
//        t.setLabel("asdf");
//        game.createFlow(p, t);
//        game.createFlow(t, pa);
//        PNWTTools.savePnwt2PDF("./", game, true);

//        PetriNet net = SelfOrganizingRobots.generate(3, 2, true, true);
//
//        for (Place place : net.getPlaces()) {
//            if (AdamExtensions.isEnviroment(place)) {
//
//            } else {
//
//            }
//            if (AdamExtensions.isBad(place)) {
//
//            }
//        }
//
//        for (Transition transition : net.getTransitions()) {
//
//        }
//
//        for (Flow edge : net.getEdges()) {
//            Place p = edge.getPlace();
//            Transition t = edge.getTransition();
//            p.getId();
//            t.getId(); // that's how u can get the unique id 
//        }
    }

    @Test
    public void copyConstructor() throws ParseException, IOException {
        final String path = System.getProperty("examplesfolder") + "/forallsafety/burglar/burglar.apt";
        PetriNet pn = Tools.getPetriNet(path);

        PetriNet net2 = new PetriNet(pn);

        PetriNetWithTransits game = new PetriNetWithTransits(pn);

        PetriNetWithTransits game2 = new PetriNetWithTransits(net2);

        game.removePlace("env");
        PetriNetWithTransits game3 = new PetriNetWithTransits(game);
    }

    @Test
    public void burglar() throws IOException, ParseException {
        final String path = System.getProperty("examplesfolder") + "/forallsafety/burglar/burglar.apt";
        PetriNet pn = Tools.getPetriNet(path);
        BoundedResult res = Bounded.checkBounded(pn);
        Assert.assertTrue(res.isBounded());
        Assert.assertTrue(res.isSafe());
        Assert.assertTrue(res.isKBounded(1));
        Assert.assertEquals(pn.getPlaces().size(), 17);
        Assert.assertEquals(pn.getTransitions().size(), 30);

        // Concurrency preserving if not in APT add this code for the analysis
        boolean test = true;
        for (Transition t : pn.getTransitions()) {
            if (t.getPreset().size() != t.getPostset().size()) {
                test = false;
            }
        }
        Assert.assertTrue(test);
    }

    @Test(enabled = false)
    public void netGenerators() {
//        TNetGenerator gen = new TNetGenerator(50);
//        PetriNet net = gen.iterator().next();
        // TristatePhilNetGenerator gen = new TristatePhilNetGenerator();
        // BistatePhilNetGenerator gen = new BistatePhilNetGenerator();
//        QuadstatePhilNetGenerator gen = new QuadstatePhilNetGenerator();
//        SimpleBitNetGenerator gen = new SimpleBitNetGenerator();
//        PetriNet net = gen.generateNet(4);
//        Place env1 = net.getPlaces().iterator().next();
//        env1.putExtension("env", "true");
//        Place env2 = env1.getPostset().iterator().next().getPostset().iterator().next();
//        env2.putExtension("env", "true");
        System.out.println("Generate-process ready.");
//        try {
//            PetriGame game = new PetriGame(net);
//            game.initialize();
//            Assert.assertTrue(game.existsWinningStrategy());
//        } catch (NetNotSafeException ex) {
//            ex.printStackTrace();
//        } catch (NetNotConcurrencyPreservingException ex) {
//            ex.printStackTrace();
//        }
    }

    @Test(expectedExceptions = {NoSuchNodeException.class}, expectedExceptionsMessageRegExp = "Node 't1' does not exist in graph 'burglar.net'")
    public void testTokenflow() throws IOException, ParseException {
        final String path = System.getProperty("examplesfolder") + "/forallsafety/burglar/burglar.apt";
        PetriNetWithTransits game = PNWTTools.getPetriNetWithTransitsFromParsedPetriNet(Tools.getPetriNet(path), true);
        game.removeNode(game.getTransition("t1"));
        game.getTransition("t1");
    }

    @Test
    public void pnmlRenderer() throws RenderException, ParseException, IOException {
        PnmlPNRenderer renderer = new PnmlPNRenderer();
        String out = renderer.render(Tools.getPetriNet(System.getProperty("examplesfolder") + "/forallsafety/burglar/burglar.apt"));
//        System.out.println(out);
    }

    @Test
    public void pnmlSaving() throws RenderException, ParseException, IOException, InterruptedException {
        PnmlPNRenderer renderer = new PnmlPNRenderer();
        PetriNetWithTransits game = new PetriNetWithTransits("testing");
        Place init = game.createPlace("inittfl");
        init.setInitialToken(1);
//        Transition t = game.createTransition("tA");
//        game.createFlow(init, t);
//        game.createFlow(t, init);
        Transition tstolen = game.createTransition("tB");
        game.createFlow(init, tstolen);
        Place out = game.createPlace("out");
        game.createFlow(tstolen, out);
//
//        Place init2 = game.createPlace("inittflA");
//        init2.setInitialToken(1);
//        
//        
        Place init3 = game.createPlace("inittflB");
        init3.setInitialToken(1);
//        
        Transition t2 = game.createTransition("tC");
        game.createFlow(init3, t2);
        game.createFlow(t2, init3);

        PNWTTools.savePnwt2PDF(game.getName(), game, true);
        renderer.renderFile(game, game.getName() + ".pnml");

    }
}
