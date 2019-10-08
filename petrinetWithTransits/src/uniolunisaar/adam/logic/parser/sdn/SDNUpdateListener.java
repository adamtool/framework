package uniolunisaar.adam.logic.parser.sdn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.ParserRuleContext;
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
//
//    private List<Update> curSeqUpdate = null;
//    private Set<Update> curConUpdate = null;

    Collection<Update> currentList = null;
    final Map<ParserRuleContext, Collection<Update>> updates = new HashMap<>();

    final PetriNetWithTransits pnwt;
    Update update = null;

    public SDNUpdateListener(PetriNetWithTransits net) {
        this.pnwt = net;
    }

    @Override
    public void enterParUpdate(SDNUpdateFormatParser.ParUpdateContext ctx) {
//        curConUpdate = new HashSet<>();
//        updates.put(ctx, curConUpdate);
        currentList = new HashSet<>();
        updates.put(ctx, currentList);
    }

    @Override
    public void enterSeqUpdate(SDNUpdateFormatParser.SeqUpdateContext ctx) {
//        curSeqUpdate = new ArrayList<>();
//        updates.put(ctx, curSeqUpdate);
        currentList = new ArrayList<>();
        updates.put(ctx, currentList);
    }

    @Override
    public void exitParUpdate(SDNUpdateFormatParser.ParUpdateContext ctx) {
        if (!ctx.getParent().getParent().isEmpty()) {
//            updates.get(ctx.getParent().getParent()).add(new ConcurrentUpdate(curConUpdate));
            updates.get(ctx.getParent().getParent()).add(new ConcurrentUpdate((Set<Update>) currentList));
        } else {
            update = new ConcurrentUpdate((Set<Update>) updates.get(ctx));
        }
//        curConUpdate = null;
    }

    @Override
    public void exitSeqUpdate(SDNUpdateFormatParser.SeqUpdateContext ctx) {
        if (!ctx.getParent().getParent().isEmpty()) {
//            updates.get(ctx.getParent().getParent()).add(new SequentialUpdate(curSeqUpdate));
            updates.get(ctx.getParent().getParent()).add(new SequentialUpdate((List<Update>) currentList));
        } else {
            update = new SequentialUpdate((List<Update>) updates.get(ctx));
        }
//        curSeqUpdate = null;
    }

    @Override
    public void exitSwUpdate(SDNUpdateFormatParser.SwUpdateContext ctx) {
        Place from = pnwt.getPlace(ctx.sw1.getText());
        Place to = null;
        if (!ctx.sw2.getText().equals("-")) {
            to = pnwt.getPlace(ctx.sw2.getText());
        }
        Place old = null;
        if (ctx.old != null) {
            old = pnwt.getPlace(ctx.old.getText());
        }
        if (to == null && old == null) {
            throw new RuntimeException("You have to give a switch connection to delete.");
        }
        // check if a connection exists
        if (to != null) {
            boolean conEx = false;
            Set<Transition> pre = to.getPreset();
            for (Transition t : from.getPostset()) {
                if (pre.contains(t)) {
                    conEx = true;
                }
            }
            if (!conEx) {
                // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
                throw new RuntimeException("You added an update '" + from.getId() + ".fwd(" + to.getId() + ")' of unconnected switches.");
            }
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
            up = new SwitchUpdate(from.getId(), (to == null) ? null : to.getId());
        } else {
            up = new SwitchUpdate(from.getId(), old.getId(), (to == null) ? null : to.getId());
        }
//        if (curSeqUpdate != null) {
//            curSeqUpdate.add(up);
//        } else if (curConUpdate != null) {
//            curConUpdate.add(up);
        if (currentList != null) {
            currentList.add(up);
        } else {
            update = up;
        }
    }

    public Update getUpdate() {
        return update;
    }

}
