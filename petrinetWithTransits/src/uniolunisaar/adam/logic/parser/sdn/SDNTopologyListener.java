package uniolunisaar.adam.logic.parser.sdn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.logic.parser.sdn.antlr.SDNTopologyFormatBaseListener;
import uniolunisaar.adam.logic.parser.sdn.antlr.SDNTopologyFormatParser;
import uniolunisaar.adam.tools.Logger;

/**
 *
 * @author Manuel Gieseking
 */
public class SDNTopologyListener extends SDNTopologyFormatBaseListener {

    static final String infixActPlace = "_fwd_";
    static final String infixTransitionLabel = "->";

    private boolean inGenOptions = false;
    private Place sw = null;
    private Set<Place> curSet;
    private final Map<String, Transition> con = new HashMap<>();
    private Transition t = null;

    private final PetriNetWithTransits pnwt;
    private boolean ingressSet = false;
    private boolean egressSet = false;

    public SDNTopologyListener(PetriNetWithTransits net) {
        this.pnwt = net;
    }

    @Override
    public void exitName(SDNTopologyFormatParser.NameContext ctx) {
        String name = ctx.STR().getText();
        pnwt.setName(name.substring(1, name.length() - 1));
    }

    @Override
    public void exitDescription(SDNTopologyFormatParser.DescriptionContext ctx) {
        String desc = (ctx.STR() == null) ? ctx.STR_MULTI().getText() : ctx.STR().getText();
        pnwt.putExtension("description", desc.substring(1, desc.length() - 1));
    }

    @Override
    public void enterGenOptions(SDNTopologyFormatParser.GenOptionsContext ctx) {
        inGenOptions = true;
    }

    @Override
    public void exitGenOptions(SDNTopologyFormatParser.GenOptionsContext ctx) {
        inGenOptions = false;
    }

    @Override
    public void exitOption(SDNTopologyFormatParser.OptionContext ctx) {
        String key = ctx.ID().getText();
        String value = ctx.STR().getText();
        value = value.substring(1, value.length() - 1);
//        if(ctx.getParent().getRuleContext() instanceof SDNTopologyFormatParser.GenOptionsContext) { // does the same but uses instance of...
        if (inGenOptions) {
            pnwt.putExtension(key, value);
        } else if (sw != null) {
            sw.putExtension(key, value);
        } else if (t != null) {
            t.putExtension(key, value);
        }
    }

    @Override
    public void enterSwitchT(SDNTopologyFormatParser.SwitchTContext ctx) {
        sw = pnwt.createPlace(ctx.sw().getText());
        sw.setInitialToken(1);
    }

    @Override
    public void exitSwitchT(SDNTopologyFormatParser.SwitchTContext ctx) {
        sw = null;
    }

    @Override
    public void enterCon(SDNTopologyFormatParser.ConContext ctx) {
        t = pnwt.createTransition();
    }

    @Override
    public void exitCon(SDNTopologyFormatParser.ConContext ctx) {
        Place pre = pnwt.getPlace(ctx.sw1.getText());
        Place post = pnwt.getPlace(ctx.sw2.getText());
        addConnection(pre, t, post);
        Transition trans = pnwt.createTransition();
        trans.copyExtensions(t);
        addConnection(post, trans, pre);
        t = null;
    }

    private void addConnection(Place pre, Transition trans, Place post) {
        String id = pre.getId() + infixTransitionLabel + post.getId();
        trans.setLabel(id);
        // if we only use one transition for connecting switches, we could get a unique commutative id by lexicographical ordering
//        String id;
//        if (pre.getId().compareTo(post.getId()) > 0) {
//            id = pre.getId() + post.getId();
//        } else {
//            id = post.getId() + pre.getId();
//        }
        if (con.containsKey(id)) {
            Logger.getInstance().addWarning("You added a redundant connection " + pre.getId() + " <-> " + post.getId() + ". We ignore this instance.");
            return;
        }
        Place act = pnwt.createPlace(pre.getId() + infixActPlace + post.getId());
        pnwt.createFlow(pre, trans);
        pnwt.createFlow(post, trans);
        pnwt.createFlow(act, trans);
        pnwt.createFlow(trans, pre);
        pnwt.createFlow(trans, post);
        pnwt.createFlow(trans, act);
        con.put(id, trans);
    }

    @Override
    public void enterIngress(SDNTopologyFormatParser.IngressContext ctx) {
        if (ingressSet) {
            Logger.getInstance().addWarning("You defined two sets of ingress nodes. We use the union.");
        } else {
            ingressSet = true;
        }
        curSet = new HashSet<>();
    }

    @Override
    public void exitIngress(SDNTopologyFormatParser.IngressContext ctx) {
        for (Place place : curSet) {
            Transition t = pnwt.createTransition();
            pnwt.createFlow(place, t);
            pnwt.createFlow(t, place);
            pnwt.createTransit(place, t, place);
            pnwt.createInitialTransit(t, place);
        }
        curSet = null;
    }

    @Override
    public void enterEgress(SDNTopologyFormatParser.EgressContext ctx) {
        if (egressSet) {
            Logger.getInstance().addWarning("You defined two sets of egress nodes. We use the union.");
        } else {
            egressSet = true;
        }
        curSet = new HashSet<>();
    }

    @Override
    public void exitEgress(SDNTopologyFormatParser.EgressContext ctx) {
        for (Place place : curSet) {
            place.putExtension("egress", true);
        }
        curSet = null;
    }

    @Override
    public void exitSw(SDNTopologyFormatParser.SwContext ctx) {
        if (curSet != null) { // in the ingress or egress set case
            curSet.add(pnwt.getPlace(ctx.getText()));
        }
    }

    @Override
    public void exitForward(SDNTopologyFormatParser.ForwardContext ctx) {
        Place from = pnwt.getPlace(ctx.src.getText());
        Place to = pnwt.getPlace(ctx.dest.getText());
        String id = from.getId() + infixTransitionLabel + to.getId();
//        String id;
//        if (from.getId().compareTo(to.getId()) > 0) {
//            id = from.getId() + to.getId();
//        } else {
//            id = to.getId() + from.getId();
//        }
        Transition tran = con.get(id);
        if (tran == null) {
            // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
            throw new RuntimeException("You added a forward rule '" + from.getId() + ".fwd(" + to.getId() + ")' of unconnected switches.");
        }
        pnwt.createTransit(from, tran, to);
        pnwt.createTransit(to, tran, to);
        // activate the transition (it's not problem to set every input place to 1, because the others are one anyhow)
        for (Place place : tran.getPreset()) {
            place.setInitialToken(1);
        }
    }

    public PetriNetWithTransits getPnwt() {
        return pnwt;
    }

}
