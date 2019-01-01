package uniolunisaar.adam.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import uniol.apt.adt.extension.ExtensionProperty;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.parser.ParseException;
import uniol.apt.io.parser.impl.AptPNParser;
import uniol.apt.io.renderer.RenderException;
import uniol.apt.io.renderer.impl.AptPNRenderer;
import uniolunisaar.adam.exceptions.pnwt.CouldNotFindSuitableConditionException;
import uniolunisaar.adam.ds.petrinetwithtransits.Transit;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.logic.parser.transits.TransitParser;
import uniolunisaar.adam.tools.ExternalProcessHandler;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.ProcessNotStartedException;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
public class PNWTTools {

    private static boolean hasConditionAnnotation(PetriNet net) {
        return net.hasExtension(AdamExtensions.condition.name());
    }

    private static String getConditionAnnotation(PetriNet net) {
        return (String) net.getExtension(AdamExtensions.condition.name());
    }

//    private static void setConditionAnnotation(PetriNet net, Condition.Objective con) {
//        net.putExtension(AdamExtensions.condition.name(), con.name(), ExtensionProperty.WRITE_TO_FILE);
//    }
    public static Condition.Objective parseConditionFromNetExtensionText(PetriNetWithTransits net) throws CouldNotFindSuitableConditionException {
        if (hasConditionAnnotation(net)) {
            try {
                Condition.Objective winCon = Condition.Objective.valueOf(getConditionAnnotation(net));
                return winCon;
            } catch (ClassCastException | IllegalArgumentException e) {
                String con = getConditionAnnotation(net);
                // Set some standards concerning existential or universal
                if (con.equals("SAFETY")) {
                    return Condition.Objective.A_SAFETY;
                }
                if (con.equals("REACHABILITY")) {
                    return Condition.Objective.E_REACHABILITY;
                }
                if (con.equals("BUCHI")) {
                    return Condition.Objective.E_BUCHI;
                }
                throw new CouldNotFindSuitableConditionException(net, e);
            }
        } else {
            throw new CouldNotFindSuitableConditionException(net);
        }
    }

    static boolean hasTransitAnnotation(Transition t) {
        return t.hasExtension(AdamExtensions.tfl.name());
    }

    static String getTransitAnnotation(Transition t) {
        String tfl = (String) t.getExtension(AdamExtensions.tfl.name());
        if (tfl.equals(AdamExtensions.tfl.name())) {
            tfl = "";
        }
        return tfl;
    }

    private static void setTransitAnnotation(Transition t, String text) {
        t.putExtension(AdamExtensions.tfl.name(), text, ExtensionProperty.WRITE_TO_FILE);
    }

    private static void parseAndCreateTransitsFromTransitionExtensionText(PetriNetWithTransits net, boolean withAutomatic) throws ParseException {
        //todo: hack. change it, when the new implemenation of the flows is implmemented
        if (net.getExtension("winningCondition").equals("A_SAFETY")
                || net.getExtension("winningCondition").equals("SAFETY")
                || net.getExtension("winningCondition").equals("E_REACHABILITY")
                || net.getExtension("winningCondition").equals("REACHABILITY")) {
            return;
        }

        for (Transition t : net.getTransitions()) {
            if (hasTransitAnnotation(t)) {
                String flow = getTransitAnnotation(t);
                if (!flow.isEmpty()) {
//                    System.out.println("flow_" + flow + "_ende");
                    TransitParser.parse(net, t, flow);
//                    System.out.println(tfl.toString());
//                   //  old manual parser
//                String[] tupels = flow.split(",");
//                for (String tupel : tupels) {
//                    String[] comp = tupel.split("->");
//                    if (comp.length != 2) {
//                        throw new ParseException(tupel + " is not in a suitable format 'p1->p2'");
//                    }
//                    try {
//                        p1 = net.getPlace(comp[0]);
//                        p2 = net.getPlace(comp[1]);
//                        if (!t.getPreset().contains(p1)) {
//                            throw new ParseException(p1.getId() + " is not in the preset of transition " + t.getId() + " as annotated in " + tupel);
//                        }
//                        if (!t.getPostset().contains(p2)) {
//                            throw new ParseException(p2.getId() + " is not in the postset of transition " + t.getId() + " as annotated in " + tupel);
//                        }
//                    } catch (NoSuchNodeException e) {
//                        throw new ParseException(tupel + " does not point to existing nodes of the net '" + net.getName() + "'.", e);
//                    }
//                }
//                    game.setTokenFlow(t, tfl);
                }
            } else if (withAutomatic) {
                if (t.getPreset().size() == 1) {
                    Place pre = t.getPreset().iterator().next();
                    Set<Place> postset = t.getPostset();
                    net.createTransit(pre, t, postset.toArray(new Place[postset.size()]));
                }
            }
        }
    }

