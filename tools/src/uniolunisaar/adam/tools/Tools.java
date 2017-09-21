package uniolunisaar.adam.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.Marking;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.analysis.coverability.CoverabilityGraph;
import uniol.apt.analysis.coverability.CoverabilityGraphNode;
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
     * Returns A-Za-z0-... for i>=0
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

    public static String getTransition2IDMapping(PetriNet net) {
        String ret = "";
        for (Transition t : net.getTransitions()) {
            ret += t.getId() + ":" + t.getExtension("id") + ", ";
        }
        return ret;
    }

    public static String petriNet2Dot(PetriNet net, boolean withLabel) {
        return petriNet2Dot(net, withLabel, null);
    }

    public static String petriNet2Dot(PetriNet net, boolean withLabel, Integer tokencount) {
        final String placeShape = "circle";
        final String specialPlaceShape = "doublecircle";

        StringBuilder sb = new StringBuilder();
        sb.append("digraph PetriNet {\n");

        // Transitions
        sb.append("#transitions\n");
        sb.append("node [shape=box, height=0.5, width=0.5, fixedsize=true]; ");
        for (Transition t : net.getTransitions()) {
            if (withLabel) {
                sb.append(t.getId()).append("[xlabel=").append(t.getLabel()).append("];");
            } else {
                sb.append(t.getId()).append(";");
            }
        }
        sb.append("\n\n");

        // Places
        sb.append("#places\n");
        for (Place place : net.getPlaces()) {
            // special?
            String shape = (place.hasExtension("bad") || place.hasExtension("reach") || place.hasExtension("buchi")) ? specialPlaceShape : placeShape;
            // Initialtoken number
            Long token = place.getInitialToken().getValue();
            String tokenString = (token > 0) ? token.toString() : "";
            // Drawing
            sb.append(place.getId()).append("[shape=").append(shape);
            sb.append(", height=0.5, width=0.5, fixedsize=true");
            sb.append(", xlabel=").append("\"").append(place.getId()).append("\"");
            sb.append(", label=").append("\"").append(tokenString).append("\"");
            // Systemplace?
            if (!place.hasExtension("env")) {
                sb.append(", style=filled, fillcolor=");
                if (tokencount == null) {
                    sb.append("gray");
                } else {
                    sb.append("\"");
                    int t = (Integer) place.getExtension("token");
                    float val = ((t + 1) * 1.f) / (tokencount * 1.f);
                    sb.append(val).append(" ").append(val).append(" ").append(val);
                    sb.append("\"");
                }
            }
            sb.append("];\n");
        }

        // Flows
        sb.append("\n#flows\n");
        for (Flow f : net.getEdges()) {
            sb.append(f.getSource().getId()).append("->").append(f.getTarget().getId());
            Integer w = f.getWeight();
            String weight = "\"" + ((w != 1) ? w.toString() : "") + "\"";
            sb.append("[label=").append(weight).append("]\n");
        }
        sb.append("overlap=false\n");
        sb.append("label=\"").append(net.getName()).append("\"\n");
        sb.append("fontsize=12\n");
        sb.append("}");
        return sb.toString();
    }

    public static String pg2Tikz(PetriNet net) {
        StringBuilder sb = new StringBuilder();
        sb.append("\\begin{tikzpicture}[node distance=12mm,>=stealth',bend angle=15,auto]\n");
        // Places
        for (Place place : net.getPlaces()) {
            // Bad?
            String bad = (place.hasExtension("bad") || place.hasExtension("reach") || place.hasExtension("buchi")) ? ",bad" : "";
            // Initialtoken number
            Long token = place.getInitialToken().getValue();
            String tokenString = (token > 0) ? ",tokens=" + token.toString() : "";
            // Systemplace?
            String type = place.hasExtension("env") ? "envplace" : "sysplace";
            sb.append("\\node [").append(type).append(bad).append(tokenString).append("] (").append(place.getId()).append(") [label=above:\\(").append(place.getId()).append("\\)] {};\n");
        }
        sb.append("\n\n");

        // Transitions
        for (Transition t : net.getTransitions()) {
            sb.append("\\node [transition] (").append(t.getId()).append(") {\\(").append(t.getLabel()).append("\\)};\n");
        }
        sb.append("\n\n");

        // Flows
        sb.append("\\draw[->] \n");
        for (Transition t : net.getTransitions()) {
            sb.append("(").append(t.getId()).append(")");
            for (Place p : t.getPreset()) {
                sb.append(" edge [pre] (").append(p.getId()).append(")\n");
            }
            for (Place p : t.getPostset()) {
                sb.append(" edge [post] (").append(p.getId()).append(")\n");
            }
        }
        sb.append(";\n");
        sb.append("\\end{tikzpicture}");
        return sb.toString();
    }

    public static void savePN2DotAndPDF(String path, PetriNet net, boolean withLabel) throws IOException, InterruptedException {
        savePN2Dot(path, net, withLabel);
        Runtime rt = Runtime.getRuntime();
//        String exString = "dot -Tpdf " + path + ".dot > " + path + ".pdf";
        String exString = "dot -Tpdf " + path + ".dot -o " + path + ".pdf";
        Process p = rt.exec(exString);
        p.waitFor();
//            rt.exec("evince " + path + ".pdf");

        Logger.getInstance().addMessage("Saved to: " + path + ".pdf", false);
    }

    public static void savePN2DotAndPDF(String path, PetriNet net, boolean withLabel, Integer tokencount) throws IOException, InterruptedException {
        savePN2Dot(path, net, withLabel, tokencount);
        Runtime rt = Runtime.getRuntime();
//        String exString = "dot -Tpdf " + path + ".dot > " + path + ".pdf";
        String exString = "dot -Tpdf " + path + ".dot -o " + path + ".pdf";
        Process p = rt.exec(exString);
        p.waitFor();
//            rt.exec("evince " + path + ".pdf");
        Logger.getInstance().addMessage("Saved to: " + path + ".pdf", false);
    }

    public static void savePN2PDF(String path, PetriNet net, boolean withLabel, Integer tokencount) throws IOException, InterruptedException {
        savePN2DotAndPDF(path, net, withLabel, tokencount);
        // Delete dot file
        new File(path + ".dot").delete();
    }

    public static void savePN2PDF(String path, PetriNet net, boolean withLabel) throws IOException, InterruptedException {
        savePN2DotAndPDF(path, net, withLabel);
        // Delete dot file
        new File(path + ".dot").delete();
    }

    public static void savePN2Dot(String path, PetriNet net, boolean withLabel) throws FileNotFoundException {
        try (PrintStream out = new PrintStream(path + ".dot")) {
            out.println(petriNet2Dot(net, withLabel));
        }
        Logger.getInstance().addMessage("Saved to: " + path + ".dot", false);
    }

    public static void savePN2Dot(String path, PetriNet net, boolean withLabel, Integer tokencount) throws FileNotFoundException {
        try (PrintStream out = new PrintStream(path + ".dot")) {
            out.println(petriNet2Dot(net, withLabel, tokencount));
        }
        Logger.getInstance().addMessage("Saved to: " + path + ".dot", false);
    }

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
                Logger.getInstance().addMessage("[WARNING] You added a transition (" + trans + ") with an empty pre- and postset. We deleted it for usability reasons.", false);
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
                Logger.getInstance().addMessage("[WARNING] You added a transition (" + trans + ") with an empty pre- and postset. We deleted it for usability reasons.", false);
            }
        }
        return net;
    }

    public static void savePN2Dot(String input, String output, boolean withLabel) throws IOException, ParseException {
        PetriNet pn = getPetriNet(input);
        savePN2Dot(output, pn, withLabel);
    }

    public static void savePN2DotAndPDF(String input, String output, boolean withLabel) throws IOException, InterruptedException, ParseException {
        PetriNet pn = new AptPNParser().parseFile(input);
        savePN2DotAndPDF(output, pn, withLabel);
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

    public static boolean isDeterministic(PetriNet strat, CoverabilityGraph cover) {
        boolean det = true;
        for (Place place : strat.getPlaces()) {
            if (!place.hasExtension("env")) {
                Set<Transition> post = place.getPostset();
                for (Iterator<CoverabilityGraphNode> iterator = cover.getNodes().iterator(); iterator.hasNext();) {
                    CoverabilityGraphNode next = iterator.next();
                    Marking m = next.getMarking();
                    boolean exTransition = false;
                    for (Transition transition : post) {
                        Set<Place> pre = transition.getPreset();
                        boolean isSubset = true;
                        for (Place place1 : pre) {
                            if (m.getToken(place1).getValue() <= 0) {
                                isSubset = false;
                            }
                        }
                        if (isSubset) {
                            if (exTransition) {
                                return false;
                            }
                            exTransition = true;
                        }
                    }
                }
            }
        }
        return det;
    }

    public static boolean isEnvTransition(Transition t) {
        for (Place p : t.getPreset()) {
            if (!p.hasExtension("env")) {
                return false;
            }
        }
        return true;
    }

    public static boolean restrictsEnvTransition(PetriNet origNet, PetriNet strat) {
        for (Place place : strat.getPlaces()) { // every env place of the strategy
            if (place.hasExtension("env")) {
                String id = (String) place.getExtension("origID");
                Place origPlace = origNet.getPlace(id);
                Set<Transition> post = origPlace.getPostset();
                for (Transition transition : post) {
                    if (isEnvTransition(transition)) { // should not restrict a single env transition
                        boolean found = false;
                        for (Transition t : place.getPostset()) {
                            // we must find the id of transition "transition"
                            if (t.getLabel().equals(transition.getId())) {
                                found = true;
                            }
                        }
                        if (!found) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isDeadlockAvoiding(PetriNet origNet, PetriNet strat, CoverabilityGraph cover) {
        for (Iterator<CoverabilityGraphNode> iterator = cover.getNodes().iterator(); iterator.hasNext();) {
            CoverabilityGraphNode next = iterator.next();
            Marking m = next.getMarking();
            // Get marking in original net
            Marking mappedMarking = new Marking(origNet);
            for (Place place : strat.getPlaces()) {
                int val = (int) m.getToken(place).getValue();
                if (val > 0) {
                    mappedMarking = mappedMarking.addTokenCount((String) place.getExtension("origID"), val);
                }
            }
            // if there's a transition in the original isfirable
            boolean firable = false;
            for (Transition t : origNet.getTransitions()) {
                if (t.isFireable(mappedMarking)) {
                    firable = true;
                    break;
                }
            }
            if (firable) { // there must also be a firable transition in the strategy                
                boolean stratfirable = false;
                for (Transition t : strat.getTransitions()) {
                    if (t.isFireable(m)) {
                        stratfirable = true;
                        break;
                    }
                }
                if (!stratfirable) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkStrategy(PetriNet origNet, PetriNet strat) {
        boolean isStrat = true;
        CoverabilityGraph cover = CoverabilityGraph.getReachabilityGraph(strat);
        // deadlock avoiding
        isStrat &= isDeadlockAvoiding(origNet, strat, cover);
        // (S1)
        isStrat &= isDeterministic(strat, cover);
        // (S2)
        isStrat &= !restrictsEnvTransition(origNet, strat);
        return isStrat;
    }
}
