package uniolunisaar.adam.logic.transformers.pn2aiger;

import uniol.apt.adt.pn.PetriNet;

/**
 *
 * @author Manuel Gieseking
 */
public class AigerRendererSafeIn extends AigerRenderer {

    public AigerRendererSafeIn(PetriNet net) {
        super(net);
    }

    // todo: problem with enabledness since in the ingoing the transition has
    // fired thus, do I check the enabledness for the next transition?
    public String renderToString() {
        return super.render().toString();
    }

}
