package uniolunisaar.adam.generators.pnwt.util.acencoding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uniol.apt.util.Pair;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 * 
 * @author Jesko Hecking-Harbusch
 *
 */

public class AirportBenchmark {
	
	public static PetriNetWithTransits generateAirport() {
		String name = "airport";
		Set<String> groups = new HashSet<>();
		groups.add("staff");
		groups.add("economy");
		groups.add("priority");

		Set<String> locations = new HashSet<>();
		locations.add("out");
		locations.add("eco");
		locations.add("pri");
		locations.add("sho");
		locations.add("co1");
		locations.add("co2");
		locations.add("co3");
		locations.add("se1");
		locations.add("se2");
		locations.add("se3");
		locations.add("se4");
		locations.add("el1");
		locations.add("el2");
		
		Map<String, String> starts = new HashMap<>();
		starts.put("staff", "out");
		starts.put("economy", "out");
		starts.put("priority", "out");
		
		Set<Pair<String, String>> connections = new HashSet<>();
		connections.add(new Pair<>("out", "eco"));
		connections.add(new Pair<>("out", "pri"));
		connections.add(new Pair<>("eco", "co1"));
		connections.add(new Pair<>("pri", "co2"));
		connections.add(new Pair<>("co1", "cp2"));
		connections.add(new Pair<>("co2", "se1"));
		connections.add(new Pair<>("co2", "se2"));
		connections.add(new Pair<>("co2", "se3"));
		connections.add(new Pair<>("co2", "se4"));
		connections.add(new Pair<>("co3", "se1"));
		connections.add(new Pair<>("co3", "se2"));
		connections.add(new Pair<>("co3", "se3"));
		connections.add(new Pair<>("co3", "se4"));
		connections.add(new Pair<>("co3", "el1"));
		connections.add(new Pair<>("co3", "sho"));
		connections.add(new Pair<>("sho", "el2"));
	
		return new AccessControl(name, groups, locations, starts, null, null).createAccessControlExample();
		
		// staff:
		// and:
		// new ExistsEventually(new AP(sho));
		// new ExistsEventually(new AP(el1));
		// new ExistsEventually(new AP(el2));
			
		// economy:
		// and:
		// A(G(¬pri))
		// A(G(¬el1))
		// A(G(¬el2))
		// A(G(¬se1))
		// new ExistsEventually(new AP(sho));
		// new ExistsEventually(new AP(co3));
		// (Or(se2, se3, se4)) R  (co3)	
		
		// priority:
		// TODO and all other requirements
	}	
}
