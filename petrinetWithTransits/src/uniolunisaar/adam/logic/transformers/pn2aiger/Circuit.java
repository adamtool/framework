package uniolunisaar.adam.logic.transformers.pn2aiger;

import uniol.apt.adt.pn.PetriNet;

/**
 *
 * @author Manuel Gieseking
 */
public class Circuit {

    public enum Renderer {
        INGOING,
        INGOING_REGISTER,
        OUTGOING,
        OUTGOING_REGISTER,
        OUTGOING_REGISTER_BIN_TRANS,
        OUTGOING_REGISTER_MAX_INTERLEAVING,
        OUTGOING_REGISTER_BIN_TRANS_MAX_INTERLEAVING
    }

    public static AigerRenderer getRenderer(Renderer renderer, PetriNet net) {
        switch (renderer) {
            case INGOING:
                return new AigerRendererSafeIn(net);
            case INGOING_REGISTER:
                throw new RuntimeException("Not yet implemented.");
            case OUTGOING:
                return new AigerRendererSafeOut(net);
            case OUTGOING_REGISTER:
                return new AigerRendererSafeOutStutterRegister(net, false);
            case OUTGOING_REGISTER_BIN_TRANS:
                return new AigerRendererSafeOutStutterRegisterLogTrans(net, false);
            case OUTGOING_REGISTER_MAX_INTERLEAVING:
                return new AigerRendererSafeOutStutterRegister(net, true);
            case OUTGOING_REGISTER_BIN_TRANS_MAX_INTERLEAVING:
                return new AigerRendererSafeOutStutterRegisterLogTrans(net, true);
        }
        throw new RuntimeException("The case " + renderer + " is not yet implemented.");
    }
}
