package uniolunisaar.adam.generators.pnwt.util.sdnencoding;

import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 * 
 * @author Jesko Hecking-Harbusch
 *
 */

public interface Update {
	
	public Place addUpdate(PetriNetWithTransits pn, Place start);

}
