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
		
		Map<String, String> connections = new HashMap<>();
		connections.put("out", "eco");
		connections.put("out", "pri");
		connections.put("eco", "co1");
		connections.put("pri", "co2");
		connections.put("co1", "cp2");
		connections.put("co2", "se1");
		connections.put("co2", "se2");
		connections.put("co2", "se3");
		connections.put("co2", "se4");
		connections.put("co3", "se1");
		connections.put("co3", "se2");
		connections.put("co3", "se3");
		connections.put("co3", "se4");
		connections.put("co3", "el1");
		connections.put("co3", "sho");
		connections.put("sho", "el2");
	
		return new AccessControl(name, groups, locations, starts).createAccessControlExample();
		
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
