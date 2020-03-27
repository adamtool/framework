package uniolunisaar.adam.exceptions.pnwt;

import uniol.apt.adt.pn.Place;

/**
 *
 * @author Manuel Gieseking
 */
public class NotInitialPlaceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotInitialPlaceException(Place place) {
        super("The place '" + place.toString() + "' is not initially marked and thus cannot "
                + "be marked as initial for a token flow.");
    }

    public NotInitialPlaceException(Place place, Exception e) {
        super("The place '" + place.toString() + "' is not initially marked and thus cannot "
                + "be marked as initial for a token flow.", e);
    }

}
