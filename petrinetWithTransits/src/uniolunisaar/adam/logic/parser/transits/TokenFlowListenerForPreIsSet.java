package uniolunisaar.adam.logic.parser.transits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.ds.petrinetwithtransits.Transit;
import uniolunisaar.adam.logic.parser.transits.antlr.TransitFormatBaseListener;
import uniolunisaar.adam.logic.parser.transits.antlr.TransitFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
@Deprecated
public class TokenFlowListenerForPreIsSet extends TransitFormatBaseListener {

    private final ParseTreeProperty<Set<String>> sets = new ParseTreeProperty<>();
    private Set<String> curSet;
    private Transit curFlow;

    private final Transition t;
    private final PetriNetWithTransits net;
    private final List<Transit> tokenflows;

    public TokenFlowListenerForPreIsSet(PetriNetWithTransits net, Transition t) {
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
        assert this.curSet != null;

        this.curSet.add(ctx.id.getText());
    }

    @Override
    public void enterFlow(TransitFormatParser.FlowContext ctx) {
//        this.curFlow = new Transit(game, t);
        this.tokenflows.add(curFlow);
    }

    @Override
    public void exitFlow(TransitFormatParser.FlowContext ctx) {
        Set<String> preset = this.sets.get(ctx.preset);
        Set<String> postset = this.sets.get(ctx.postset);
        preset.stream().forEach((pre) -> {
//            this.curFlow.addPresetPlace(pre);
        });
        postset.stream().forEach((post) -> {
//            this.curFlow.addPostsetPlace(post);
        });
        net.checkTransitConsistency(this.curFlow);
    }

    public List<Transit> getTokenflows() {
        return tokenflows;
    }

}
