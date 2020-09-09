package uniolunisaar.adam.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Transition;
import uniol.apt.analysis.bounded.Bounded;
import uniol.apt.analysis.bounded.BoundedResult;
import uniol.apt.io.parser.ParseException;
import uniol.apt.io.parser.impl.AptPNParser;
import uniol.apt.io.renderer.RenderException;
import uniol.apt.io.renderer.impl.AptPNRenderer;
import uniol.apt.module.exception.ModuleException;
import uniol.apt.util.Pair;
import uniolunisaar.adam.util.IDotSaveable;
import uniolunisaar.adam.util.PNTools;

/**
 *
 * @author Manuel Gieseking
 */
public class Tools {

    public static String fillupWithBlanks(int length, int maxLength) {
        String out = "";
        for (int i = length; i < maxLength; i++) {
            out += " ";
        }
        return out;
    }

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
        PNTools.annotateProcessFamilyID(net);
        return net;
    }

    public static String getPN(PetriNet net) throws RenderException {
        String file = new AptPNRenderer().render(net);
        // TODO: Since the APT-Renderer isn't adaptive enough, 
        // delete the quotation marks around the token number
        file = file.replaceAll("token=\"([^\"]*)\"", "token=$1");
        // the same for the coordinates
        file = file.replaceAll("xCoord=\"([^\"]*)\"", "xCoord=$1");
        file = file.replaceAll("yCoord=\"([^\"]*)\"", "yCoord=$1");
        return file;
    }

    /**
     * Saves the given Petri net as an APT file.
     *
     * @param path
     * @param net
     * @throws FileNotFoundException
     * @throws ModuleException
     */
    public static void savePN(String path, PetriNet net) throws FileNotFoundException, ModuleException {
        String file = getPN(net);
        try ( PrintStream out = new PrintStream(path + ".apt")) {
            out.println(file);
        }
        Logger.getInstance().addMessage("Saved to: " + path + ".apt", false);
    }

    public static void saveFile(String path, String content) throws FileNotFoundException {
        saveFile(path, content, false);
    }

    public static void saveFile(String path, String content, boolean append) throws FileNotFoundException {
        try ( PrintStream out = new PrintStream(new FileOutputStream(path, append))) {
            out.println(content);
            Logger.getInstance().addMessage("Saved to: " + path);
        }
    }

    public static void saveFile(String path, byte[] content) throws FileNotFoundException, IOException {
        try ( OutputStream out = new FileOutputStream(path)) {
            out.write(content);
            Logger.getInstance().addMessage("Saved to: " + path, false);
        }
    }

    public static String readFile(String path) throws IOException {
        return FileUtils.readFileToString(new File(path));
//        return IOUtils.readFileToString(new File(path));  // this currently has a problem for the sdn topology parser
    }

    public static void deleteFile(String path) {
        new File(path).delete();
        Logger.getInstance().addMessage("Deleted: " + path, true);
    }

    /**
     * Returns the concrete line and column of the occurrence of the error of
     * the given ParseException.
     *
     * @param e
     * @return The first value is the line, the second the column. If there are
     * no lines or columns given, -1 is returned.
     */
    public static Pair<Integer, Integer> getErrorLocation(ParseException e) {
        int line = -1;
        int col = -1;
        String[] msg = e.getMessage().split("line ");
        if (msg.length > 1) {
            msg = msg[1].split(" col ");
            if (msg.length > 1) {
                line = Integer.parseInt(msg[0]);
                col = Integer.parseInt(msg[1].substring(0, msg[1].indexOf(":")));
            }
        }
        return new Pair<>(line, col);
    }

    public static BoundedResult getBounded(PetriNet net) {
        return Bounded.checkBounded(net);
    }

    public static boolean isSafe(PetriNet net) {
        return getBounded(net).isSafe();
    }

    /**
     * Calculates the powerset of a given set. Algorithm from
     * https://stackoverflow.com/questions/1670862/obtaining-a-powerset-of-a-set-in-java
     *
     * @param <T>
     * @param originalSet
     * @return
     */
    public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
        Set<Set<T>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }
        List<T> list = new ArrayList<>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<T> set : powerSet(rest)) {
            Set<T> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    public static void save2Dot(String path, IDotSaveable object) throws FileNotFoundException {
        try ( PrintStream out = new PrintStream(path + ".dot")) {
            out.println(object.toDot());
        }
        Logger.getInstance().addMessage("Saved to: " + path + ".dot", true);
    }

    public static Thread save2DotAndPDF(String path, IDotSaveable object) throws IOException, InterruptedException {
        save2Dot(path, object);
        String dot = AdamProperties.getInstance().getProperty(AdamProperties.DOT);
        String[] command = {dot, "-Tpdf", path + ".dot", "-o", path + ".pdf"};
        uniolunisaar.adam.tools.processHandling.ExternalProcessHandler procH = new uniolunisaar.adam.tools.processHandling.ExternalProcessHandler(true, command);
        uniolunisaar.adam.tools.processHandling.ProcessPool.getInstance().putProcess(object.hashCode() + "#dot", procH);
        // start it in an extra thread
        Thread thread = new Thread(() -> {
            try {
                procH.startAndWaitFor();
                Logger.getInstance().addMessage("Saved to: " + path + ".pdf", true);
//                    if (deleteDot) {
//                        // Delete dot file
//                        new File(path + ".dot").delete();
//                        Logger.getInstance().addMessage("Deleted: " + path + ".dot", true);
//                    }
            } catch (IOException | InterruptedException ex) {
                String errors = "";
                try {
                    errors = procH.getErrors();
                } catch (uniolunisaar.adam.exceptions.ProcessNotStartedException e) {
                }
                Logger.getInstance().addError("Saving pdf from dot failed.\n" + errors, ex);
            }
        });
        thread.start();
        return thread;
    }

    public static Thread save2PDF(String path, IDotSaveable object) throws IOException, InterruptedException {
        String bufferpath = path + "_" + System.currentTimeMillis();
        Thread dot;
        dot = save2DotAndPDF(bufferpath, object);
        Thread mvPdf = new Thread(() -> {
            try {
                dot.join();
                // Delete dot file
                new File(bufferpath + ".dot").delete();
                Logger.getInstance().addMessage("Deleted: " + bufferpath + ".dot", true);
                // move to original name 
                Files.move(new File(bufferpath + ".pdf").toPath(), new File(path + ".pdf").toPath(), REPLACE_EXISTING);
                Logger.getInstance().addMessage("Moved: " + bufferpath + ".pdf --> " + path + ".pdf", true);
            } catch (IOException | InterruptedException ex) {
                Logger.getInstance().addError("Deleting the buffer files and moving the pdf failed", ex);
            }
        });
        mvPdf.start();
        return mvPdf;
    }
}
