package uniolunisaar.adam.generators.pnwt.util.acencoding;

import java.util.Map;
import java.util.Set;

import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 * 
 * @author Jesko Hecking-Harbusch
 *
 */

public class AccessControl {
	
	private Set<String> groups;
	private Set<String> locations;
	private Map<String, String> starts; // person -> location
	private Map<String, String> connections; // location -> location
	private PetriNetWithTransits net;
	
	public AccessControl(String name, Set<String> pers, Set<String> locs, Map<String, String> sts) {
		net = new PetriNetWithTransits(name);
		groups = pers;
		locations= locs;
		starts = sts;
	}

	public PetriNetWithTransits createAccessControlExample() { 
		for (String person : groups) {
			for (String location : locations) {
				if (starts.get(person).equals(location)) {
					addRoom(person, location);
				} else {
					addRoom(person, location);
				}
			}
		}
		for (String from : locations) {
			String to = connections.get(from);
			if (to != null) {
				for (String person : groups) {
					addConnection(person, from, to);
				}
			}
		}
		// TODO door configuration
		return net;
	}
	
	public Place addRoom(String person, String location) {
		String name = person + "AT" + location;
		Place p = net.createPlace(name);
		if (starts.get(person) != null && starts.get(person).equals(location)) {
			Place createChain = net.createPlace("CREATECHAIN" + name);
			createChain.setInitialToken(1);
			Transition transitionCreate = net.createTransition("FLOWCREATECHAIN" + name);
			net.createFlow(createChain, transitionCreate);
			net.createFlow(transitionCreate, p);
			net.createInitialTransit(transitionCreate, p);
			
		}
		return p;
	}
	
	public void addConnection(String person, String origin, String destination) {
		Place from = net.getPlace(person + "AT" + origin);
		Place to = net.getPlace(person + "AT" + destination);
		Transition connection = net.createTransition("DOOR" + from.getId() + "TO" + to.getId());
		net.createFlow(from, connection);
		net.createFlow(connection, to);
		net.createTransit(from, connection, to);
		Place control = net.createPlace("CONTROL" + from.getId() + "TO" + to.getId());
		net.createFlow(control, connection);
		net.createFlow(connection, control);
	}
}
