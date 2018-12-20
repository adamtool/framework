package uniolunisaar.adam.logic.parser.transits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.ds.petrinetwithtransits.Transit;
import uniolunisaar.adam.logic.parser.transits.antlr.TransitFormatBaseListener;
import uniolunisaar.adam.logic.parser.transits.antlr.TransitFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
public class TransitListener extends TransitFormatBaseListener {

    private final ParseTreeProperty<Set<String>> sets = new ParseTreeProperty<>();
    private Set<String> curSet;

    private final Transition t;
    private final PetriNetWithTransits net;
    private final List<Transit> tokenflows;

    public TransitListener(PetriNetWithTransits net, Transition t) {
        this.t = t;
        this.net = net;
        tokenflows = new ArrayList<>();
    }

    @Override
    public void enterSet(TransitFormatParser.SetContext ctx) {
        this.curSet = new HashSet<>();
        this.sets.put(ctx, this.curSet);
    }

    @Override
    public void exitSet(TransitFormatParser.SetContext ctx) {
        this.curSet = null;
    }

    @Override
    public void exitObj(TransitFormatParser.ObjContext ctx) {
        if (curSet != null) {
            this.curSet.add(ctx.id.getText());
        }
    }

    @Override
    public void exitFlow(TransitFormatParser.FlowContext ctx) {
        Set<String> postSet = this.sets.get(ctx.postset);
        Place[] postset = new Place[postSet.size()];
        int i = 0;
        for (String id : postSet) {
            postset[i] = net.getPlace(id);
            ++i;
        }
        if (ctx.preset.GR() != null) {
            tokenflows.add(net.createInitialTokenFlow(t, postset));
        } else if (ctx.preset.obj() != null) {
            tokenflows.add(net.createTokenFlow(net.getPlace(ctx.preset.obj().getText()), t, postset));
        }
    }

    public List<Transit> getTokenflows() {
        return tokenflows;
    }

}
