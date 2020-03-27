package uniolunisaar.adam.tests.circuits;

import org.testng.Assert;
import org.testng.annotations.Test;
import uniolunisaar.adam.ds.circuits.AigerFile;
import uniolunisaar.adam.ds.circuits.AigerFileOptimizedGates;
import uniolunisaar.adam.ds.circuits.AigerFileOptimizedGatesAndIndizes;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestAigerFileOptimizedGatesAndIndices {

    @Test
    public void testDuplicateInputForGate() {
        AigerFile f = new AigerFileOptimizedGatesAndIndizes(true, false);
        f.addInputs("i1", "i2", "i3");
        f.addGate("out", "i1", "i2", "i1", "i3", "i2");
        Assert.assertEquals(f.toString(), "aag 5 3 0 0 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8 2 4\n"
                + "10 8 6\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n");
        f.addGate("peter", "i3");
        Assert.assertEquals(f.toString(), "aag 5 3 0 0 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8 2 4\n"
                + "10 8 6\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n");
        f.addGate("klaus", "i3", "i3");
        Assert.assertEquals(f.toString(), "aag 5 3 0 0 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8 2 4\n"
                + "10 8 6\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n");
        f.addGate("asdf", "peter", "i3");
        Assert.assertEquals(f.toString(), "aag 5 3 0 0 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8 2 4\n"
                + "10 8 6\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n");
    }

    @Test
    public void testFirstDuplicateInputForGate() {
        AigerFile f = new AigerFileOptimizedGatesAndIndizes(true, false);
        f.addInputs("i1", "i2", "i3");
        f.addOutputs("out1");
        f.addGate("out1", "i1", "i1");
        Assert.assertEquals(f.toString(), "aag 3 3 0 1 0\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "2\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n"
                + "o0 out1");

        f.addGate("out1", "i1", "i1", "i1", "i3", "i2");
        Assert.assertEquals(f.toString(), "aag 5 3 0 1 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "2\n"
                + "8 10 6\n"
                + "10 2 4\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n"
                + "o0 out1");
        f.addGate("peterr", "i3");
        Assert.assertEquals(f.toString(), "aag 5 3 0 1 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "2\n"
                + "8 10 6\n"
                + "10 2 4\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n"
                + "o0 out1");
        f.addGate("klaus", "i3", "i3");
        Assert.assertEquals(f.toString(), "aag 5 3 0 1 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "2\n"
                + "8 10 6\n"
                + "10 2 4\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n"
                + "o0 out1");
        f.addGate("asdf", "peterr", "i3");
        Assert.assertEquals(f.toString(), "aag 5 3 0 1 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "2\n"
                + "8 10 6\n"
                + "10 2 4\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n"
                + "o0 out1");
        f.addGate("new", "i1", "!i1");
// without also optimizing the negation results to zero
//        Assert.assertEquals(f.toString(), "aag 6 3 0 1 3\n"
//                + "2\n"
//                + "4\n"
//                + "6\n"
//                + "2\n"
//                + "8 2 3\n"
//                + "10 12 6\n"
//                + "12 2 4\n"
//                + "i0 i1\n"
//                + "i1 i2\n"
//                + "i2 i3\n"
//                + "o0 out1"); // possibly differently ordered? not deterministic?
        Assert.assertEquals(f.toString(), "aag 5 3 0 1 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "2\n"
                + "8 10 6\n"
                + "10 2 4\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n"
                + "o0 out1"); // possibly the order is not deterministic?
        f.addGate("next", "i2", "i3");
        // without also optimizing the negation results to zero
//        Assert.assertEquals(f.toString(), "aag 7 3 0 1 4\n"
//                + "2\n"
//                + "4\n"
//                + "6\n"
//                + "2\n"
//                + "8 4 6\n"
//                + "10 2 3\n"
//                + "12 14 6\n"
//                + "14 2 4\n"
//                + "i0 i1\n"
//                + "i1 i2\n"
//                + "i2 i3\n"
//                + "o0 out1"); // possibly the order is not deterministic?
        Assert.assertEquals(f.toString(), "aag 6 3 0 1 3\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "2\n"
                + "8 4 6\n"
                + "10 12 6\n"
                + "12 2 4\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n"
                + "o0 out1"); // possibly the order is not deterministic?
        // here is a problem by just doing it iterativly since new is already optimized
        AigerFile f1 = new AigerFileOptimizedGatesAndIndizes(true, false);
        f1.addInputs("i1", "i2", "i3");
        f1.addOutputs("out1");
        f1.addGate("out1", "i1", "i1");
        f1.addGate("out1", "i1", "i1", "i1", "i3", "i2");
        f1.addGate("peterr", "i3");
        f1.addGate("klaus", "i3", "i3");
        f1.addGate("asdf", "peterr", "i3");
        f1.addGate("new", "i1", "!i1");
        f1.addGate("next", "i2", "i3");
        f1.addGate("peter", "new", "i3");
        Assert.assertEquals(f1.toString(), "aag 6 3 0 1 3\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "2\n"
                + "8 4 6\n"
                + "10 12 6\n"
                + "12 2 4\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n"
                + "o0 out1"); // possibly the order is not deterministic?
        // here is a problem by just doing it iterativly since new is already optimized
        AigerFile f2 = new AigerFileOptimizedGatesAndIndizes(true, false);
        f2.addInputs("i1", "i2", "i3");
        f2.addOutputs("out1");
        f2.addGate("out1", "i1", "i1");
        f2.addGate("out1", "i1", "i1", "i1", "i3", "i2");
        f2.addGate("peterr", "i3");
        f2.addGate("klaus", "i3", "i3");
        f2.addGate("asdf", "peterr", "i3");
        f2.addGate("new", "i1", "!i1");
        f2.addGate("next", "i2", "i3");
        f2.addGate("peter", "new", "i3");
        f2.addOutput("outPeter");
        f2.copyValues("outPeter", "peter");
        Assert.assertEquals(f2.toString(), "aag 6 3 0 2 3\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "2\n"
                + "0\n"
                + "8 4 6\n"
                + "10 12 6\n"
                + "12 2 4\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n"
                + "o0 out1\n"
                + "o1 outPeter"); // possibly the order is not deterministic?
    }

    @Test
    public void testNegation() {
        AigerFile f = new AigerFileOptimizedGatesAndIndizes(true, false);
        f.addInputs("a1", "a2", "a3");
        f.addOutputs("out1");
        f.addGate("out1", "a1", "a2");
        Assert.assertEquals(f.toString(), "aag 4 3 0 1 1\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8\n"
                + "8 2 4\n"
                + "i0 a1\n"
                + "i1 a2\n"
                + "i2 a3\n"
                + "o0 out1");
        f.addGate("out2", "a1", "!a1");
        Assert.assertEquals(f.toString(), "aag 4 3 0 1 1\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8\n"
                + "8 2 4\n"
                + "i0 a1\n"
                + "i1 a2\n"
                + "i2 a3\n"
                + "o0 out1");
        AigerFile f1 = new AigerFileOptimizedGatesAndIndizes(true, false);
        f1.addInputs("a1", "a2", "a3");
        f1.addOutputs("out1");
        f1.addGate("out1", "a1", "a2");
        f1.addGate("out2", "a1", "!a1");
        f1.addGate("out3", "a1", "out2");
        // without also optimizing the "one input is zero"
//        Assert.assertEquals(f1.toString(), "aag 5 3 0 1 2\n"
//                + "2\n"
//                + "4\n"
//                + "6\n"
//                + "10\n"
//                + "8 2 0\n"
//                + "10 2 4\n"
//                + "i0 a1\n"
//                + "i1 a2\n"
//                + "i2 a3\n"
//                + "o0 out1");        
        Assert.assertEquals(f1.toString(), "aag 4 3 0 1 1\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8\n"
                + "8 2 4\n"
                + "i0 a1\n"
                + "i1 a2\n"
                + "i2 a3\n"
                + "o0 out1");
        AigerFile f2 = new AigerFileOptimizedGatesAndIndizes(true, false);
        f2.addInputs("a1", "a2", "a3");
        f2.addOutputs("out1");
        f2.addGate("out1", "a1", "a2");
        f2.addGate("out2", "a1", "!a1");
        f2.addGate("out3", "a1", "out2");
        f2.addOutputs("myout");
        f2.copyValues("myout", "out2");
        Assert.assertEquals(f2.toString(), "aag 4 3 0 2 1\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8\n"
                + "0\n"
                + "8 2 4\n"
                + "i0 a1\n"
                + "i1 a2\n"
                + "i2 a3\n"
                + "o0 out1\n"
                + "o1 myout");
    }

    @Test
    public void testRecursive() {
        AigerFile f = new AigerFileOptimizedGatesAndIndizes(true, false);
        f.addInputs("a1", "a2", "a3");
        f.addOutputs("out1");
        f.addGate("out1", "a1", "a2");
        Assert.assertEquals(f.toString(), "aag 4 3 0 1 1\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8\n"
                + "8 2 4\n"
                + "i0 a1\n"
                + "i1 a2\n"
                + "i2 a3\n"
                + "o0 out1");
        f.addGate("asdf1", "a1", AigerFileOptimizedGates.FALSE);
        f.addGate("asdf2", AigerFileOptimizedGates.FALSE, "a2");
        f.addGate("res", "asdf1", "asdf2");
        Assert.assertEquals(f.toString(), "aag 4 3 0 1 1\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8\n"
                + "8 2 4\n"
                + "i0 a1\n"
                + "i1 a2\n"
                + "i2 a3\n"
                + "o0 out1");
        AigerFile f1 = new AigerFileOptimizedGatesAndIndizes(true, false);
        f1.addInputs("a1", "a2", "a3");
        f1.addOutputs("out1");
        f1.addGate("out1", "a1", "a2");
        f1.addGate("asdf1", "a1", AigerFileOptimizedGates.FALSE);
        f1.addGate("asdf2", AigerFileOptimizedGates.FALSE, "a2");
        f1.addGate("res", "asdf1", "asdf2");
        f1.addGate("asdf3", "asdf2", "asdf1");
        Assert.assertEquals(f1.toString(), "aag 4 3 0 1 1\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8\n"
                + "8 2 4\n"
                + "i0 a1\n"
                + "i1 a2\n"
                + "i2 a3\n"
                + "o0 out1");
        f1.addGate("out2", "a2", "a1");
        f1.addGate("out3", "out1", "out2");
        Assert.assertEquals(f1.toString(), "aag 4 3 0 1 1\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8\n"
                + "8 2 4\n"
                + "i0 a1\n"
                + "i1 a2\n"
                + "i2 a3\n"
                + "o0 out1");
    }
}
