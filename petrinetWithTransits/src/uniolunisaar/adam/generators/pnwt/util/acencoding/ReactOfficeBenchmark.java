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

public class ReactOfficeBenchmark {
	
	public static PetriNetWithTransits generateReactOffice() {
		String name = "officeReact";
		Set<String> groups = new HashSet<>();
		groups.add("");
	
		Set<String> locations = new HashSet<>();
		locations.add("out");
		locations.add("coP");		// public corridor
		locations.add("coR");		// react corridor
		locations.add("wcF");
		locations.add("WcM");
		locations.add("sto");		// storage room
		locations.add("la1");		// lab 1
		locations.add("kit");		// kitchen
		locations.add("la2");		// lab 2
		locations.add("pri");		// printer room
		locations.add("lou");		// lounge
		locations.add("sec");		// secretary
		locations.add("pro");		// professor
		for (int i = 0; i < 6; ++i) {
			locations.add("ro" + i);
		}
		
		Map<String, String> starts = new HashMap<>();
		starts.put("", "out");
		
		Map<String, String> connections = new HashMap<>();
		connections.put("out", "coP");
		
		connections.put("coP", "coR");
		connections.put("coP", "sto");
		connections.put("coP", "wcF");
		connections.put("coP", "wcM");
		connections.put("coP", "la1");
		
		connections.put("coR", "kit");
		connections.put("coR", "lou");
		connections.put("coR", "pri");
		connections.put("coR", "la2");
		connections.put("coR", "sec");
		connections.put("coR", "pro");
		
		for (int i = 0; i < 6; ++i) {
			connections.put("coR", "ro" + i);
		}
		
		connections.put("sec", "pro");
		connections.put("sec", "ro5");
		
		return new AccessControl(name, groups, locations, starts, null, null).createAccessControlExample();
		
		
		// new ForallGlobally(new Not(new AP(sec)));
		// new ForallGlobally(new Not(new AP(kit)));
		// new ForallGlobally(new Not(new AP(pri)));
		// new ForallGlobally(new Not(new AP(lou)));
		// new ForallGlobally(new Not(new AP(sto)));
		// new ForallRelease(new AP(sec), new AP(pro));
		// new ForallRelease(new AP(sec), new Not(new AP(rooms[rooms.length - 1])));
		// new ExistsEventually(new AP(la1));
		// new ExistsEventually(new AP(la2));
		// new ExistsEventually(new AP(wcF));
		// new ExistsEventually(new AP(wcM));
	}
}
