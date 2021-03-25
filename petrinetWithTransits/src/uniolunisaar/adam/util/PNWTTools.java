package uniolunisaar.adam.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import uniol.apt.adt.extension.ExtensionProperty;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.Node;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.parser.ParseException;
import uniol.apt.io.parser.impl.AptPNParser;
import uniol.apt.io.renderer.RenderException;
import uniol.apt.io.renderer.impl.AptPNRenderer;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;
import uniolunisaar.adam.ds.petrinetwithtransits.DataFlowTree;
import uniolunisaar.adam.ds.petrinetwithtransits.DataFlowTreeNode;
import uniolunisaar.adam.ds.petrinetwithtransits.Transit;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.exceptions.ExternalToolException;
import uniolunisaar.adam.logic.parser.transits.TransitParser;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.logic.externaltools.pn.Dot;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
public class PNWTTools {

    //%%%%%%%%%% The following methods are used for the parsing, here the 
    //           Extensions are overwritten because they are stored as String
    //           during the parsing. Thus, we handle them separately.
    static boolean hasTransitAnnotation(Transition t) {
        return ExtensionManagement.getInstance().hasExtension(t, AdamPNWTExtensions.tfl);
    }

    static String getTransitAnnotation(Transition t) {
        String tfl = ExtensionManagement.getInstance().getExtension(t, AdamPNWTExtensions.tfl, String.class);
        if (tfl.equals(AdamPNWTExtensions.tfl.name())) {
            tfl = "";
        }
        return tfl;
    }

    private static void setTransitAnnotation(Transition t, String text) {
        ExtensionManagement.getInstance().putExtension(t, AdamPNWTExtensions.tfl, text, ExtensionProperty.WRITE_TO_FILE, ExtensionProperty.NOCOPY);
    }

