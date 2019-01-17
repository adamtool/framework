package uniolunisaar.adam.generators.pnwt;

import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 * 
 * @author Jesko Hecking-Harbusch
 *
 */

public class AccessControl {
	
	public static PetriNetWithTransits createAccessControlExample() { 
		PetriNetWithTransits net = new PetriNetWithTransits("AccessControl");
		Place outside = addRoom(net, "start", true);
		Place lobby = addRoom(net, "lobby", false);
		Place corridor = addRoom(net, "corridor", false);
		Place meetingRoom = addRoom(net, "meetingroom", false);
		Place bureau = addRoom(net, "bureau", false);
		addConnectionBothWays(net, outside, lobby, true, true);
		addConnectionBothWays(net, lobby, corridor, true, true);
		addConnectionBothWays(net, corridor, meetingRoom, true, true);
		addConnectionBothWays(net, corridor, bureau, true, true);
		addConnectionBothWays(net, outside, corridor, false, true);
		return net;
	}
	
	public static Place addRoom(PetriNetWithTransits net, String name, boolean initial) {
		Place p = net.createPlace(name);
		if (initial) {
			p.setInitialToken(1);
		}
		return p;
	}
	
	public static void addConnectionOneWay(PetriNetWithTransits net, Place from, Place to, boolean open) {
		Transition connection = net.createTransition("Door:" + from.getId() + "->" + to.getId());
		net.createFlow(from, connection);
		net.createFlow(connection, to);
		net.createTransit(from, connection, to);
		Place control = net.createPlace("Control:" + from.getId() + "->" + to.getId());
		if (open) {
			control.setInitialToken(1);
		}
		net.createFlow(control, connection);
		net.createFlow(connection, control);
	}
	
	public static void addConnectionBothWays(PetriNetWithTransits net, Place p1, Place p2, boolean firstDirection, boolean secondDirection) {
		addConnectionOneWay(net, p1, p2, firstDirection);
		addConnectionOneWay(net, p1, p2, secondDirection);
	}
}
