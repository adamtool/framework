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
import uniolunisaar.adam.util.SDNTools;
import static uniolunisaar.adam.util.SDNTools.infixActPlace;
import static uniolunisaar.adam.util.SDNTools.infixTransitionLabel;

/**
 *
 * @author Manuel Gieseking
 */
public class SDNTopologyListenerOptimized extends SDNTopologyFormatBaseListener {

    private boolean inGenOptions = false;
    private Place sw = null;
    private Set<Place> curSet;
    private final Map<String, Map<String, String>> con = new HashMap<>();
    private Map<String, String> opts = null;

    private final PetriNetWithTransits pnwt;
    private boolean ingressSet = false;
    private boolean egressSet = false;

    public SDNTopologyListenerOptimized(PetriNetWithTransits net) {
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
        } else if (opts != null) {
            opts.put(key, value);
        }
    }

    @Override
    public void enterSwitchT(SDNTopologyFormatParser.SwitchTContext ctx) {
        sw = pnwt.createPlace(ctx.sw().getText());
        sw.setInitialToken(1);
        sw.putExtension(SDNTools.switchExtension, true);
    }

    @Override
    public void exitSwitchT(SDNTopologyFormatParser.SwitchTContext ctx) {
        sw = null;
    }

    @Override
    public void enterCon(SDNTopologyFormatParser.ConContext ctx) {
        opts = new HashMap<>();
    }

    @Override
    public void exitCon(SDNTopologyFormatParser.ConContext ctx) {
        Place pre = pnwt.getPlace(ctx.sw1.getText());
        Place post = pnwt.getPlace(ctx.sw2.getText());
        String id = pre.getId() + infixTransitionLabel + post.getId();
        con.put(id, opts);
        opts = null;
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
            place.putExtension(SDNTools.ingressExtension, true);
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
            place.putExtension(SDNTools.egressExtension, true);
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
        // activate the transition (it's not problem to set every input place to 1, because the others are one anyhow)
        for (Place place : tran.getPreset()) {
            place.setInitialToken(1);
        }
    }

    public PetriNetWithTransits getPnwt() {
        return pnwt;
    }

}
