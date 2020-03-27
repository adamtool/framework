package uniolunisaar.adam.tests.pnwt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.ds.petrinetwithtransits.TokenTree;
import uniolunisaar.adam.util.PNWTTools;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestTokenTrees {

    private static final String inputDir = System.getProperty("examplesfolder") + "/forallreachability/";

    private void testExample(String name, List<List<String>> expected) throws IOException, InterruptedException, ParseException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final String path = inputDir + File.separator;
        PetriNet net = Tools.getPetriNet(path + name + ".apt");
        PetriNetWithTransits game = PNWTTools.getPetriNetWithTransitsFromParsedPetriNet(net, true);
//        TokenTreeCreator.createAndAnnotateTokenTree(game);
//        List<TokenTree> trees = TokenTreeCreator.getTokenTrees(game);
//        testTrees(trees, expected);
//        System.out.println(trees.toString());
    }

    private void testTrees(List<TokenTree> output, List<List<String>> expected) {
        Assert.assertTrue(output.size() == expected.size(), "Number of trees match.\n" + output.toString());
        for (TokenTree tree : output) {
            boolean found = false;
            for (List<String> exp : expected) {
                boolean fit = testTree(tree, exp);
                if (fit) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found, "Couldn't find tree:\n" + tree.toString() + "\nin\n" + expected.toString());
        }
    }

    private boolean testTree(TokenTree output, List<String> expected) {
        if (output.size() != expected.size()) {
            return false;
        }
        for (Place place : output) {
            boolean found = false;
            for (String pid : expected) {
                if (place.getId().equals(pid)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    @Test(enabled = true)
    public void testBurglar() throws Exception {
        final String burglar = "burglar/";
        List<List<String>> expected = new ArrayList<>();
        expected.add(new ArrayList<>(Arrays.asList("pa", "s3", "ab", "a", "aa")));
        expected.add(new ArrayList<>(Arrays.asList("pb", "b", "alarm", "bb", "ba", "s4")));
        expected.add(new ArrayList<>(Arrays.asList("ea", "s2", "eb", "s1", "env")));
        testExample(burglar + "burglar", expected);

        expected = new ArrayList<>();
        expected.add(new ArrayList<>(Arrays.asList("a", "s3", "pa", "aa", "ab", "pb", "ba", "bb", "alarm")));
        expected.add(new ArrayList<>(Arrays.asList("b", "s4", "pb", "bb", "ba", "alarm", "pa", "aa", "ab")));
        expected.add(new ArrayList<>(Arrays.asList("ea", "s2", "eb", "s1", "env")));
        testExample(burglar + "burglar1", expected);
    }

    @Test(enabled = true)
    public void testChains() throws Exception {
        final String folder = "toyexamples/";
        List<List<String>> expected = new ArrayList<>();
        expected.add(new ArrayList<>(Arrays.asList("sa1", "sa2", "sa3", "sa4")));
        expected.add(new ArrayList<>(Arrays.asList("sb1", "sb2", "sb3", "sb4")));
        expected.add(new ArrayList<>(Arrays.asList("env", "env1")));
        testExample(folder + "chains", expected);

        expected = new ArrayList<>();
        expected.add(new ArrayList<>(Arrays.asList("sa1", "sa2", "sb3", "sb4")));
        expected.add(new ArrayList<>(Arrays.asList("sb1", "sb2", "sa3", "sa4")));
        expected.add(new ArrayList<>(Arrays.asList("env", "env1")));
        testExample(folder + "chains0", expected);

        expected = new ArrayList<>();
        expected.add(new ArrayList<>(Arrays.asList("sa1", "sa2")));
        expected.add(new ArrayList<>(Arrays.asList("sa3", "sa4")));
        expected.add(new ArrayList<>(Arrays.asList("sb1", "sb2")));
        expected.add(new ArrayList<>(Arrays.asList("sb3", "sb4")));
        expected.add(new ArrayList<>(Arrays.asList("env", "env1")));
        testExample(folder + "chains1", expected);
    }

    @Test
    public void testType2() throws Exception {
        final String folder = "toyexamples/";
        List<List<String>> expected = new ArrayList<>();
        expected.add(new ArrayList<>(Arrays.asList("s1", "s2")));
        expected.add(new ArrayList<>(Arrays.asList("env", "env1")));
        testExample(folder + "type2", expected);
    }

}