    private static void parseAndCreateTransitsFromTransitionExtensionText(PetriNetWithTransits net, boolean withAutomatic) throws ParseException {
//        //todo: hack. change it, when the new implemenation of the flows is implmemented
//        if (net.hasExtension(AdamExtensions.condition.name())) {
//            if (net.getExtension(AdamExtensions.condition.name()).equals("A_SAFETY")
//                    || net.getExtension(AdamExtensions.condition.name()).equals("SAFETY")
//                    || net.getExtension(AdamExtensions.condition.name()).equals("E_REACHABILITY")
//                    || net.getExtension(AdamExtensions.condition.name()).equals("REACHABILITY")) {
//                return;
//            }
//        } else if (net.hasExtension(AdamExtensions.winningCondition.name())) { // todo: this is only for the fallback to the just-sythesis-version.
//            if (net.getExtension(AdamExtensions.winningCondition.name()).equals("A_SAFETY")
//                    || net.getExtension(AdamExtensions.winningCondition.name()).equals("SAFETY")
//                    || net.getExtension(AdamExtensions.winningCondition.name()).equals("E_REACHABILITY")
//                    || net.getExtension(AdamExtensions.winningCondition.name()).equals("REACHABILITY")) {
//                return;
//            }
//        }

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
                // clear the entry because this just for saving and is not changed when any transit is changed.
                ExtensionManagement.getInstance().removeExtension(t, AdamPNWTExtensions.tfl);
            } else if (withAutomatic) {
                if (t.getPreset().size() == 1) {
                    Place pre = t.getPreset().iterator().next();
                    Set<Place> postset = t.getPostset();
                    net.createTransit(pre, t, postset.toArray(new Place[postset.size()]));
                }
            }
        }
    }

    public static PetriNetWithTransits getPetriNetWithTransitsFromFile(String path, boolean withAutomatic) throws ParseException, IOException {
        PetriNet pn = Tools.getPetriNet(path);
        return getPetriNetWithTransitsFromParsedPetriNet(pn, withAutomatic);
    }

    /**
     * Creates a PetriNetWithTransits from the string in apt format given in
     * content.
     *
     * @param content
     * @param withAutomatic
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static PetriNetWithTransits getPetriNetWithTransits(String content, boolean withAutomatic) throws ParseException, IOException {
        PetriNet pn = Tools.getPetriNetFromString(content);
        return getPetriNetWithTransitsFromParsedPetriNet(pn, withAutomatic);
    }

    public static PetriNetWithTransits getPetriNetWithTransitsFromParsedPetriNet(PetriNet net, boolean withAutomatic) throws ParseException {
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
    // %%%%%%%%%%%%%%%%%%% END method for parsing

    public static void saveAPT(String path, PetriNetWithTransits net, boolean withAnnotationPartition, boolean withCoordinates) throws RenderException, FileNotFoundException {
        String file = net.toAPT(withAnnotationPartition, withCoordinates);
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
            } else if (net.isInitialTransit(place)) {
                sb.append(", style=dashed");
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

    public static Thread savePnwt2DotAndPDF(String input, String output, boolean withLabel) throws FileNotFoundException, ParseException, IOException {
        PetriNetWithTransits net = getPetriNetWithTransitsFromParsedPetriNet(new AptPNParser().parseFile(input), false);
        return savePnwt2DotAndPDF(output, net, withLabel);
    }

    public static Thread savePnwt2DotAndPDF(String path, PetriNetWithTransits net, boolean withLabel) throws FileNotFoundException {
        return savePnwt2DotAndPDF(path, net, withLabel, -1);
    }

    public static Thread savePnwt2DotAndPDF(String path, PetriNetWithTransits net, boolean withLabel, Integer tokencount) throws FileNotFoundException {
        if (tokencount == -1) {
            savePnwt2Dot(path, net, withLabel);
        } else {
            savePnwt2Dot(path, net, withLabel, tokencount);
        }
        // start rendering in an extra thread
        Thread thread = new Thread(() -> {
            try {
                Dot.call(path + ".dot", path, true, PetriNetExtensionHandler.getProcessFamilyID(net));
                Logger.getInstance().addMessage("Saved to: " + path + ".pdf", true);
//                    if (deleteDot) {
//                        // Delete dot file
//                        new File(path + ".dot").delete();
//                        Logger.getInstance().addMessage("Deleted: " + path + ".dot", true);
//                    }
            } catch (IOException | InterruptedException | ExternalToolException ex) {
                Logger.getInstance().addError("Saving pdf from dot failed.", ex);
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

    public static Thread savePnwt2PDF(String path, PetriNetWithTransits net, boolean withLabel) throws FileNotFoundException {
        return savePnwt2PDF(path, net, withLabel, -1);
    }

    public static Thread savePnwt2PDF(String path, PetriNetWithTransits net, boolean withLabel, Integer tokencount) throws FileNotFoundException {
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
                ex.printStackTrace();
                Logger.getInstance().addError("Deleting the buffered dot file and moving the pdf failed", ex);
            }
        });
        mvPdf.start();
        return mvPdf;
    }

    /**
     * Creates a PetriNetWithTransits which has automatically named nodes and
     * the original ids in the label of the node. This can be used to create a
     * net which is definitely readably be the APT parser.
     *
     * @param net
     * @return
     */
    public static PetriNetWithTransits createPNWTWithIDsInLabel(PetriNetWithTransits net) {
        PetriNetWithTransits out = new PetriNetWithTransits(net.getName());
        addElementsForPNWTWithIDsInLabel(net, out);
        return out;
    }

    public static Map<Node, Node> addElementsForPNWTWithIDsInLabel(PetriNetWithTransits net, PetriNetWithTransits out) {
        Map<Node, Node> mapping = PNTools.addElementsForPetriNetWithIDsInLabel(net, out);
        for (Transition transition : net.getTransitions()) {
            for (Transit transit : net.getTransits(transition)) {
                String[] postset = new String[transit.getPostset().size()];
                int i = 0;
                for (Place post : transit.getPostset()) {
                    postset[i] = mapping.get(post).getId();
                    ++i;
                }
                if (transit.isInitial()) {
                    out.createInitialTransit(mapping.get(transit.getTransition()).getId(), postset);
                } else {
                    out.createTransit(mapping.get(transit.getPresetPlace()).getId(), transition.getId(), postset);
                }
            }
        }
        return mapping;
    }

    public static List<DataFlowTree> getDataFlowTrees(PetriNetWithTransits pnwt, List<Transition> firingSequence) {
        List<DataFlowTree> trees = new ArrayList<>();
        // the list of current leaves of all trees
        List<DataFlowTreeNode> children = new ArrayList<>();
        // for each transition in the firing sequence
        for (int i = 0; i < firingSequence.size(); i++) {
            Transition t = firingSequence.get(i);
            Collection<Transit> transits = pnwt.getTransits(t);
            for (Transit transit : transits) {
                if (transit.isInitial()) { // when initial create a new tree
                    DataFlowTree tree = new DataFlowTree(t);
                    trees.add(tree);
                    tree.getRoot().addChildren(transit.getPostset());
                    children.addAll(tree.getRoot().getChildren());
                } else { // when not initial check all current leaves of the trees and possible extend the trees
                    Place pre = transit.getPresetPlace();
                    List<DataFlowTreeNode> childrenToRemove = new ArrayList<>();
                    List<DataFlowTreeNode> childrenToAdd = new ArrayList<>();
                    for (DataFlowTreeNode child : children) {
                        if (child.getNode().getId().equals(pre.getId())) { // the preset of the transit is a current leave of any tree
                            childrenToRemove.add(child); // this is no leave a the tree anymore                                   
                            // add transition as child
                            DataFlowTreeNode tNode = child.addChild(t);
                            // and all the transit successors
                            tNode.addChildren(transit.getPostset());
                            childrenToAdd.addAll(tNode.getChildren()); // add the new leaves of the tree
                        }
                    }
                    children.removeAll(childrenToRemove);
                    children.addAll(childrenToAdd);
                }
            }
        }
        return trees;
    }

    private static void addChild(DataFlowTree tree, DataFlowTreeNode node, StringBuilder sb) {
        String parentID = tree.hashCode() + "." + node.getParent().hashCode();
        String childID = tree.hashCode() + "." + node.hashCode();
        // child node
        sb.append("\"").append(childID).append("\"[label=\"").append(node.getNode().getId()).append("\", shape=none]\n");
        // edge
        sb.append("\"").append(parentID).append("\"").append("->").append("\"").append(childID).append("\"\n");
        // if there is no successor this branch is finished
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return;
        }
        // add the next transition
        DataFlowTreeNode tNode = node.getChildren().get(0); // there can only be one child
        String tNodeID = tree.hashCode() + "." + tNode.hashCode();
        sb.append("\"").append(tNodeID).append("\"[shape=point]").append("\n");
        sb.append("\"").append(childID).append("\"").append("->").append("\"").append(tNodeID);
        sb.append("\"[label=\"").append(tNode.getNode().getId()).append("\",arrowhead=none]\n");
        // recursively all children
        for (DataFlowTreeNode child : tNode.getChildren()) {
            addChild(tree, child, sb);
        }
    }

    public static String dataFlowTreesToDot(List<DataFlowTree> trees) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph DataFlowTrees {\n");

        if (trees.isEmpty()) {
            sb.append("# there is no tree\n");
            sb.append("\"For the given firing sequence there exists no data flow tree.\"[shape=none]");
        }

        for (DataFlowTree tree : trees) {
            DataFlowTreeNode pre = tree.getRoot();
            // init of the tree o--ti--.
            String initId = "init." + tree.hashCode();
            String dotId = tree.hashCode() + "." + pre.hashCode();
            sb.append("# new tree\n");
            sb.append("\"").append(initId).append("\"[shape=circle, height=0.1, width=0.1, fixedsize=true, label=\"\"]").append("\n");
            sb.append("\"").append(dotId).append("\"[shape=point]").append("\n");
            sb.append("\"").append(initId).append("\"").append("->").append("\"").append(dotId);
            sb.append("\"[label=\"").append(pre.getNode().getId()).append("\",arrowhead=none]\n");
            // add recursively all children
            for (DataFlowTreeNode child : pre.getChildren()) {
                addChild(tree, child, sb);
            }
        }
        sb.append("\n\n");
        sb.append("overlap=false\n");
//        sb.append("label=\"").append(net.getName()).append("\"\n");
        sb.append("fontsize=12\n");
        sb.append("}");
        return sb.toString();
    }

    public static void saveDataFlowTreesToPDF(String path, List<DataFlowTree> trees, String procID) throws FileNotFoundException, IOException {
        Tools.saveFile(path + ".dot", dataFlowTreesToDot(trees));
        try {
            Dot.call(path + ".dot", path, true, procID);
        } catch (IOException | InterruptedException | ExternalToolException ex) {
            File dotFile = new File(path + ".dot");
            if (dotFile.exists()) {
//                Files.delete(dotFile.toPath());
            }
            Logger.getInstance().addError("Saving pdf from dot failed.", ex);
        }
    }
}
