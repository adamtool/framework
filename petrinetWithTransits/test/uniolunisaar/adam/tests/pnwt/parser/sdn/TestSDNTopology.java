package uniolunisaar.adam.tests.pnwt.parser.sdn;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.ConcurrentUpdate;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.SequentialUpdate;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.SwitchUpdate;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.Update;
import uniolunisaar.adam.logic.parser.sdn.SDNTopologyParser;
import uniolunisaar.adam.logic.parser.sdn.SDNUpdateParser;
import uniolunisaar.adam.util.PNWTTools;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestSDNTopology {

    private static final String outputDir = System.getProperty("testoutputfolder") + "/";

    @BeforeClass
    public void createFolder() {
        (new File(outputDir)).mkdirs();
    }

    private static final String topologyA = ".name \"peter\"\n"
            + ".description \"klaus ist raus\"\n"
            + ".options\n"
            + "opt1 = \"asdf\",\n"
            + "opt2 = \"more\"\n"
            + "\n"
            + ".switches\n"
            + "p1 [asdf=\"A\", second=\"B\"]\n"
            + "p2\n"
            + "4\n"
            + "\n"
            + ".connections\n"
            + "p1 4\n"
            + "4 p2[A=\"asdf\", B=\"5\"]\n"
            + "\n"
            + ".switches\n"
            + "p3\n"
            + "p3B\n"
            + "\n"
            + ".connections\n"
            + "p3 p3B\n"
            + "\n"
            + ".ingress={4, p1}\n"
            + ".egress={p2}\n"
            + "\n"
            + ".forwarding"
            + "p1.fwd(4)\n"
            + "4.fwd(p2)";

    private static final String updateASw = "upd(p2.fwd(4))";
    private static final String updateAPar = "(upd(p2.fwd(4)) || upd(4.fwd(p1)))";
    private static final String updateASeq = "(upd(p2.fwd(4)) >> upd(4.fwd(p1)))";
    private static final String updateANested = "(upd(p3.fwd(p3B)) || (upd(p2.fwd(4)) >> upd(4.fwd(p1))) )";

    @Test
    public void topology() throws ParseException, FileNotFoundException {
        PetriNetWithTransits pn = SDNTopologyParser.parse(topologyA);
        Assert.assertEquals(pn.getName(), "peter");
        Assert.assertEquals(pn.getExtension("description"), "klaus ist raus");
        Assert.assertEquals(pn.getExtension("opt1"), "asdf");
        Assert.assertEquals(pn.getExtension("opt2"), "more");
        Place p1 = pn.getPlace("p1");
        Assert.assertEquals(p1.getExtension("asdf"), "A");
        Assert.assertEquals(p1.getExtension("second"), "B");
        Place p2 = pn.getPlace("p2");
        Place four = pn.getPlace("4");
        Assert.assertEquals(p1.getPostset().size(), 3);
        Assert.assertEquals(four.getPostset().size(), 5);
        Assert.assertEquals(p2.getPostset().size(), 2);
        Set<Transition> pre = p2.getPreset();
        Assert.assertEquals(pre.size(), 2);
        Transition t = pre.iterator().next();
        Assert.assertEquals(t.getExtension("A"), "asdf");
        Assert.assertEquals(t.getExtension("B"), "5");
        Place p3 = pn.getPlace("p3");
        PNWTTools.savePnwt2PDF(outputDir + pn.getName(), pn, true);
    }

    @Test
    public void update() throws ParseException {
        PetriNetWithTransits pn = SDNTopologyParser.parse(topologyA);

        Update updateSw = SDNUpdateParser.parse(pn, updateASw);
        if (!(updateSw instanceof SwitchUpdate)) {
            Assert.fail("Should be a switch update");
        }
        Update updatePar = SDNUpdateParser.parse(pn, updateAPar);
        if (!(updatePar instanceof ConcurrentUpdate)) {
            Assert.fail("Should be a concurrent update");
        }
        Update updateSeq = SDNUpdateParser.parse(pn, updateASeq);
        if (!(updateSeq instanceof SequentialUpdate)) {
            Assert.fail("Should be a concurrent update");
        }
        Update updateNested = SDNUpdateParser.parse(pn, updateANested);  
        if (!(updateNested instanceof ConcurrentUpdate)) {
            Assert.fail("Should be a concurrent update");
        }
    }
}
