package uniolunisaar.adam.logic.transformers.pn2aiger;

import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.logic.transformers.pn2aiger.mcc.AigerRendererSafeOutStutterRegisterFireability;
import uniolunisaar.adam.logic.transformers.pn2aiger.mcc.AigerRendererSafeOutStutterRegisterLogTransFireability;
import uniolunisaar.adam.logic.transformers.pn2aiger.mcc.AigerRendererSafeOutStutterRegisterLogTransOnlyPlaces;
import uniolunisaar.adam.logic.transformers.pn2aiger.mcc.AigerRendererSafeOutStutterRegisterOnlyPlaces;

/**
 *
 * @author Manuel Gieseking
 */
public class Circuit {

    public enum Renderer {
        INGOING,
        INGOING_REGISTER,
        OUTGOING,
        // %%%%%% places and transitions
        // explicit
        OUTGOING_REGISTER,
        OUTGOING_REGISTER_MAX_INTERLEAVING,
        // logarithmic
        OUTGOING_REGISTER_BIN_TRANS,
        OUTGOING_REGISTER_BIN_TRANS_MAX_INTERLEAVING,
        // %%%%%% onlyPlaces
        // explicit
        OUTGOING_REGISTER_ONLYPLACES,
        OUTGOING_REGISTER_ONLYPLACES_MAX_INTERLEAVING,
        // logarithmic
        OUTGOING_REGISTER_ONLYPLACES_BIN_TRANS,
        OUTGOING_REGISTER_ONLYPLACES_BIN_TRANS_MAX_INTERLEAVING,
        // %%%%%% fireability
        // explicit
        OUTGOING_REGISTER_FIREABILITY,
        OUTGOING_REGISTER_FIREABILITY_MAX_INTERLEAVING,
        // logarithmic
        OUTGOING_REGISTER_FIREABILITY_BIN_TRANS,
        OUTGOING_REGISTER_FIREABILITY_BIN_TRANS_MAX_INTERLEAVING
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
            case OUTGOING_REGISTER_MAX_INTERLEAVING:
                return new AigerRendererSafeOutStutterRegister(net, true);
            case OUTGOING_REGISTER_BIN_TRANS:
                return new AigerRendererSafeOutStutterRegisterLogTrans(net, false);
            case OUTGOING_REGISTER_BIN_TRANS_MAX_INTERLEAVING:
                return new AigerRendererSafeOutStutterRegisterLogTrans(net, true);
            case OUTGOING_REGISTER_ONLYPLACES:
                return new AigerRendererSafeOutStutterRegisterOnlyPlaces(net, false);
            case OUTGOING_REGISTER_ONLYPLACES_MAX_INTERLEAVING:
                return new AigerRendererSafeOutStutterRegisterOnlyPlaces(net, true);
            case OUTGOING_REGISTER_ONLYPLACES_BIN_TRANS:
                return new AigerRendererSafeOutStutterRegisterLogTransOnlyPlaces(net, false);
            case OUTGOING_REGISTER_ONLYPLACES_BIN_TRANS_MAX_INTERLEAVING:
                return new AigerRendererSafeOutStutterRegisterLogTransOnlyPlaces(net, true);
            case OUTGOING_REGISTER_FIREABILITY:
                return new AigerRendererSafeOutStutterRegisterFireability(net, false);
            case OUTGOING_REGISTER_FIREABILITY_MAX_INTERLEAVING:
                return new AigerRendererSafeOutStutterRegisterFireability(net, true);
            case OUTGOING_REGISTER_FIREABILITY_BIN_TRANS:
                return new AigerRendererSafeOutStutterRegisterLogTransFireability(net, false);
            case OUTGOING_REGISTER_FIREABILITY_BIN_TRANS_MAX_INTERLEAVING:
                return new AigerRendererSafeOutStutterRegisterLogTransFireability(net, true);
        }
        throw new RuntimeException("The case " + renderer + " is not yet implemented.");
    }
}
