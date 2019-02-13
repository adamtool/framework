package uniolunisaar.adam.tools;

import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.Test;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.io.parser.ParseException;
import uniol.apt.util.Pair;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestTools {

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

}
