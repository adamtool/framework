package uniolunisaar.adam.logic.transformers.pnwt;

import java.util.Map;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.ds.petrinetwithtransits.Transit;
import uniolunisaar.adam.logic.transformers.petrinet.PetriNet2DotRenderer;
import uniolunisaar.adam.tools.Tools;
import static uniolunisaar.adam.util.PNWTTools.getTransitRelationFromTransitions;

/**
 *
 * @author Manuel Gieseking
 * @param <G>
 */
public class PNWT2DotRenderer<G extends PetriNetWithTransits> extends PetriNet2DotRenderer<G> {

    @Override
    public String render(G net, boolean withLabel, boolean withOrigPlaces) {
        return render("PetriNetWithTransits", net, withLabel, withOrigPlaces);
    }

    @Override
    protected String getPlacesAdditionalStyles(G net, boolean withOrigPlaces, Place place) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getPlacesAdditionalStyles(net, withOrigPlaces, place));

        // add possible coloring and the initial transits
        if (nb_partitions != null && net.hasPartition(place)) {
            int t = net.getPartition(place);
            sb.append(", style=\"filled");
            if (net.isInitialTransit(place)) {
                sb.append(", dashed");
            }
            sb.append("\", fillcolor=");
            sb.append("\"");
            float val = ((t + 1) * 1.f) / (nb_partitions * 1.f);
            sb.append(val).append(" ").append(val).append(" ").append(val);
            sb.append("\"");

        } else if (net.isInitialTransit(place)) {
            sb.append(", style=dashed");
        }
        return sb.toString();
    }

    @Override
    protected String getFlowsAdditionalStyles(G net, Flow f) {
        StringBuilder sb = new StringBuilder();
        String superStyles = super.getFlowsAdditionalStyles(net, f);
        // replace the label with the additional transit identifiers        
        Map<Flow, String> map = getTransitRelationFromTransitions(net);
        Integer w = f.getWeight();
        String weight = "\"" + ((w != 1) ? w.toString() + " : " : "");
        if (map.containsKey(f)) {
            weight += map.get(f);
        }
        weight += "\"";
        String newLabel = superStyles.replaceAll("label=\".*\"", "label=" + weight);
        sb.append(newLabel);

        // do the coloring
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
        return sb.toString();
    }

}
