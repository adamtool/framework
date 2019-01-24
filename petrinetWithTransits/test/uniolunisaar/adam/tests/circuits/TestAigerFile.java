package uniolunisaar.adam.tests.circuits;

import org.testng.Assert;
import org.testng.annotations.Test;
import uniolunisaar.adam.ds.circuits.AigerFile;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestAigerFile {

    @Test
    public void testDuplicateInputForGate() {
        AigerFile f = new AigerFile();
        f.addInputs("i1", "i2", "i3");
        f.addGate("out", "i1", "i2", "i1", "i3", "i2");
        Assert.assertEquals(f.toString(), "aag 6 3 0 0 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8 2 4\n"
                + "10 8 6\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n");
        f.addGate("peter", "i3");
        Assert.assertEquals(f.toString(), "aag 6 3 0 0 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8 2 4\n"
                + "10 8 6\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n");
        f.addGate("klaus", "i3", "i3");
        Assert.assertEquals(f.toString(), "aag 6 3 0 0 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8 2 4\n"
                + "10 8 6\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n");
        // this still a problem
        f.addGate("asdf", "peter", "i3");
        Assert.assertEquals(f.toString(), "aag 6 3 0 0 2\n"
                + "2\n"
                + "4\n"
                + "6\n"
                + "8 2 4\n"
                + "10 8 6\n"
                + "i0 i1\n"
                + "i1 i2\n"
                + "i2 i3\n");
    }
}
