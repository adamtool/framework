package uniolunisaar.adam.logic.parser.sdn;

import java.util.Set;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.generators.pnwt.util.sdnencoding.SwitchUpdate;
import uniolunisaar.adam.logic.parser.sdn.antlr.SDNUpdateFormatParser;
import uniolunisaar.adam.util.SDNTools;
import static uniolunisaar.adam.util.SDNTools.infixActPlace;
import static uniolunisaar.adam.util.SDNTools.infixTransitionLabel;

/**
 *
 * @author Manuel Gieseking
 */
public class SDNUpdateListenerOptimized extends SDNUpdateListener {

    public SDNUpdateListenerOptimized(PetriNetWithTransits net) {
        super(net);
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
        if (to != null) {
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
        }

        // check if old connection existed
        if (old != null) {
            boolean conEx = false;
            Set<Transition> pre = old.getPreset();
            for (Transition t : from.getPostset()) {
                if (pre.contains(t)) {
                    conEx = true;
                }
            }
            if (!conEx) {
                // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
                throw new RuntimeException("You wanted to update the connection '" + from.getId() + ".fwd(" + old.getId() + ")'. But this connection hadn't existed.");
            }
        }
        SwitchUpdate up;
        if (old == null) {
            up = new SwitchUpdate(from.getId(), (to == null) ? null : to.getId());
        } else {
            up = new SwitchUpdate(from.getId(), old.getId(), (to == null) ? null : to.getId());
        }
        if (currentList != null) {
            currentList.add(up);
        } else {
            update = up;
        }
    }

}
