package uniolunisaar.adam.tests.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.parser.ParseException;
import uniol.apt.io.renderer.RenderException;
import uniol.apt.util.Pair;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.util.PNTools;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestTools {

    @BeforeMethod
    public void silence() {
        Logger.getInstance().setVerbose(false);
        Logger.getInstance().setShortMessageStream(null);
        Logger.getInstance().setVerboseMessageStream(null);
        Logger.getInstance().setWarningStream(null);
//        System.out.println("Thread in before method" + Thread.currentThread());
    }

    @Test
    public void testParseExceptionParsing() throws IOException {
        String content = ".name \"unfoldingAdditionalSystem\"\n"
                + ".description \"Testing the correctness of additional system places in the unfolding\"\n"
                + ".type LPN\n"
                + ".options\n"
                + "condition=\"LTL\",\n"
                + "winningCondition=\"E_SAFETY\"\n"
                + "\n"
                + ".places\n"
                + "E1[yCoord=528.28, bad=\"true\", xCoord=568.71, env=\"true\"]\n"
                + "E2[yCoord=671.4, xCoord=800.59, env=\"true\"]\n"
                + "E3[yCoord=484.89, xCoord=1117.85, env=\"true\"]\n"
                + "E4[yCoord=603.28, xCoord=1064.9, env=\"true\"]\n"
                + "S1[yCoord=555.99, xCoord=692.99, token=1]\n"
                + "S2[yCoord=331.64, xCoord=930.02, token=1]\n"
                + "S3[yCoord=822.92, xCoord=996.08, token=1]\n"
                + "bad1[yCoord=531.27, bad=\"true\", xCoord=1031.29, token=1]\n"
                + "\n"
                + ".transitions\n"
                + "b1[label=\"b1\", yCoord=403.94, xCoord=1072.63, tfl=\"E3 -> {E4},S2 -> {bad1}\"]\n"
                + "b2[label=\"b2\", yCoord=704.62, xCoord=973.94, tfl=\"S3 -> {bad1},E2 -> {E4}\"]\n"
                + "g1[label=\"g1\", yCoord=519.79, xCoord=907.68, tfl=\"E2 -> {E4},S2 -> {E4}\"]\n"
                + "g2[label=\"g2\", yCoord=685.0, xCoord=1142.47, tfl=\"S3 -> {E4},E3 -> {E4}\"]\n"
                + "t1[label=\"t1\", yCoord=680.21, xCoord=593.14, tfl=\"E1 -> {E2}\"]\n"
                + "t2[label=\"t2\", yCoord=450.17, xCoord=816.42, tfl=\"E1 -> {E3}\"]-\n"
                + "t3[label=\"t3\", yCoord=329.84, xCoord=749.33, tfl=\"S1 -> {S2}\"]\n"
                + "t4[label=\"t4\", yCoord=802.64, xCoord=802.31, tfl=\"S1 -> {S3}\"]\n"
                + "\n"
                + ".flows\n"
                + "b1: {1*E3, 1*S2} -> {1*bad1, 1*E4}\n"
                + "b2: {1*E2, 1*S3} -> {1*bad1, 1*E4}\n"
                + "g1: {1*S2, 1*E2} -> {1*E4}\n"
                + "g2: {1*S3, 1*E3} -> {1*E4}\n"
                + "t1: {1*E1} -> {1*E2}\n"
                + "t2: {1*E1} -> {1*E3}\n"
                + "t3: {1*S1} -> {1*S2}\n"
                + "t4: {1*S1} -> {1*S3}\n"
                + "\n"
                + ".initial_marking {1*E1, 1*S1}";
        try {
            PetriNet pn = Tools.getPetriNetFromString(content);
        } catch (ParseException ex) {
            Pair<Integer, Integer> loc = Tools.getErrorLocation(ex);
            int line = loc.getFirst();
            int col = loc.getSecond();
            Assert.assertEquals(line, 24, "Line Error");
            Assert.assertEquals(col, 62, "Column Error");
        }
    }

    @Test(timeOut = (60 * 1000) / 2) // 30 sec
    public void testLoggerWithTimeout() {
//        System.out.println("Thread in timeout class" + Thread.currentThread());
    }

    @Test
    public void testThreads() {
        Thread first = new Thread();
        ThreadGroup threadGroup = new ThreadGroup("peter");
        Thread a = new Thread(threadGroup, () -> {
//            System.out.println("A " + Thread.currentThread());
//            System.out.println(Thread.currentThread().getThreadGroup().toString());
            Thread b = new Thread(() -> {
//                System.out.println("B " + Thread.currentThread());
//                System.out.println(Thread.currentThread().getThreadGroup().toString());
                Thread c = new Thread(() -> {
//                    System.out.println("C " + Thread.currentThread());
//                    System.out.println(Thread.currentThread().getThreadGroup().toString());
                });
                c.start();
            });
            b.start();
        });
        a.start();
    }

    @Test
    public void testLogger() throws InterruptedException {
//        System.out.println("count " +  Logger.instances.size());
//        System.out.println("%%%%%%%%%%%%% LOGGER ");
        ByteArrayOutputStream ob1 = new ByteArrayOutputStream();
        PrintStream streamA = new PrintStream(ob1);
        ThreadGroup first = new ThreadGroup("first");
        Thread calcA = new Thread(first, () -> {
            Logger.getInstance().setShortMessageStream(streamA);
            Logger.getInstance().addMessage("A go for it");
        });
        calcA.start();

        ByteArrayOutputStream ob2 = new ByteArrayOutputStream();
        PrintStream streamB = new PrintStream(ob2);
        ThreadGroup second = new ThreadGroup("second");
        Thread calcB = new Thread(second, () -> {
            Logger.getInstance().setShortMessageStream(streamB);
            Logger.getInstance().addMessage("B go for it");
        });
        calcB.start();

//        System.out.println("count " +  Logger.instances.size());
        calcA.join();
        calcB.join();

//        System.out.println("This is A");
        streamA.flush();
        String data = new String(ob1.toByteArray(), StandardCharsets.UTF_8);
//        System.out.println(data);
//        
//        System.out.println("This is B");
        streamB.flush();
        data = new String(ob2.toByteArray(), StandardCharsets.UTF_8);
//        System.out.println(data);
//        System.out.println("%%%%%%%%%%% finished");
//        
//        System.out.println("count " +  Logger.instances.size());
//        Logger.getInstance();
//        System.out.println("count " +  Logger.instances.size());
    }

    public void testPNMLRenderer() throws RenderException {
        PetriNet net = new PetriNet();
        net.createPlace("peter_<tfl>_asdf");
        net.createPlace("nod1");
        Place p1 = net.createPlace("nod2_<>");
        Place p2 = net.createPlace("nod3");
        Transition t = net.createTransition("asdf");
        net.createFlow(p1,t);
        net.createFlow(t,p2);
        String pnml = PNTools.pn2pnml(net);
    }
}
