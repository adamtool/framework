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
import uniolunisaar.adam.util.SDNTools;
import static uniolunisaar.adam.util.SDNTools.infixActPlace;
import static uniolunisaar.adam.util.SDNTools.infixTransitionLabel;

/**
 *
 * @author Manuel Gieseking
 */
public class SDNUpdateListenerOptimized extends SDNUpdateFormatBaseListener {

    private List<Update> curSeqUpdate = null;
    private Set<Update> curConUpdate = null;

    private final PetriNetWithTransits pnwt;
    private Update update = null;

    public SDNUpdateListenerOptimized(PetriNetWithTransits net) {
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
        Place old = null;
        if (ctx.old != null) {
            old = pnwt.getPlace(ctx.old.getText());
        }
        // check if a connection exists
        boolean conEx = false;
        Set<Transition> pre = to.getPreset();
        for (Transition t : from.getPostset()) {
            if (pre.contains(t)) {
                conEx = true;
            }
        }
        if (!conEx) { // add this connection
            String id = from.getId() + infixTransitionLabel + to.getId();
            Transition tran = pnwt.createTransition();
            tran.setLabel(id);
            tran.putExtension(SDNTools.fwdExtension, true);
            Place act = pnwt.createPlace(from.getId() + infixActPlace + to.getId());

            pnwt.createFlow(from, tran);
            pnwt.createFlow(to, tran);
            pnwt.createFlow(act, tran);
            pnwt.createFlow(tran, from);
            pnwt.createFlow(tran, to);
            pnwt.createFlow(tran, act);

            pnwt.createTransit(from, tran, to);
            pnwt.createTransit(to, tran, to);
        }
//        SwitchUpdate up = null;
//        System.out.println("switch update " + from.getId() + " " + to.getId());
//        for (Transition t : from.getPostset()) {            // find the activated transition
//            for (Place place : t.getPreset()) {
//                System.out.println(place.getId());
//                if (place.getId().contains(infixActPlace) && pnwt.getInitialMarking().getToken(place).getValue() > 0) {
//                    System.out.println("drin");
//
//                    up = new SwitchUpdate(from.getId(), place.getId(), to.getId());
//                    break;
//                }
//            }
//        }
        SwitchUpdate up;
        if (old == null) {
            up = new SwitchUpdate(from.getId(), to.getId());
        } else {
            up = new SwitchUpdate(from.getId(), old.getId(), to.getId());
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