    /**
     * Creates a PetriNetWithTransits from the string in apt format given in
     * content.
     *
     * @param content
     * @param skipTests
     * @param withAutomatic
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static PetriNetWithTransits getPetriNetWithTransits(String content, boolean skipTests, boolean withAutomatic) throws ParseException, IOException {
        PetriNet pn = Tools.getPetriNetFromString(content);
        return getPetriNetWithTransitsFromParsedPetriNet(pn, skipTests, withAutomatic);
    }

    public static PetriNetWithTransits getPetriNetWithTransitsFromParsedPetriNet(PetriNet net, boolean skipTests, boolean withAutomatic) throws ParseException {
//        Condition.Condition win = parseConditionFromNetExtensionText(net);
        PetriNetWithTransits pnwt = new PetriNetWithTransits(net);
        parseAndCreateTransitsFromTransitionExtensionText(pnwt, withAutomatic);
//        if (win == Condition.Condition.E_SAFETY
//                || win == Condition.Condition.A_REACHABILITY
//                || win == Condition.Condition.E_BUCHI
//                || win == Condition.Condition.A_BUCHI
//                || win == Condition.Condition.E_PARITY
//                || win == Condition.Condition.A_PARITY) {
//        PetriGameAnnotator.parseAndAnnotateTokenflow(game, true);
//        } else if (win == Condition.Condition.A_SAFETY
//                || win == Condition.Condition.E_REACHABILITY) {
////            try {
////                parseAndAnnotateTokenflow(net);
////            } catch (ParseException pe) {
////
////            }
//        }
        return pnwt;
    }

    public static void saveAPT(String path, PetriNetWithTransits net, boolean withAnnotationPartition) throws RenderException, FileNotFoundException {
        String file = net.toAPT(withAnnotationPartition, false);
        Tools.saveFile(path + ".apt", file);
    }

    private static void setTransitAnnotation(PetriNetWithTransits net, Transition t) {
        if (net.hasTransit(t)) {
            Collection<Transit> flows = net.getTransits(t);
            StringBuilder sb = new StringBuilder("");
            boolean first = true;
            for (Transit flow : flows) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
//            sb.append("{");
//            boolean ffirst = true;
//            for (Place p : flow.getPreset()) {
//                if (ffirst) {
//                    ffirst = false;
//                } else {
//                    sb.append(",");
//                }
//                sb.append(p.getId());
//            }
//            sb.append("} -> {");
//            ffirst = true;
                if (flow.isInitial()) {
                    sb.append(">");
                } else {
                    sb.append(flow.getPresetPlace().getId());
                }
                sb.append(" -> {");
                boolean ffirst = true;
                for (Place p : flow.getPostset()) {
                    if (ffirst) {
                        ffirst = false;
                    } else {
                        sb.append(",");
                    }
                    sb.append(p.getId());
                }
                sb.append("}");
            }
            setTransitAnnotation(t, sb.toString());
        }
    }

    private static void renderTransits2TransitionExtensions(PetriNetWithTransits net) {
        for (Transition transition : net.getTransitions()) {
            setTransitAnnotation(net, transition);
        }
    }

    private static String deleteQuoteFromCoords(String aptText) {
        aptText = aptText.replaceAll(AdamExtensions.xCoord.name() + "=\"([^\"]*)\"", AdamExtensions.xCoord.name() + "=$1");
        aptText = aptText.replaceAll(AdamExtensions.yCoord.name() + "=\"([^\"]*)\"", AdamExtensions.yCoord.name() + "=$1");
        return aptText;
    }

    private static String deleteQuoteFromTokenIds(String aptText) {
        return aptText.replaceAll(AdamExtensions.token.name() + "=\"([^\"]*)\"", AdamExtensions.token.name() + "=$1");
    }

    public static String getAPT(PetriNetWithTransits net, boolean withAnnotationPartition, boolean withCoordinates) throws RenderException {
        renderTransits2TransitionExtensions(net);
        String file = new AptPNRenderer().render(net);
        // Since every value of the options is put as a String Object into the file,
        // delete the quotes for a suitable value (Integers can be parsed)
        // todo: maybe make the APT-Renderer more adaptive
        if (withCoordinates) {
            file = deleteQuoteFromCoords(file);
        }
        if (withAnnotationPartition) {
            file = deleteQuoteFromTokenIds(file);
        }
//        // Restore tokenflow objects
//        if (withAnnotationTokenflow) {
//            for (Transition transition : game.getTransitions()) { // todo: it's a quick hack here. Use AdamExtension and do this in another package
//                if (game.hasTransit(transition)) {
//                    game.setTokenFlow(transition, buffer.get(transition));
//                }
//            }
//        }
        return file;
    }

//    /**
//     * Creates a Petri game from the string in apt format given in content.
//     *
//     * @param content
//     * @param skipTests
//     * @param withAutomatic
//     * @return
//     * @throws NotSupportedGameException
//     * @throws ParseException
//     * @throws IOException
//     * @throws uniolunisaar.adam.logic.exceptions.CouldNotCalculateException
//     */
//    public static PetriGame getPetriGame(String content, boolean skipTests, boolean withAutomatic) throws NotSupportedGameException, ParseException, IOException, CouldNotCalculateException {
//        PetriNet pn = Tools.getPetriNetFromString(content);
//        return getPetriGameFromParsedPetriNet(pn, skipTests, withAutomatic);
//    }
//
//    public static PetriGame getPetriGameFromParsedPetriNet(PetriNet net, boolean skipTests, boolean withAutomatic) throws NotSupportedGameException, ParseException, CouldNotCalculateException {
////        Condition.Condition win = parseConditionFromNetExtensionText(net);
//        PetriGame game = new PetriGame(net, skipTests, new ConcurrencyPreservingCalculator(), new MaxTokenCountCalculator());
//        parseAndCreateTokenflowsFromTransitionExtensionText(game, withAutomatic);
////        if (win == Condition.Condition.E_SAFETY
////                || win == Condition.Condition.A_REACHABILITY
////                || win == Condition.Condition.E_BUCHI
////                || win == Condition.Condition.A_BUCHI
////                || win == Condition.Condition.E_PARITY
////                || win == Condition.Condition.A_PARITY) {
////        PetriGameAnnotator.parseAndAnnotateTokenflow(game, true);
////        } else if (win == Condition.Condition.A_SAFETY
////                || win == Condition.Condition.E_REACHABILITY) {
//////            try {
//////                parseAndAnnotateTokenflow(net);
//////            } catch (ParseException pe) {
//////
//////            }
////        }
//        return game;
//    }
    public static String pnwt2Dot(PetriNetWithTransits net, boolean withLabel) {
        return PNWTTools.pnwt2Dot(net, withLabel, null);
    }

//    public static String getFlowRepresentativ(int id) {
//        int start = 97; // a
//        if (id > 25 && id < 51) {
//            start = 65; //A
//            id -= 25;
//        } else {
//            start = 
//        }
//        return String.valueOf(start + id);
//    }
    public static String pnwt2Dot(PetriNetWithTransits net, boolean withLabel, Integer tokencount) {
        final String placeShape = "circle";
        final String specialPlaceShape = "doublecircle";

        StringBuilder sb = new StringBuilder();
        sb.append("digraph PetriNet {\n");

        // Transitions
        sb.append("#transitions\n");
        sb.append("node [shape=box, height=0.5, width=0.5, fixedsize=true];\n");
        for (Transition t : net.getTransitions()) {
            String c = null;
            if (net.isStrongFair(t)) {
                c = "blue";
            }
            if (net.isWeakFair(t)) {
                c = "lightblue";
            }
            String color = (c != null) ? "style=filled, fillcolor=" + c : "";

            sb.append("\"").append(t.getId()).append("\"").append("[").append(color);
            if (withLabel) {
                if (net.isStrongFair(t) || net.isWeakFair(t)) {
                    sb.append(", ");
                }
                sb.append("xlabel=\"").append(t.getLabel()).append("\"");
            }
            sb.append("];\n");
        }
        sb.append("\n\n");

        // Places
        sb.append("#places\n");
        for (Place place : net.getPlaces()) {
            // special?
            String shape = (net.isBad(place) || net.isReach(place) || net.isBuchi(place)) ? specialPlaceShape : placeShape;
            // Initialtoken number
            Long token = place.getInitialToken().getValue();
            String tokenString = (token > 0) ? token.toString() : "";
            // Drawing
            sb.append("\"").append(place.getId()).append("\"").append("[shape=").append(shape);
            sb.append(", height=0.5, width=0.5, fixedsize=true");
            sb.append(", xlabel=").append("\"").append(place.getId()).append("\"");
            sb.append(", label=").append("\"").append(tokenString).append("\"");

            if (net.hasPartition(place)) {
                int t = net.getPartition(place);
                if (t != 0) {  // should it be colored?
                    sb.append(", style=\"filled");
                    if (net.isInitialTransit(place)) {
                        sb.append(", dashed");
                    }
                    sb.append("\", fillcolor=");
                    if (tokencount == null) {
                        sb.append("gray");
                    } else {
                        sb.append("\"");
                        float val = ((t + 1) * 1.f) / (tokencount * 1.f);
                        sb.append(val).append(" ").append(val).append(" ").append(val);
                        sb.append("\"");
                    }
                } else if (net.isInitialTransit(place)) {
                    sb.append(", style=dashed");
                }
            }
            sb.append("];\n");
        }

        // Flows
        Map<Flow, String> map = getTransitRelationFromTransitions(net);
        sb.append("\n#flows\n");
        for (Flow f : net.getEdges()) {
            sb.append("\"").append(f.getSource().getId()).append("\"").append("->").append("\"").append(f.getTarget().getId()).append("\"");
            Integer w = f.getWeight();
            String weight = "\"" + ((w != 1) ? w.toString() + " : " : "");
            if (map.containsKey(f)) {
                weight += map.get(f);
            }
            weight += "\"";
            sb.append("[label=").append(weight);
            if (map.containsKey(f)) {
                String tfl = map.get(f);
                if (!tfl.contains(",")) {
                    sb.append(", color=\"");
                    Transit init = net.getInitialTransit(f.getTransition());
                    int max = net.getTransits(f.getTransition()).size() + ((init == null) ? 0 : init.getPostset().size() - 1);
                    int id = Tools.calcStringIDSmallPrecedenceReverse(tfl);
                    float val = ((id + 1) * 1.f) / (max * 1.f);
                    sb.append(val).append(" ").append(val).append(" ").append(val);
                    sb.append("\"");
                }
            }
            if (net.isInhibitor(f)) {
                sb.append(", dir=\"both\", arrowtail=\"odot\"");
            }
            sb.append("]\n");
        }
        sb.append("overlap=false\n");
        sb.append("label=\"").append(net.getName()).append("\"\n");
        sb.append("fontsize=12\n");
        sb.append("}");
        return sb.toString();
    }

