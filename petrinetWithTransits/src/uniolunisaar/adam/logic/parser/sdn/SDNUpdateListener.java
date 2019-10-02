package uniolunisaar.adam.logic.parser.sdn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.ConcurrentUpdate;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.SequentialUpdate;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.SwitchUpdate;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.Update;
import uniolunisaar.adam.logic.parser.sdn.antlr.SDNUpdateFormatBaseListener;
import uniolunisaar.adam.logic.parser.sdn.antlr.SDNUpdateFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
public class SDNUpdateListener extends SDNUpdateFormatBaseListener {

    private List<Update> curSeqUpdate = null;
    private Set<Update> curConUpdate = null;

    private final PetriNetWithTransits pnwt;
    private Update update = null;

    public SDNUpdateListener(PetriNetWithTransits net) {
        this.pnwt = net;
    }

    @Override
    public void enterParUpdate(SDNUpdateFormatParser.ParUpdateContext ctx) {
        curConUpdate = new HashSet<>();
    }

    @Override
    public void enterSeqUpdate(SDNUpdateFormatParser.SeqUpdateContext ctx) {
        curSeqUpdate = new ArrayList<>();
    }

    @Override
    public void exitParUpdate(SDNUpdateFormatParser.ParUpdateContext ctx) {
        update = new ConcurrentUpdate(curConUpdate);
        curConUpdate = null;
    }

    @Override
    public void exitSeqUpdate(SDNUpdateFormatParser.SeqUpdateContext ctx) {
        update = new SequentialUpdate(curSeqUpdate);
        curSeqUpdate = null;
    }

    @Override
    public void exitSwUpdate(SDNUpdateFormatParser.SwUpdateContext ctx) {
        Place from = pnwt.getPlace(ctx.sw1.getText());
        Place to = pnwt.getPlace(ctx.sw2.getText());
        SwitchUpdate up = null;
        for (Transition t : from.getPostset()) {            // find the activated transition
            for (Place place : t.getPreset()) {
                if (place.getId().contains(SDNTopologyListener.infixActPlace) && pnwt.getInitialMarking().getToken(place).getValue() > 0) {
                    up = new SwitchUpdate(from.getId(), place.getId(), to.getId());
                    break;
                }
            }
        }
        if (curSeqUpdate != null) {
            curSeqUpdate.add(up);
        } else if (curConUpdate != null) {
            curConUpdate.add(up);
        } else {
            update = up;
        }
    }

    public Update getUpdate() {
        return update;
    }

}
