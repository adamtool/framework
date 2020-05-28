package uniolunisaar.adam.generators.pnwt.util.acencoding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

public class AccessControlChainSplitAtTransitions {
	
	private Set<String> groups;
	private Set<String> locations;
	private Map<String, String> starts; // person -> location
	private Set<Pair<String, String>> connections; // location -> location
	private Map<String, Set<Pair<String, String>>> open; // person -> open doors
	private PetriNetWithTransits net;
	
	static boolean infinitelyManyChains = false;
	
	public AccessControlChainSplitAtTransitions(String name, Set<String> pers, Set<String> locs, Map<String, String> sts, Set<Pair<String, String>> con, Map<String, Set<Pair<String, String>>> op) {
		net = new PetriNetWithTransits(name);
		groups = pers;
		locations= locs;
		starts = sts;
		connections = con;
		open = op;
	}

	public PetriNetWithTransits createAccessControlExample() { 
		for (String group : groups) {
			for (String location : locations) {
				addRoom(group, location);
			}
			
			// add control places for connections
			for (Pair<String, String> connection : connections) {
				Place from = net.getPlace(group + "AT" + connection.getFirst());
				Place to = net.getPlace(group + "AT" + connection.getSecond());
				net.createPlace("OPEN" + from.getId() + "TO" + to.getId());
				net.createPlace("CLOSED" + from.getId() + "TO" + to.getId());
			}
			
			for (String location : locations) {
				addConnection(group, location);
			}
			
			Set<Pair<String, String>> openSet = open.get(group);
			for (Pair<String, String> pair : connections) {
				Place from = net.getPlace(group + "AT" + pair.getFirst());
				Place to = net.getPlace(group + "AT" + pair.getSecond());
				if (openSet.contains(pair)) {
					Place initial = net.getPlace("OPEN" + from.getId() + "TO" + to.getId());
					initial.setInitialToken(1);
				} else {
					Place initial = net.getPlace("CLOSED" + from.getId() + "TO" + to.getId());
					initial.setInitialToken(1);
				}
			}
		}
		return net;
	}
	
	public Place addRoom(String person, String location) {
		String name = person + "AT" + location;
		Place p = net.createPlace(name);
		p.setInitialToken(1);
		if (starts.get(person) != null && starts.get(person).equals(location)) {
			if (infinitelyManyChains) {
				// infinite number of flow chains:
				Transition transitionCreate = net.createTransition("FLOWCREATECHAIN" + name);
				net.createFlow(transitionCreate, p);
				net.createFlow(p, transitionCreate);
				net.createInitialTransit(transitionCreate, p);
				net.createTransit(p, transitionCreate, p);
			} else {
				// one flow chain:
				Place createChainStart = net.createPlace("CREATECHAINstart" + name);
				createChainStart.setInitialToken(1);
				Place createChainEnd = net.createPlace("CREATECHAINend" + name);
				Transition transitionCreate = net.createTransition("FLOWCREATECHAIN" + name);
				net.createFlow(createChainStart, transitionCreate);
				net.createFlow(transitionCreate, createChainEnd);
				net.createFlow(transitionCreate, p);
				net.createFlow(p, transitionCreate);
				net.createInitialTransit(transitionCreate, p);
			}
		}
		return p;
	}
	
	public void addConnection(String group, String location) {
		// calculate postset
		Set<String> post = new HashSet<>();
		for (Pair<String, String> connection : connections) {
			if (connection.getFirst().matches(location)) {
				post.add(connection.getSecond());
			}
		}
		
		Set<Set<String>> powerset = powerSet(post);
		
		for (Set<String> open: powerset) {
			if (open.size() > 0) {
				Set<String> closed = new HashSet<>(post);
				closed.removeAll(open);
				
				Place from = net.getPlace(group + "AT" + location);
				Transition connection = net.createTransition("DOOR" + from.getId() + "TO" + open);
				net.createFlow(from, connection);
				for (String destination : open) {
					Place to = net.getPlace(group + "AT" + destination);
					net.createFlow(connection, to);
					net.createTransit(from, connection, to);
					
					Place control = net.getPlace("OPEN" + from.getId() + "TO" + to.getId());
					net.createFlow(control, connection);
					net.createFlow(connection, control);
				}
				
				for (String destination : closed) {
					Place to = net.getPlace(group + "AT" + destination);
					Place control = net.getPlace("CLOSED" + from.getId() + "TO" + to.getId());
					net.createFlow(control, connection);
					net.createFlow(connection, control);
				}
			}
		}
	}
	
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
	    Set<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	        sets.add(new HashSet<T>());
	        return sets;
	    }
	    List<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
	    for (Set<T> set : powerSet(rest)) {
	        Set<T> newSet = new HashSet<T>();
	        newSet.add(head);
	        newSet.addAll(set);
	        sets.add(newSet);
	        sets.add(set);
	    }       
	    return sets;
	}  
}