    public static Map<Flow, String> getTransitRelationFromTransitions(PetriNetWithTransits net) {
        Map<Flow, String> map = new HashMap<>(); // store for each flow it's string representation of the token flows
        for (Transition t : net.getTransitions()) {
            if (!net.hasTransit(t)) {
                continue; // todo: make it sense for whole transitions when they have no tokenflwo?
            }
            Collection<Transit> tfls = net.getTransits(t);
            Transit initial = net.getInitialTransit(t);
            int size = (initial == null) ? tfls.size() : tfls.size() - 1;
            Iterator<Transit> it = tfls.iterator();
            for (int i = 0; i < size; i++) { // only not initial token flows
                Transit tfl = it.next();
//                for (Place p : tfl.getPreset()) {
                if (!tfl.isInitial()) {
                    String id = Tools.calcStringIDSmallPrecedence(i);
                    Place pl = tfl.getPresetPlace();
                    Flow f = net.getFlow(pl, t);
                    map.put(f, id);
                    for (Place p : tfl.getPostset()) {
                        f = net.getFlow(t, p);
                        if (!map.containsKey(f)) {
                            map.put(f, id);
                        } else {
                            String fl = map.get(f);
                            map.put(f, fl + "," + id);
                        }
                    }
                }
            }
            if (initial != null) { // initial token flows
                Set<Place> postset = initial.getPostset();
                Iterator<Place> iter = postset.iterator();
                for (int i = size; i < size + postset.size(); i++) {
                    String id = Tools.calcStringIDSmallPrecedence(i);
                    Place post = iter.next();
                    Flow f = net.getFlow(t, post);
                    if (!map.containsKey(f)) {
                        map.put(f, id);
                    } else {
                        String fl = map.get(f);
                        map.put(f, fl + "," + id);
                    }
                }
            }
        }
        return map;
    }

