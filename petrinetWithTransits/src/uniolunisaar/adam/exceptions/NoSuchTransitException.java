package uniolunisaar.adam.exceptions;

import uniol.apt.adt.exception.DatastructureException;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 * @author Manuel Gieseking
 */
public class NoSuchTransitException extends DatastructureException {

    public static final long serialVersionUID = 0xdeadbeef00000003l;

    public NoSuchTransitException(PetriNetWithTransits net, String sourceId, String targetId) {
        super("Transit '" + sourceId + " --> " + targetId + "' does not exist in graph '" + net.getName() + "'");
    }

    public NoSuchTransitException(PetriNetWithTransits net, String sourceId, String transitionId, String targetId) {
        super("Transit '" + sourceId + " [" +transitionId+"> " + targetId + "' does not exist in graph '" + net.getName() + "'");
    }

}
