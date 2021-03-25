package uniolunisaar.adam.logic.transformers.petrinet;

import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;

/**
 *
 * @author Manuel Gieseking
 * @param <G> the type of graph to render
 */
public class PetriNet2DotRenderer<G extends PetriNet> {

    protected final String placeShape = "circle";
    protected final String specialPlaceShape = "doublecircle";

    protected Integer nb_partitions = null;

    public String render(G net, boolean withLabel, boolean withOrigPlaces) {
        return render("PetriNet", net, withLabel, withOrigPlaces);
    }

    public String render(G net, boolean withLabel, boolean withOrigPlaces, Integer nb_partitions) {
        this.nb_partitions = nb_partitions;
        return render(net, withLabel, withOrigPlaces);
    }

    protected String render(String type, G net, boolean withLabel, boolean withOrigPlaces) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHeader(type));
        sb.append(getTransitions(net, withLabel));
        sb.append(getPlaces(net, withOrigPlaces));
        sb.append(getFlows(net));
        sb.append(getFooter(net));
        return sb.toString();
    }

    // %%%%%%%%%%%%%%%%% HEADER
    protected String getHeader(String name) {
        return "digraph " + name + " {\n";
    }

    // %%%%%%%%%%%%%%%% TRANSITIONS
    protected String getTransitions(G net, boolean withLabel) {
        StringBuilder sb = new StringBuilder();
        sb.append("#transitions\n");
        sb.append("node [shape=box, height=0.5, width=0.5, fixedsize=true];\n");
        for (Transition t : net.getTransitions()) {
            sb.append("\"").append(t.getId()).append("\"").append("[");
            sb.append(getTransitionsAdditionalStyles(net, withLabel, t));
            sb.append("];\n");
        }
        sb.append("\n\n");
        return sb.toString();
    }

    protected String getTransitionsAdditionalStyles(G net, boolean withLabel, Transition t) {
        StringBuilder sb = new StringBuilder();
        String c = null;
        if (PetriNetExtensionHandler.isStrongFair(t)) {
            c = "blue";
        }
        if (PetriNetExtensionHandler.isWeakFair(t)) {
            c = "lightblue";
        }
        String color = (c != null) ? "style=filled, fillcolor=" + c : "";
        sb.append(color);
        if (withLabel) {
            if (PetriNetExtensionHandler.isStrongFair(t) || PetriNetExtensionHandler.isWeakFair(t)) {
                sb.append(", ");
            }
            sb.append("xlabel=\"").append(t.getLabel()).append("\"");
        }
        return sb.toString();
    }

    // %%%%%%%%%%%%%%%%%%%%% PLACES
    protected String getPlaces(G net, boolean withOrigPlaces) {
        StringBuilder sb = new StringBuilder();
        sb.append("#places\n");
        for (Place place : net.getPlaces()) {
            sb.append("\"").append(place.getId()).append("\"[");
            sb.append(getPlacesAdditionalStyles(net, withOrigPlaces, place));
            sb.append("];\n");
        }
        return sb.toString();
    }

    protected String getPlacesAdditionalStyles(G net, boolean withOrigPlaces, Place place) {
        StringBuilder sb = new StringBuilder();
        // special?
        String shape = (PetriNetExtensionHandler.isBad(place)
                || PetriNetExtensionHandler.isReach(place)
                || PetriNetExtensionHandler.isBuchi(place)) ? specialPlaceShape : placeShape;
        // Initialtoken number
        Long token = place.getInitialToken().getValue();
        String tokenString = (token > 0) ? token.toString() : "";
        // Drawing
        String id = place.getId();
        if (withOrigPlaces && PetriNetExtensionHandler.hasOrigID(place)) {
            id += "(" + PetriNetExtensionHandler.getOrigID(place) + ")";
        }
        sb.append("shape=").append(shape);
        sb.append(", height=0.5, width=0.5, fixedsize=true");
        sb.append(", xlabel=").append("\"").append(id).append("\"");
        sb.append(", label=").append("\"").append(tokenString).append("\"");
        return sb.toString();
    }

    // %%%%%%%%%%%%%%%%%%%%%%%% FLOWS
    protected String getFlows(G net) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n#flows\n");
        for (Flow f : net.getEdges()) {
            sb.append("\"").append(f.getSource().getId()).append("\"").append("->").append("\"").append(f.getTarget().getId()).append("\"[");
            sb.append(getFlowsAdditionalStyles(net, f));
            sb.append("]\n");
        }
        return sb.toString();
    }

    protected String getFlowsAdditionalStyles(G net, Flow f) {
        StringBuilder sb = new StringBuilder();
        Integer w = f.getWeight();
        String weight = "\"" + ((w != 1) ? w.toString() : "");
        weight += "\"";
        sb.append("label=").append(weight);
        if (PetriNetExtensionHandler.isInhibitor(f)) {
            sb.append(", dir=\"both\", arrowtail=\"odot\"");
        }
        return sb.toString();
    }

    // %%%%%%%%%%%%%%%%%%%% FOOTER
    protected String getFooter(G net) {
        StringBuilder sb = new StringBuilder();
        sb.append("overlap=false\n");
        sb.append("label=\"").append(net.getName()).append("\"\n");
        sb.append("fontsize=12\n");
        sb.append("}");
        return sb.toString();
    }
}
