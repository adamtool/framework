package uniolunisaar.adam.logic.transformers.petrinet;

import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.BoundingBox;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;
import uniolunisaar.adam.util.PNTools;

/**
 *
 * @author Manuel Gieseking
 * @param <G> the type of graph to render
 */
public class PetriNet2TikzRenderer<G extends PetriNet> {

    private final float SCALE = 0.1f;
    private final String PLACE_STYLE = "circle, thick, draw=black!75, fill=white, minimum size=6mm";
    private final String TRANSITION_STYLE = "rectangle, thick, draw=DarkBlue!75, fill=DarkBlue!20, minimum size=4mm";

    public String renderFromCoordinates(PetriNet net) {
        StringBuilder sb = new StringBuilder();
        sb.append(header());
        sb.append(nodes(net));
        sb.append(path(net));
        sb.append(footer());
        return sb.toString();
    }

    protected String header() {
        StringBuilder sb = new StringBuilder();
        // styles
        sb.append("\\tikzstyle{place}=[").append(PLACE_STYLE).append("]\n");
        sb.append("\\tikzstyle{transition}=[").append(TRANSITION_STYLE).append("]\n");
        sb.append("\\begin{tikzpicture}["
                + "node distance=12mm, "
                + ">=stealth', "
                + "bend angle=15, auto, scale=").append(SCALE).
                append("]\n");
        return sb.toString();
    }

    protected String nodes(PetriNet net) {
        BoundingBox bb = PNTools.calculateBoundingBox(net);
        StringBuilder sb = new StringBuilder();
        sb.append(places(net, bb));
        sb.append(transitions(net, bb));
        return sb.toString();
    }

    protected String places(PetriNet net, BoundingBox bb) {
        StringBuilder sb = new StringBuilder();
        for (Place place : net.getPlaces()) {
            double xcoord = norm(PetriNetExtensionHandler.getXCoord(place), bb.getLeft(), bb.getRight());
            double ycoord = -1 * norm(PetriNetExtensionHandler.getYCoord(place), bb.getTop(), bb.getBottom());
            // Initialtoken number
            Long token = place.getInitialToken().getValue();
            String tokenString = (token > 0) ? ", tokens=" + token.toString() : "";
            sb.append("\\node[place").
                    append(tokenString).
                    append("] at (").append(xcoord).append(", ").append(ycoord).append(")").
                    append(" (").append(place.getId()).
                    append(") [label=above:\\(\\mathit{").append(place.getId()).append("}").
                    append("\\)] {};\n");
        }
        sb.append("\n\n");
        return sb.toString();
    }

    protected String transitions(PetriNet net, BoundingBox bb) {
        StringBuilder sb = new StringBuilder();
        for (Transition t : net.getTransitions()) {
            double xcoord = norm(PetriNetExtensionHandler.getXCoord(t), bb.getLeft(), bb.getRight());
            double ycoord = -1 * norm(PetriNetExtensionHandler.getYCoord(t), bb.getTop(), bb.getBottom());
            sb.append("\\node [transition] at (").
                    append(xcoord).append(", ").append(ycoord).append(") (").append(t.getId()).append(")").
                    //                    append(" {\\(\\mathit{").append(t.getLabel()).append("}"). // inside the node
                    append(" [label=above:\\(\\mathit{").append(t.getId()).append("}"). // as label
                    append("\\)] {};\n");
        }
        sb.append("\n\n");
        return sb.toString();
    }

    protected String path(PetriNet net) {
        StringBuilder sb = new StringBuilder();
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
        return sb.toString();
    }

    protected String footer() {
        StringBuilder sb = new StringBuilder();
        sb.append("\\end{tikzpicture}");
        return sb.toString();
    }

    protected double norm(double val, double min, double max) {
        return 100 * (val - min) / (max - min);
    }
}
