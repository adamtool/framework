package uniolunisaar.adam.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import uniol.apt.adt.exception.StructureException;
import uniol.apt.adt.exception.TransitionFireException;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.Marking;
import uniol.apt.adt.pn.Node;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Token;
import uniol.apt.adt.pn.Transition;
import uniol.apt.analysis.bounded.BoundedResult;
import uniol.apt.analysis.coverability.CoverabilityGraph;
import uniol.apt.analysis.coverability.CoverabilityGraphNode;
import uniol.apt.analysis.language.FiringSequence;
import uniol.apt.io.parser.ParseException;
import uniol.apt.io.renderer.RenderException;
import uniol.apt.io.renderer.impl.PnmlPNRenderer;
import uniol.apt.util.interrupt.InterrupterRegistry;
import uniolunisaar.adam.ds.BoundingBox;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;
import uniolunisaar.adam.exceptions.ExternalToolException;
import uniolunisaar.adam.logic.externaltools.pn.Dot;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
public class PNTools {

    /**
     * Checks whether the given transition t is fireable in the given marking m.
     * Different to t.isFireable(m) this method also correctly handles inhibitor
     * arcs.
     *
     * @param t - the transition which is checked to be fireable
     * @param m - the marking in which the transition should be fireeable
     * @return true iff t is fireable in m (m[t>) respecting inhibitor arcs.
     */
    public static boolean isFireable(Transition t, Marking m) {
        PetriNet net = m.getNet();
        for (Flow f : net.getPresetEdges(t.getId())) {
            if (PetriNetExtensionHandler.isInhibitor(f)) {
                if (m.getToken(f.getPlace()).getValue() != 0) {
                    return false;
                }
            } else if (m.getToken(f.getPlace()).compareTo(Token.valueOf(f.getWeight())) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fires the given transition t in the given marking m. Different to
     * t.fire(m) this method also correctly handles inhibitor arcs.
     *
     * @param t - the transition which should be fired
     * @param m - the marking in which the transition should be fired
     * @return M' for m[t>M' respecting inhibitor arcs.
     */
    public static Marking fire(Transition t, Marking m) {
        PetriNet net = m.getNet();
        if (isFireable(t, m)) {
            for (Flow f : net.getPresetEdges(t.getId())) {
                if (!PetriNetExtensionHandler.isInhibitor(f)) {
                    m = m.addTokenCount(f.getPlace(), -f.getWeight());
                }
            }
            for (Flow f : net.getPostsetEdges(t.getId())) {
                if (!PetriNetExtensionHandler.isInhibitor(f)) {
                    m = m.addTokenCount(f.getPlace(), +f.getWeight());
                }
            }
            return m;
        } else {
            throw new TransitionFireException("transition '" + t.getId()
                    + "' is not fireable in marking '" + m.toString() + "'.");
        }
    }

    public static PetriNet addsBoundedness2Places(PetriNet net) {
        Collection<Place> places = net.getPlaces();
        // initialize them with 0 bounds for not reachable places
        for (Place place : places) {
            PetriNetExtensionHandler.setBoundedness(place, 0);
        }

        // check coverability graph
        CoverabilityGraph cover = CoverabilityGraph.get(net);
        for (CoverabilityGraphNode state : cover.getNodes()) {
            Marking marking = state.getMarking();
            for (Place place : places) {
                InterrupterRegistry.throwIfInterruptRequestedForCurrentThread();
                long k = PetriNetExtensionHandler.getBoundedness(place);
                Token value = marking.getToken(place);
                if (k < value.getValue() || value.isOmega()) {
                    PetriNetExtensionHandler.setBoundedness(place, value.getValue());
                }
            }
        }
        return net;
    }

    /**
     * Calculates the extension of the given Petri net with transits.
     *
     * @param net - the Petri net with transits
     * @return a bounding box with the extensions of net or 'null' if a node of
     * the net has no x-coordinate
     */
    public static BoundingBox calculateBoundingBox(PetriNet net) {
        double top = Double.MAX_VALUE, bottom = -Double.MAX_VALUE, left = Double.MAX_VALUE, right = -Double.MAX_VALUE;
        for (Node node : net.getNodes()) {
            if (!PetriNetExtensionHandler.hasXCoord(node)) {
                return null;
            }
            double xcoord = PetriNetExtensionHandler.getXCoord(node);
            if (xcoord < left) {
                left = xcoord;
            }
            if (xcoord > right) {
                right = xcoord;
            }
            double ycoord = PetriNetExtensionHandler.getYCoord(node);
            if (ycoord < top) {
                top = ycoord;
            }
            if (ycoord > bottom) {
                bottom = ycoord;
            }
        }
        return new BoundingBox(top, bottom, left, right);
    }

    /**
     * Returns, iff available the label of the node. For a transition
     * node.getLabel() for a place PetriNetExtensionHandler.getLabel(node).
     *
     * @param net
     * @param node
     * @return
     */
    public static String getLabel(PetriNet net, Node node) {
        if (!net.containsNode(node)) {
            throw new StructureException("Node '" + node.getId() + "' does not belong to net '" + net.getName() + "'");
        }
        if (net.containsTransition(node.getId())) {
            return ((Transition) node).getLabel();
        }
        Place p = (Place) node;
        if (!PetriNetExtensionHandler.hasLabel(p)) {
            throw new StructureException("Place '" + p.getId() + "' does not have a label.");
        }
        return PetriNetExtensionHandler.getLabel(p);
    }

    public static void annotateProcessFamilyID(PetriNet net) {
        PetriNetExtensionHandler.setProcessFamilyID(net, net.getName() + Thread.currentThread().getName());
    }

    public static PetriNet createPetriNet(String name) {
        PetriNet net = new PetriNet(name);
        PetriNetExtensionHandler.setProcessFamilyID(net, name + Thread.currentThread().getName());
        return net;
    }

    /**
     * Creates a PetriNet which has automatically named nodes and the original
     * ids in the label of the node. This can be used to create a net which is
     * definitely readably be the APT parser.
     *
     * @param net
     * @return
     */
    public static PetriNet createPetriNetWithIDsInLabel(PetriNet net) {
        PetriNet out = new PetriNet(net.getName());
        addElementsForPetriNetWithIDsInLabel(net, out);
        return out;
    }

    public static Map<Node, Node> addElementsForPetriNetWithIDsInLabel(PetriNet net, PetriNet out) {
        Map<Node, Node> mapping = new HashMap<>();
        Marking init = new Marking(out);
        for (Place place : net.getPlaces()) {
            Place p = out.createPlace();
            p.copyExtensions(place);
            PetriNetExtensionHandler.setLabel(p, place.getId());
            mapping.put(place, p);
            init = init.setTokenCount(p, place.getInitialToken());
        }
        out.setInitialMarking(init);
        for (Transition transition : net.getTransitions()) {
            Transition t = out.createTransition();
            t.copyExtensions(transition);
            t.setLabel(transition.getId());
            mapping.put(transition, t);
        }
        for (Flow edge : net.getEdges()) {
            Flow f = out.createFlow(mapping.get(edge.getSource()), mapping.get(edge.getTarget()), edge.getWeight());
            f.copyExtensions(edge);
        }

        for (Marking m : net.getFinalMarkings()) {
            Marking fin = new Marking(out);
            for (Place place : net.getPlaces()) {
                fin.setTokenCount((Place) mapping.get(place), m.getToken(place));
            }
            out.addFinalMarking(fin);
        }
        out.copyExtensions(net);
        return mapping;
    }

    public static String pn2Dot(PetriNet net, boolean withLabel, boolean withOrigPlaces, Integer tokencount) {
        final String placeShape = "circle";
        final String specialPlaceShape = "doublecircle";

        StringBuilder sb = new StringBuilder();
        sb.append("digraph PetriNet {\n");

        // Transitions
        sb.append("#transitions\n");
        sb.append("node [shape=box, height=0.5, width=0.5, fixedsize=true];\n");
        for (Transition t : net.getTransitions()) {
            String c = null;
            if (PetriNetExtensionHandler.isStrongFair(t)) {
                c = "blue";
            }
            if (PetriNetExtensionHandler.isWeakFair(t)) {
                c = "lightblue";
            }
            String color = (c != null) ? "style=filled, fillcolor=" + c : "";

            sb.append("\"").append(t.getId()).append("\"").append("[").append(color);
            if (withLabel) {
                if (PetriNetExtensionHandler.isStrongFair(t) || PetriNetExtensionHandler.isWeakFair(t)) {
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
            String shape = (PetriNetExtensionHandler.isBad(place) || PetriNetExtensionHandler.isReach(place) || PetriNetExtensionHandler.isBuchi(place)) ? specialPlaceShape : placeShape;
            // Initialtoken number
            Long token = place.getInitialToken().getValue();
            String tokenString = (token > 0) ? token.toString() : "";
            // Drawing
            String id = place.getId();
            if (withOrigPlaces && PetriNetExtensionHandler.hasOrigID(place)) {
                id += "(" + PetriNetExtensionHandler.getOrigID(place) + ")";
            }
            sb.append("\"").append(place.getId()).append("\"").append("[shape=").append(shape);
            sb.append(", height=0.5, width=0.5, fixedsize=true");
            sb.append(", xlabel=").append("\"").append(id).append("\"");
            sb.append(", label=").append("\"").append(tokenString).append("\"");
            sb.append("];\n");
        }

        // Flows
        sb.append("\n#flows\n");
        for (Flow f : net.getEdges()) {
            sb.append("\"").append(f.getSource().getId()).append("\"").append("->").append("\"").append(f.getTarget().getId()).append("\"");
            Integer w = f.getWeight();
            String weight = "\"" + ((w != 1) ? w.toString() + " : " : "");
            weight += "\"";
            sb.append("[label=").append(weight);
            if (PetriNetExtensionHandler.isInhibitor(f)) {
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

    public static String pn2Dot(PetriNet net, boolean withLabel, boolean withOrigPlaces) {
        return pn2Dot(net, withLabel, withOrigPlaces, null);
    }

    public static void savePN2Dot(String input, String output, boolean withLabel, boolean withOrigPlaces) throws IOException, ParseException {
        PetriNet net = Tools.getPetriNet(input);
        savePN2Dot(output, net, withLabel, withOrigPlaces);
    }

    public static void savePN2Dot(String path, PetriNet net, boolean withLabel, boolean withOrigPlaces) throws FileNotFoundException {
        savePN2Dot(path, net, withLabel, withOrigPlaces, -1);
    }

    public static void savePN2Dot(String path, PetriNet net, boolean withLabel, boolean withOrigPlaces, Integer tokencount) throws FileNotFoundException {
        try (PrintStream out = new PrintStream(path + ".dot")) {
            if (tokencount == -1) {
                out.println(pn2Dot(net, withLabel, withOrigPlaces));
            } else {
                out.println(pn2Dot(net, withLabel, withOrigPlaces, tokencount));
            }
        }
        Logger.getInstance().addMessage("Saved to: " + path + ".dot", true);
    }

    public static Thread savePN2DotAndPDF(String input, String output, boolean withLabel, boolean withOrigPlaces) throws FileNotFoundException, ParseException, IOException {
        PetriNet net = Tools.getPetriNet(input);
        return savePN2DotAndPDF(output, net, withLabel, withOrigPlaces);
    }

    public static Thread savePN2DotAndPDF(String path, PetriNet net, boolean withLabel, boolean withOrigPlaces) throws FileNotFoundException {
        return savePN2DotAndPDF(path, net, withLabel, withOrigPlaces, -1);
    }

    public static Thread savePN2DotAndPDF(String path, PetriNet net, boolean withLabel, boolean withOrigPlaces, Integer tokencount) throws FileNotFoundException {
        if (tokencount == -1) {
            savePN2Dot(path, net, withLabel, withOrigPlaces);
        } else {
            savePN2Dot(path, net, withLabel, withOrigPlaces, tokencount);
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

    public static Thread savePN2PDF(String path, PetriNet net, boolean withLabel, boolean withOrigPlaces) throws FileNotFoundException {
        return PNTools.savePN2PDF(path, net, withLabel, withOrigPlaces, -1);
    }

    public static Thread savePN2PDF(String path, PetriNet net, boolean withLabel, boolean withOrigPlaces, Integer tokencount) throws FileNotFoundException {
        String bufferpath = path + "_" + System.currentTimeMillis();
        Thread dot;
        if (tokencount == -1) {
            dot = savePN2DotAndPDF(bufferpath, net, withLabel, withOrigPlaces);
        } else {
            dot = savePN2DotAndPDF(bufferpath, net, withLabel, withOrigPlaces, tokencount);
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
     * It is not enough to only get rid of the < and > symbols. At least other
     * tools often have very restrictive grammars for the ids of the nodes.
     *
     * @param pnml
     * @return
     * @deprecated
     */
    @Deprecated
    private static String replaceSpecialSymbolsForPNML(String pnml) {
        // replace place ids which contain '<' or '>'
        String[] matches = Pattern.compile("id=\"([^\"]*)\"")
                .matcher(pnml)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);
        for (String match : matches) {
            if (match.contains("<") || match.contains(">")) {
                String oldID = match.substring(3, match.length());
                String id = oldID.replaceAll("[<]", "[");
                id = id.replaceAll("[>]", "]");
                // replace the id with " " to unique ids
                pnml = pnml.replaceAll(oldID, id);
                // replace all < with [ and > with ] in the text field
                pnml = pnml.replaceAll("<text>" + oldID.substring(1, oldID.length() - 1) + "</text>", "<text>" + id.substring(1, id.length() - 1) + "</text>");

            }
        }
        return pnml;
    }

    /**
     * Since many other tools have a very restrictive syntax for the ids of the
     * nodes we replace all nodes with ids and only write the real ID in the
     * text.
     *
     * @param net
     * @return
     * @throws RenderException
     */
    public static String pn2pnml(PetriNet net) throws RenderException {
        // create the net with the ids
        PetriNet idNet = createPetriNetWithIDsInLabel(net);
        String pnml = new PnmlPNRenderer().render(idNet);
        // replace the text field with the real id
        for (Place place : idNet.getPlaces()) {
            String label = PetriNetExtensionHandler.getLabel(place).replaceAll("<", "[");
            label = label.replaceAll(">", "]");
            pnml = pnml.replaceAll("<text>" + place.getId() + "</text>", "<text>" + label + "</text>");
        }
        for (Transition transition : idNet.getTransitions()) {
            String label = transition.getLabel().replaceAll("<", "[");
            label = label.replaceAll(">", "]");
            pnml = pnml.replaceAll("<text>" + transition.getLabel() + "</text>", "<text>" + label + "</text>");
        }
        // add the inhibitor annotation to the arcs
        for (Flow edge : idNet.getEdges()) {
            String prefix = "<arc id=\"" + edge.getSource().getId() + "-" + edge.getTarget().getId() + "\" source=\"" + edge.getSource().getId() + "\" target=\"" + edge.getTarget().getId() + "\"";
            String oldArc = prefix + ">";
            String newArc = prefix + " type=\"";
            if (PetriNetExtensionHandler.isInhibitor(edge)) {
                newArc += "inhibitor\">";
            } else {
                newArc += "normal\">";
            }
            pnml = pnml.replaceAll(oldArc, newArc);
        }
        // add the coordinates iff existent
        for (Node node : idNet.getNodes()) {
            if (PetriNetExtensionHandler.hasXCoord(node) && PetriNetExtensionHandler.hasYCoord(node)) {
                String prefix = (idNet.containsPlace(node.getId())) ? "<place id=\"" : "<transition id=\"";
                String startPoint = prefix + node.getId() + "\">\n";
                String addPart = "<graphics>\n"
                        + "  <position x=\"" + PetriNetExtensionHandler.getXCoord(node) + "\" y=\"" + PetriNetExtensionHandler.getYCoord(node) + "\"/>\n"
                        + "</graphics>";
                pnml = pnml.replaceAll(startPoint, startPoint + addPart);
            }
        }
        return pnml;
    }

    public static void save2pnml(String path, PetriNet net) throws FileNotFoundException, RenderException {
        String pnml = pn2pnml(net);
        Tools.saveFile(path, pnml);
    }
}
