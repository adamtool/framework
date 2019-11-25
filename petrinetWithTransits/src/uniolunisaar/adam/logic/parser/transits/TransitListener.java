package uniolunisaar.adam.logic.parser.transits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import uniol.apt.adt.exception.NoSuchNodeException;
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

    private final ParseTreeProperty<Set<Place>> sets = new ParseTreeProperty<>();
    private Set<Place> curSet;

    private final Transition t;
    private final PetriNetWithTransits net;
    private final List<Transit> tokenflows;

    private final TransitFormatParser parser;

    public TransitListener(PetriNetWithTransits net, Transition t, TransitFormatParser parser) {
        this.t = t;
        this.net = net;
        this.parser = parser;
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
            try {
                this.curSet.add(net.getPlace(ctx.id.getText()));
            } catch (NoSuchNodeException e) {
                parser.notifyErrorListeners(ctx.start, e.getMessage(), ctx.exception);
            }
        }
    }

    @Override
    public void exitFlow(TransitFormatParser.FlowContext ctx) {
        Set<Place> postSet = this.sets.get(ctx.postset);
        Place[] postset = new Place[postSet.size()];
        postset = postSet.toArray(postset);
        if (ctx.preset.GR() != null) {
            tokenflows.add(net.createInitialTransit(t, postset));
        } else if (ctx.preset.obj() != null) {
            tokenflows.add(net.createTransit(net.getPlace(ctx.preset.obj().getText()), t, postset));
        }

    }

    public List<Transit> getTokenflows() {
        return tokenflows;
    }

}