    public static void savePnwt2Dot(String input, String output, boolean withLabel) throws IOException, ParseException {
        PetriNetWithTransits net = new PetriNetWithTransits(Tools.getPetriNet(input));
        PNWTTools.savePnwt2Dot(output, net, withLabel);
    }

    public static void savePnwt2Dot(String path, PetriNetWithTransits net, boolean withLabel) throws FileNotFoundException {
        savePnwt2Dot(path, net, withLabel, -1);
    }

    public static void savePnwt2Dot(String path, PetriNetWithTransits net, boolean withLabel, Integer tokencount) throws FileNotFoundException {
        try (PrintStream out = new PrintStream(path + ".dot")) {
            if (tokencount == -1) {
                out.println(pnwt2Dot(net, withLabel));
            } else {
                out.println(pnwt2Dot(net, withLabel, tokencount));
            }
        }
        Logger.getInstance().addMessage("Saved to: " + path + ".dot", true);
    }

    public static Thread savePnwt2DotAndPDF(String input, String output, boolean withLabel) throws IOException, InterruptedException, ParseException {
        PetriNetWithTransits net = new PetriNetWithTransits(new AptPNParser().parseFile(input));
        return savePnwt2DotAndPDF(output, net, withLabel);
    }

    public static Thread savePnwt2DotAndPDF(String path, PetriNetWithTransits net, boolean withLabel) throws IOException, InterruptedException {
        return savePnwt2DotAndPDF(path, net, withLabel, -1);
    }

