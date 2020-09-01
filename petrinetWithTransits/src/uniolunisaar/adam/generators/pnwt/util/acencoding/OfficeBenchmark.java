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

public class OfficeBenchmark {
	
	// TODO each group day and night
	
	private static Set<String> generateGroups() {
		Set<String> groups = new HashSet<>();
		groups.add("visitor");		// only: 15 sec
		groups.add("hr");			// two:
		//groups.add("researcher");
		//groups.add("it");
		//groups.add("postman");
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
	
	public static PetriNetWithTransits generateOfficeToy() {
		String name = "officeVeryToy";
		Set<String> groups = generateGroups();
		Set<String> locations = new HashSet<>();
		locations.add("out");
		locations.add("in1");
		locations.add("in2");
		locations.add("in3");
		
		Map<String, String> starts = generateStarts();
		
		Set<Pair<String, String>> connections = new HashSet<>();
		connections.add(new Pair<String, String>("out", "in1"));
		connections.add(new Pair<String, String>("in1", "out"));
		
		connections.add(new Pair<String, String>("out", "in2"));
		connections.add(new Pair<String, String>("in2", "out"));
		
		connections.add(new Pair<String, String>("in2", "in3"));
		connections.add(new Pair<String, String>("in3", "in2"));
		
		Map<String, Set<Pair<String, String>>> open = new HashMap<>();
		
		Set<Pair<String, String>> visitorOpen = new HashSet<>();
		//visitorOpen.add(new Pair<String, String>("out", "in1"));
		visitorOpen.add(new Pair<String, String>("in1", "out"));
		visitorOpen.add(new Pair<String, String>("out", "in2"));
		visitorOpen.add(new Pair<String, String>("in2", "out"));
		visitorOpen.add(new Pair<String, String>("in2", "in3"));
		visitorOpen.add(new Pair<String, String>("in3", "in2"));
		open.put("visitor", visitorOpen);
		
		Set<Pair<String, String>> hrOpen = new HashSet<>();
		hrOpen.add(new Pair<String, String>("out", "in1"));
		hrOpen.add(new Pair<String, String>("in1", "out"));
		hrOpen.add(new Pair<String, String>("out", "in2"));
		hrOpen.add(new Pair<String, String>("in2", "out"));
		hrOpen.add(new Pair<String, String>("in2", "in3"));
		hrOpen.add(new Pair<String, String>("in3", "in2"));
		open.put("hr", hrOpen);
		
		Set<Pair<String, String>> researcherOpen = new HashSet<>();
		researcherOpen.add(new Pair<String, String>("out", "in1"));
		researcherOpen.add(new Pair<String, String>("in1", "out"));
		researcherOpen.add(new Pair<String, String>("out", "in2"));
		researcherOpen.add(new Pair<String, String>("in2", "out"));
		researcherOpen.add(new Pair<String, String>("in2", "in3"));
		researcherOpen.add(new Pair<String, String>("in3", "in2"));
		open.put("researcher", researcherOpen);
		
		Set<Pair<String, String>> itOpen = new HashSet<>();
		itOpen.add(new Pair<String, String>("out", "in1"));
		itOpen.add(new Pair<String, String>("in1", "out"));
		itOpen.add(new Pair<String, String>("out", "in2"));
		itOpen.add(new Pair<String, String>("in2", "out"));
		itOpen.add(new Pair<String, String>("in2", "in3"));
		itOpen.add(new Pair<String, String>("in3", "in2"));
		open.put("it", itOpen);
		
		return new AccessControlChainSplitAtPlaces(name, groups, locations, starts, connections, open).createAccessControlExample();
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
		
		Set<Pair<String, String>> connections = new HashSet<>();
		connections.add(new Pair<String, String>("out", "lob"));
		connections.add(new Pair<String, String>("lob", "out"));
		
		connections.add(new Pair<String, String>("out", "cor"));
		connections.add(new Pair<String, String>("cor", "out"));
		
		connections.add(new Pair<String, String>("lob", "cor"));
		connections.add(new Pair<String, String>("cor", "lob"));

		connections.add(new Pair<String, String>("cor", "mr"));
		connections.add(new Pair<String, String>("mr", "cor"));
		
		connections.add(new Pair<String, String>("cor", "bur"));
		connections.add(new Pair<String, String>("bur", "cor"));
		
		Map<String, Set<Pair<String, String>>> open = new HashMap<>();
		
		Set<Pair<String, String>> visitorOpen = new HashSet<>();
		visitorOpen.add(new Pair<String, String>("out", "lob"));
		visitorOpen.add(new Pair<String, String>("lob", "out"));
		//visitorOpen.add(new Pair<String, String>("out", "cor"));
		visitorOpen.add(new Pair<String, String>("cor", "out"));
		visitorOpen.add(new Pair<String, String>("lob", "cor"));
		visitorOpen.add(new Pair<String, String>("cor", "lob"));
		visitorOpen.add(new Pair<String, String>("cor", "mr"));
		visitorOpen.add(new Pair<String, String>("mr", "cor"));
		//visitorOpen.add(new Pair<String, String>("cor", "bur"));
		visitorOpen.add(new Pair<String, String>("bur", "cor"));
		open.put("visitor", visitorOpen);
		
		Set<Pair<String, String>> hrOpen = new HashSet<>();
		hrOpen.add(new Pair<String, String>("out", "lob"));
		hrOpen.add(new Pair<String, String>("lob", "out"));
		hrOpen.add(new Pair<String, String>("out", "cor"));
		hrOpen.add(new Pair<String, String>("cor", "out"));
		hrOpen.add(new Pair<String, String>("lob", "cor"));
		hrOpen.add(new Pair<String, String>("cor", "lob"));
		hrOpen.add(new Pair<String, String>("cor", "mr"));
		hrOpen.add(new Pair<String, String>("mr", "cor"));
		hrOpen.add(new Pair<String, String>("cor", "bur"));
		hrOpen.add(new Pair<String, String>("bur", "cor"));
		open.put("hr", hrOpen);
		
		Set<Pair<String, String>> researcherOpen = new HashSet<>();
		researcherOpen.add(new Pair<String, String>("out", "lob"));
		researcherOpen.add(new Pair<String, String>("lob", "out"));
		researcherOpen.add(new Pair<String, String>("out", "cor"));
		researcherOpen.add(new Pair<String, String>("cor", "out"));
		researcherOpen.add(new Pair<String, String>("lob", "cor"));
		researcherOpen.add(new Pair<String, String>("cor", "lob"));
		researcherOpen.add(new Pair<String, String>("cor", "mr"));
		researcherOpen.add(new Pair<String, String>("mr", "cor"));
		researcherOpen.add(new Pair<String, String>("cor", "bur"));
		researcherOpen.add(new Pair<String, String>("bur", "cor"));
		open.put("researcher", researcherOpen);
		
		Set<Pair<String, String>> itOpen = new HashSet<>();
		itOpen.add(new Pair<String, String>("out", "lob"));
		itOpen.add(new Pair<String, String>("lob", "out"));
		itOpen.add(new Pair<String, String>("out", "cor"));
		itOpen.add(new Pair<String, String>("cor", "out"));
		itOpen.add(new Pair<String, String>("lob", "cor"));
		itOpen.add(new Pair<String, String>("cor", "lob"));
		itOpen.add(new Pair<String, String>("cor", "mr"));
		itOpen.add(new Pair<String, String>("mr", "cor"));
		itOpen.add(new Pair<String, String>("cor", "bur"));
		itOpen.add(new Pair<String, String>("bur", "cor"));
		open.put("it", itOpen);
		
		Set<Pair<String, String>> postmanOpen = new HashSet<>();
		postmanOpen.add(new Pair<String, String>("out", "lob"));
		postmanOpen.add(new Pair<String, String>("lob", "out"));
		postmanOpen.add(new Pair<String, String>("out", "cor"));
		postmanOpen.add(new Pair<String, String>("cor", "out"));
		postmanOpen.add(new Pair<String, String>("lob", "cor"));
		postmanOpen.add(new Pair<String, String>("cor", "lob"));
		postmanOpen.add(new Pair<String, String>("cor", "mr"));
		postmanOpen.add(new Pair<String, String>("mr", "cor"));
		postmanOpen.add(new Pair<String, String>("cor", "bur"));
		postmanOpen.add(new Pair<String, String>("bur", "cor"));
		open.put("postman", postmanOpen);
		
		return new AccessControlChainSplitAtPlaces(name, groups, locations, starts, connections, open).createAccessControlExample();
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
		
		return new AccessControlChainSplitAtPlaces(name, groups, locations, starts, null, null).createAccessControlExample();
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
