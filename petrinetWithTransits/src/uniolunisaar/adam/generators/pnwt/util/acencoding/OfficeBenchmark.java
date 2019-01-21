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

public class OfficeBenchmark {
	
	// TODO each group day and night
	
	private static Set<String> generateGroups() {
		Set<String> groups = new HashSet<>();
		groups.add("visitor");
		groups.add("hr");
		groups.add("researcher");
		groups.add("it");
		groups.add("postman");
		return groups;
	}
	
	private static Map<String, String> generateStarts() {
		Map<String, String> starts = new HashMap<>();
		starts.put("visitor", "out");
		starts.put("hr", "out");
		starts.put("researcher", "out");
		starts.put("it", "out");
		starts.put("postman", "out");
		return starts;
	}
	
	public static PetriNetWithTransits generateOfficeSmall() {
		String name = "officeSmall";
		Set<String> groups = generateGroups();
		
		Set<String> locations = new HashSet<>();
		locations.add("out");
		locations.add("lob");
		locations.add("cor");
		locations.add("mr");
		locations.add("bur");
		
		Map<String, String> starts = generateStarts();
		
		Map<String, String> connections = new HashMap<>();
		connections.put("out", "lob");
		connections.put("out", "cor");
		connections.put("lob", "cor");
		connections.put("cor", "mr");
		connections.put("cor", "bur");
		
		return new AccessControl(name, groups, locations, starts).createAccessControlExample();
	}
	
	public static PetriNetWithTransits generateOfficeNormal() {
		String name = "officeNormal";
		Set<String> groups = generateGroups();
		
		Set<String> locations = new HashSet<>();
		locations.add("out");
		locations.add("lob");			// lobby
		locations.add("mr1");			// meeting rooms
		locations.add("mr2");
		locations.add("mr3");
		locations.add("co1");			// corridors
		locations.add("co2");
		locations.add("co3");
		locations.add("bu1");			// offices
		locations.add("bu2");
		locations.add("bu3");
		locations.add("bu4");
		locations.add("bu5");
		locations.add("buS");			// office staff
		locations.add("bu7");
		locations.add("wc1");
		locations.add("wc2");
		locations.add("ser");			// server
		locations.add("pos");			// post office
		locations.add("ele");			// elevator
		
		Map<String, String> starts = generateStarts();
		
		Map<String, String> connections = new HashMap<>();
		connections.put("out", "lob");
		connections.put("lob", "mr1");
		connections.put("lob", "mr2");
		connections.put("lob", "mr3");
		connections.put("lob", "co1");
		connections.put("out", "co2");
		connections.put("out", "co3");
		connections.put("co1", "co3");
		connections.put("co1", "bu1");
		connections.put("co1", "bu2");
		connections.put("co1", "bu3");
		connections.put("co1", "bu4");
		connections.put("co1", "bu5");
		connections.put("co1", "buS");
		connections.put("co1", "bu7");
		connections.put("co1", "wc1");
		connections.put("co1", "wc2");
		connections.put("co1", "pos");
		connections.put("co2", "ser");
		connections.put("co2", "pos");
		connections.put("co2", "ele");				// access controll happens on other level
		
		return new AccessControl(name, groups, locations, starts).createAccessControlExample();
	}
}
		// visitorDay:
		// new ExistsEventually(new AP(mr1));
		// new ExistsEventually(new AP(mr2));
		// new ExistsEventually(new AP(mr3));
		// new ExistsEventually(new AP(lob));
		// new ForallGlobally(new Not(new AP(bu1)));
		// new ForallGlobally(new Not(new AP(bu2)));
		// new ForallGlobally(new Not(new AP(bu3)));
		// new ForallGlobally(new Not(new AP(bu4)));
		// new ForallGlobally(new Not(new AP(bu5)));
		// new ForallGlobally(new Not(new AP(buS)));
		// new ForallGlobally(new Not(new AP(bu7)));
		// new ForallGlobally(new Not(new AP(ser)));
		// new ForallRelease(new AP(lob), new AP(mr1));	// always satisfied makes 10 seconds slower
		// new ForallRelease(new AP(lob), new AP(mr2));	// always satisfied
		// new ForallRelease(new AP(lob), new AP(mr3));	// always satisfied
		// TODO "visitor may visit restroom from meeting room"-constraint
		
		// visitorNight: 
		// new ForallGlobally(new AP(out));
		// new ForallGlobally(new Not(new AP(bu1)));
		// new ForallGlobally(new Not(new AP(bu2)));
		// new ForallGlobally(new Not(new AP(bu3)));
		// new ForallGlobally(new Not(new AP(bu4)));
		// new ForallGlobally(new Not(new AP(bu5)));
		// new ForallGlobally(new Not(new AP(buS)));
		// new ForallGlobally(new Not(new AP(bu7)));
		// new ForallGlobally(new Not(new AP(ser)));

		// hrDay:
		// new ExistsEventually(new AP(bu1));
		// new ExistsEventually(new AP(bu2));
		// ExistsEventually(new AP(bu3));
		// new ExistsEventually(new AP(bu4));
		// new ExistsEventually(new AP(bu5));
		// new ExistsEventually(new AP(buS));
		// new ExistsEventually(new AP(bu7));
		// new ExistsEventually(new AP(pos));
		// new ExistsEventually(new AP(lob));
		// new ForallGlobally(new Not(new AP(ser)));

		// hrNight:
		// TODO pin

		// researcherDay: 
		// new ExistsEventually(new AP(bu1));
		// new ExistsEventually(new AP(bu2));
		// new ExistsEventually(new AP(bu3));
		// new ExistsEventually(new AP(bu4));
		// new ExistsEventually(new AP(bu5));
		// new ExistsEventually(new AP(bu7));
		// new ExistsEventually(new AP(lob));
		// new ForallGlobally(new Not(new AP(ser)));
		// new ForallGlobally(new Not(new AP(pos)));
		// new ForallGlobally(new Not(new AP(buS)));
			
		// researcherNight:
		// TODO pin

		// itDay:
		// new ExistsEventually(new AP(bu1));
		// new ExistsEventually(new AP(bu2));
		// new ExistsEventually(new AP(bu3));
		// new ExistsEventually(new AP(bu4));
		// new ExistsEventually(new AP(bu5));
		// new ExistsEventually(new AP(bu7));
		// new ExistsEventually(new AP(lob));
		// new ExistsEventually(new AP(ser));
		// new ForallGlobally(new Not(new AP(pos)));
		// new ForallGlobally(new Not(new AP(buS)));
			
		// itNight:
		// TODO pin
		
		//postmanDay:
		// new ExistsEventually(new AP(pos));
		// new ForallGlobally(new Not(new AP(bu1)));
		// new ForallGlobally(new Not(new AP(bu2)));
		// new ForallGlobally(new Not(new AP(bu3)));
		// new ForallGlobally(new Not(new AP(bu4)));
		// new ForallGlobally(new Not(new AP(bu5)));
		// new ForallGlobally(new Not(new AP(buS)));
		// new ForallGlobally(new Not(new AP(bu7)));
		// new ForallGlobally(new Not(new AP(ser)));
		// new ForallGlobally(new Not(new AP(co1)));
		// TODO "after reaching post office elevator has to be reachable"-constraint
			
		// postmanNight:
		// TODO unspecified? like visitor at night?
