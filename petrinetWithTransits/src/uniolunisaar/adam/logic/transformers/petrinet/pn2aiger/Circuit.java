package uniolunisaar.adam.logic.transformers.petrinet.pn2aiger;

import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.ds.circuits.CircuitRendererSettings;
import uniolunisaar.adam.logic.transformers.petrinet.pn2aiger.mcc.AigerRendererSafeStutterRegisterFireability;
import uniolunisaar.adam.logic.transformers.petrinet.pn2aiger.mcc.AigerRendererSafeStutterRegisterLogTransFireability;
import uniolunisaar.adam.logic.transformers.petrinet.pn2aiger.mcc.AigerRendererSafeStutterRegisterLogTransOnlyPlaces;
import uniolunisaar.adam.logic.transformers.petrinet.pn2aiger.mcc.AigerRendererSafeStutterRegisterOnlyPlaces;

/**
 *
 * @author Manuel Gieseking
 */
public class Circuit {

    public static AigerRenderer getRenderer(PetriNet net, CircuitRendererSettings settings) {
        switch (settings.getEncoding()) {
            case EXPLICIT:
                switch (settings.getAtoms()) {
                    case PLACES_AND_TRANSITIONS:
                        return new AigerRendererSafeStutterRegister(net, settings.isMaxInterleaving(), settings.getSemantics());
                    case PLACES:
                        return new AigerRendererSafeStutterRegisterOnlyPlaces(net, settings.isMaxInterleaving(), settings.getSemantics());
                    case FIREABILITY:
                        return new AigerRendererSafeStutterRegisterFireability(net, settings.isMaxInterleaving(), settings.getSemantics());
                    default:
                        throw new RuntimeException("The case " + settings.getAtoms().name() + " is not yet implemented.");
                }
            case LOGARITHMIC:
                switch (settings.getAtoms()) {
                    case PLACES_AND_TRANSITIONS:
                        return new AigerRendererSafeStutterRegisterLogTrans(net, settings.isMaxInterleaving(), settings.getSemantics());
                    case PLACES:
                        return new AigerRendererSafeStutterRegisterLogTransOnlyPlaces(net, settings.isMaxInterleaving(), settings.getSemantics());
                    case FIREABILITY:
                        return new AigerRendererSafeStutterRegisterLogTransFireability(net, settings.isMaxInterleaving(), settings.getSemantics());
                    default:
                        throw new RuntimeException("The case " + settings.getAtoms().name() + " is not yet implemented.");
                }
            default:
                throw new RuntimeException("The case " + settings.getEncoding().name() + " is not yet implemented.");
        }
    }
}
