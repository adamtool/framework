package uniolunisaar.adam.generators.pnwt.util.acencoding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 * 
 * @author Jesko Hecking-Harbusch
 *
 */

public class EscapeBenchmark {

	public static PetriNetWithTransits generateEscape() {
		String name = "escape";
		Set<String> groups = new HashSet<>();
		groups.add("one");
		groups.add("two");
		
		Set<String> locations = new HashSet<>();
		locations.add("s1");
		locations.add("s2");
		locations.add("b1");
		locations.add("b2");
		locations.add("g1");
		locations.add("g2");
		locations.add("d1");
		locations.add("d2");
		locations.add("S1");
		locations.add("S2");
		locations.add("B1");
		locations.add("B2");
		locations.add("G1");
		locations.add("G2");
		locations.add("e1");
		locations.add("e2");
		locations.add("t1");
		locations.add("t2");
		
		Map<String, String> starts = new HashMap<>();
		starts.put("one", "s1");
		starts.put("two", "s2");
		
		Map<String, String> connections = new HashMap<>();
		connections.put("s1", "b1");
		connections.put("s1", "g1");
		connections.put("s2", "b2");
		connections.put("s2", "g2");
		connections.put("b1", "d1");
		connections.put("g1", "d1");
		connections.put("b1", "d2");
		connections.put("g2", "d2");
		
		connections.put("S1", "B1");
		connections.put("S1", "G1");
		connections.put("S2", "B2");
		connections.put("S2", "G2");
		connections.put("B1", "e1");
		connections.put("G1", "e1");
		connections.put("B1", "e2");
		connections.put("G2", "e2");
		
		connections.put("e1", "t1");
		connections.put("e2", "t2");
		
		// TODO only one of the following three doors can be used (differentiate them):
		// TODO synchronous door:
		connections.put("d1", "S1");
		connections.put("d2", "S2");
		
		// TODO asynchronous door 1
		connections.put("d1", "S1");
		
		// TODO asynchronous door2
		connections.put("d2", "S2");
		
		return new AccessControlChainSplitAtPlaces(name, groups, locations, starts, null, null).createAccessControlExample();
	
		// new ExistsEventually(new AP(e1)) AND new ExistsEventually(new AP(e2))
	}
}
