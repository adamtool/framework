package uniolunisaar.adam.logic.transformers.petrinet.rgpropchecker;

import uniol.apt.adt.pn.Marking;
import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.util.IAdamExtensions;

/**
 *
 * @author Manuel Gieseking
 */
public interface ICoverabilityGraphPropertyUser {

    public IAdamExtensions getKey();

    /**
     * Does the calculation of the property. If this calculation does not need
     * to use any more markings return true so save running time. If so, the
     * property should be saved with the ExtensionManagement under the getKey().
     *
     * @param <G> - the type of the net.
     * @param net - the net on which graph the property is checked.
     * @param m - the marking to check
     * @return true iff the calculator has finished.
     */
    public <G extends PetriNet> boolean calculateProperty(G net, Marking m);

    /**
     * This method is called by the CoverabilityGraphPropertiesManagement, when
     * the property wasn't already calculated (the ExtensionManagement couldn't
     * find the key getKey()), it traversed all nodes of the coverability graph,
     * and the calculateProperty method never returned true. Normally, here this
     * method is used to store the result under getKey().
     *
     * @param <G>
     * @param net
     * @return
     */
    public <G extends PetriNet> boolean checkedAllNodes(G net);

}