    public static Thread savePnwt2DotAndPDF(String path, PetriNetWithTransits net, boolean withLabel, Integer tokencount) throws IOException, InterruptedException {
        if (tokencount == -1) {
            savePnwt2Dot(path, net, withLabel);
        } else {
            savePnwt2Dot(path, net, withLabel, tokencount);
        }
        String[] command = {"dot", "-Tpdf", path + ".dot", "-o", path + ".pdf"};
        ExternalProcessHandler procH = new ExternalProcessHandler(true, command);
        // start it in an extra thread
        Thread thread = new Thread(() -> {
            try {
                procH.startAndWaitFor();
//                    if (deleteDot) {
//                        // Delete dot file
//                        new File(path + ".dot").delete();
//                        Logger.getInstance().addMessage("Deleted: " + path + ".dot", true);
//                    }
            } catch (IOException | InterruptedException ex) {
                String errors = "";
                try {
                    errors = procH.getErrors();
                } catch (ProcessNotStartedException e) {
                }
                Logger.getInstance().addError("Saving pdf from dot failed.\n" + errors, ex);
            }
        });
        thread.start();
        return thread;
        // older version
//        ProcessBuilder procBuilder = new ProcessBuilder("dot", "-Tpdf", path + ".dot", "-o", path + ".pdf");
//        Process proc = procBuilder.start();
//        String error = IOUtils.toString(proc.getErrorStream());
//        Logger.getInstance().addMessage(error, true); // todo: print it as error an a proper exception
//        String output = IOUtils.toString(proc.getInputStream());
//        Logger.getInstance().addMessage(output, true);
//        proc.waitFor();
//        Logger.getInstance().addMessage("Saved to: " + path + ".pdf", true);

        // oldest version
//        Runtime rt = Runtime.getRuntime();
////        String exString = "dot -Tpdf " + path + ".dot > " + path + ".pdf";
//        String exString = "dot -Tpdf " + path + ".dot -o " + path + ".pdf";
//        Process p = rt.exec(exString);
//        p.waitFor();
//            rt.exec("evince " + path + ".pdf");
//        Logger.getInstance().addMessage("Saved to: " + path + ".pdf", true);
    }

    public static Thread savePnwt2PDF(String path, PetriNetWithTransits net, boolean withLabel) throws IOException, InterruptedException {
        return savePnwt2PDF(path, net, withLabel, -1);
    }

    public static Thread savePnwt2PDF(String path, PetriNetWithTransits net, boolean withLabel, Integer tokencount) throws IOException, InterruptedException {
        String bufferpath = path + "_" + System.currentTimeMillis();
        Thread dot;
        if (tokencount == -1) {
            dot = savePnwt2DotAndPDF(bufferpath, net, withLabel);
        } else {
            dot = savePnwt2DotAndPDF(bufferpath, net, withLabel, tokencount);
        }
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
