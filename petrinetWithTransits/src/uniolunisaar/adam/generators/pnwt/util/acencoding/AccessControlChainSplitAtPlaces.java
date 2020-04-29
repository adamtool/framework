package uniolunisaar.adam.generators.pnwt.util.acencoding;

import java.util.Map;
import java.util.Set;

import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.util.Pair;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 * 
 * @author Jesko Hecking-Harbusch
 *
 */

public class AccessControlChainSplitAtPlaces {
	
	private Set<String> groups;
	private Set<String> locations;
	private Map<String, String> starts; // person -> location
	private Set<Pair<String, String>> connections; // location -> location
	private Map<String, Set<Pair<String, String>>> open; // person -> open doors
	private PetriNetWithTransits net;
	
	public AccessControlChainSplitAtPlaces(String name, Set<String> pers, Set<String> locs, Map<String, String> sts, Set<Pair<String, String>> con, Map<String, Set<Pair<String, String>>> op) {
		net = new PetriNetWithTransits(name);
		groups = pers;
		locations= locs;
		starts = sts;
		connections = con;
		open = op;
	}

	public PetriNetWithTransits createAccessControlExample() { 
		for (String person : groups) {
			for (String location : locations) {
				addRoom(person, location);
			}
			
			for (Pair<String, String> pair : connections) {
				addConnection(person, pair.getFirst(), pair.getSecond());
			}
			
			for (Pair<String, String> pair : open.get(person)) {
				Place from = net.getPlace(person + "AT" + pair.getFirst());
				Place to = net.getPlace(person + "AT" + pair.getSecond());
				Place initial = net.getPlace("CONTROL" + from.getId() + "TO" + to.getId());
				initial.setInitialToken(1);
			}
		}
		// TODO open doors for all benchmarks
		// TODO make all connections based on set of pairs instead of hashmaps
		// TODO shared doors, limited throughput door
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
