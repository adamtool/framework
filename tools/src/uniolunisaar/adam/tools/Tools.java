package uniolunisaar.adam.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.parser.ParseException;
import uniol.apt.io.parser.impl.AptPNParser;
import uniol.apt.io.renderer.RenderException;
import uniol.apt.io.renderer.impl.AptPNRenderer;
import uniol.apt.module.exception.ModuleException;

/**
 *
 * @author Manuel Gieseking
 */
public class Tools {

    /**
     * Returns A-Z, a-z, 0-... for i>=0
     *
     * @param i
     * @return
     */
    public static String calcStringID(int i) {
        assert i >= 0;
        if (i >= 0 && i < 26) {
            return Character.toString((char) (i + 65));
        }
        if (i > 25 && i < 52) {
            return Character.toString((char) (i + 71));
        }
        return String.valueOf(i - 52);
    }

    /**
     * Returns a-z, A-Z, 0-... for i>=0
     *
     * @param i
     * @return
     */
    public static String calcStringIDSmallPrecedence(int i) {
        assert i >= 0;
        if (i >= 0 && i < 26) {
            return Character.toString((char) (i + 97));
        }
        if (i > 25 && i < 52) {
            return Character.toString((char) (i + 39));
        }
        return String.valueOf(i - 52);
    }

    /**
     * Returns the reverse of calcStringIDSmallPrecendence. So it really have to
     * be calculated with this method!
     *
     * @param id
     * @return
     */
    public static int calcStringIDSmallPrecedenceReverse(String id) {
        assert id.length() == 1;
        int i = (int) id.charAt(0);
        assert i >= 0;
        if (i >= 97 && i <= 122) {
            return i - 97;
        }
        if (i >= 65 && i <= 90) {
            return i - 65 + 26;
        }
        return i + 52;
    }
//
//    /**
//     * @deprecated not in use anymore
//     * @param net
//     * @return 
//     */
//    public static String getTransition2IDMapping(PetriNet net) {
//        String ret = "";
//        for (Transition t : net.getTransitions()) {
//            ret += t.getId() + ":" + t.getExtension("id") + ", ";
//        }
//        return ret;
//    }

    /**
     * Returns a Petri net parsed from the given string
     *
     * @param content
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static PetriNet getPetriNetFromString(String content) throws ParseException, IOException {
        PetriNet net = new AptPNParser().parseString(content);
        Set<Transition> transitions = new HashSet<>(net.getTransitions());
        for (Transition trans : transitions) {
            if (trans.getPreset().isEmpty() && trans.getPostset().isEmpty()) {
                net.removeTransition(trans);
                Logger.getInstance().addWarning("You added a transition (" + trans + ") with an empty pre- and postset. We deleted it for usability reasons.");
            }
        }
        return net;
    }

    /**
     * Returns a Petri net parsed from the given file at the path
     *
     * @param path
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static PetriNet getPetriNet(String path) throws ParseException, IOException {
        PetriNet net = new AptPNParser().parseFile(path);
        Set<Transition> transitions = new HashSet<>(net.getTransitions());
        for (Transition trans : transitions) {
            if (trans.getPreset().isEmpty() && trans.getPostset().isEmpty()) {
                net.removeTransition(trans);
                Logger.getInstance().addWarning("You added a transition (" + trans + ") with an empty pre- and postset. We deleted it for usability reasons.");
            }
        }
        return net;
    }

    public static String getPN(PetriNet net) throws RenderException {
        String file = new AptPNRenderer().render(net);
        // TODO: Since the APT-Renderer isn't adaptive enough, 
        // delete the quotation marks around the token number
        file = file.replaceAll("token=\"([^\"]*)\"", "token=$1");
        return file;
    }

    public static void savePN(String path, PetriNet net) throws FileNotFoundException, ModuleException {
        String file = getPN(net);
        try (PrintStream out = new PrintStream(path + ".apt")) {
            out.println(file);
        }
        Logger.getInstance().addMessage("Saved to: " + path + ".apt", false);
    }

    public static void saveFile(String path, String content) throws FileNotFoundException {
        try (PrintStream out = new PrintStream(path)) {
            out.println(content);
            Logger.getInstance().addMessage("Saved to: " + path, false);
        }
    }

    public static void saveFile(String path, byte[] content) throws FileNotFoundException, IOException {
        try (OutputStream out = new FileOutputStream(path)) {
            out.write(content);
            Logger.getInstance().addMessage("Saved to: " + path, false);
        }
    }

    public static String readFile(String path) throws IOException {
        return FileUtils.readFileToString(new File(path));
    }

    public static void deleteFile(String path) {
        new File(path).delete();
        Logger.getInstance().addMessage("Deleted: " + path, true);
    }

}
