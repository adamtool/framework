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

public class UniversityBenchmark {

	public static PetriNetWithTransits generateUniversity() {
		String name = "university";
		Set<String> groups = new HashSet<>();
		groups.add("cor");
		groups.add("mix");
		groups.add("roo");
		
		Set<String> locations = new HashSet<>();
		locations.add("out");
		locations.add("co1");
		locations.add("co2");
		locations.add("coM");
		
		for (int i =  0; i < 62; ++i) {
			locations.add("r" +  String.format("%02d", i));
		}

		Map<String, String> starts = new HashMap<>();
		starts.put("cor", "out");
		starts.put("mix", "out");
		starts.put("roo", "out");
		
		Map<String, String> connections = new HashMap<>();
		connections.put("out", "co1");
		connections.put("out", "co2");
		connections.put("out", "coM");
		
		for (int i =  0; i <=  8; ++i)
			connections.put("co1", "r" +  String.format("%02d", i));
		
		for (int i = 34; i <= 38; ++i)
			connections.put("co2", "r" +  String.format("%02d", i));
		
		for (int i = 41; i <= 46; ++i)
			connections.put("co2", "r" +  String.format("%02d", i));
		
		for (int i =  9; i <= 33; ++i)
			connections.put("coM", "r" +  String.format("%02d", i));
		
		for (int i = 52; i <= 61; ++i)
			connections.put("coM", "r" +  String.format("%02d", i));
		
		connections.put("coM", "r39");
		connections.put("coM", "r40");
		connections.put("coM", "r47");
		connections.put("coM", "r48");
		
		connections.put("r48", "r49");
		connections.put("r48", "r50");
		connections.put("r48", "r51");
		
		return new AccessControl(name, groups, locations, starts).createAccessControlExample();
		
		// studentMix:
		//	for (int i = 0; i < 21; ++i) 
		//		new ExistsEventually(new AP(rooms[i + 13]));
		//	for (int i = 0; i < 6; ++i) 
		//		new ExistsEventually(new AP(rooms[i + 52]));
			
		//	new ForallGlobally(new Not(new AP(rooms[ 9])));
		//	new ForallGlobally(new Not(new AP(rooms[10])));
		//	new ForallGlobally(new Not(new AP(rooms[11])));
		//	new ForallGlobally(new Not(new AP(rooms[12])));
		//	new ForallGlobally(new Not(new AP(rooms[39])));
		//	new ForallGlobally(new Not(new AP(rooms[40])));
		//	new ForallGlobally(new Not(new AP(rooms[47])));
		//	new ForallGlobally(new Not(new AP(rooms[48])));
		//	new ForallGlobally(new Not(new AP(rooms[58])));
		//	new ForallGlobally(new Not(new AP(rooms[59])));
		//	new ForallGlobally(new Not(new AP(rooms[60])));
		//	new ForallGlobally(new Not(new AP(rooms[61])));
		//	new ForallGlobally(new Not(new AP(co1)));
		//	for (int i = 0; i < 5; ++i)
		//		new ForallGlobally(new Not(new AP(rooms[i + 34])));
		//	for (int i = 0; i < 6; ++i)
		//		new ForallGlobally(new Not(new AP(rooms[i + 41])));
		
		// studentCor:
		//	for (int i = 0; i < 21; ++i) 
		//		new ExistsEventually(new AP(rooms[i + 13]));
		//	for (int i = 0; i < 6; ++i) 
		//		new ExistsEventually(new AP(rooms[i + 52]));	
		//	new ForallGlobally(new Not(new AP(rooms[ 9])));
		//	new ForallGlobally(new Not(new AP(rooms[10])));
		//	new ForallGlobally(new Not(new AP(rooms[11])));
		//	new ForallGlobally(new Not(new AP(rooms[12])));
		//	new ForallGlobally(new Not(new AP(rooms[39])));
		//	new ForallGlobally(new Not(new AP(rooms[40])));
		//	new ForallGlobally(new Not(new AP(rooms[47])));
		//	new ForallGlobally(new Not(new AP(rooms[48])));
		//	new ForallGlobally(new Not(new AP(rooms[58])));
		//	new ForallGlobally(new Not(new AP(rooms[59])));
		//	new ForallGlobally(new Not(new AP(rooms[60])));
		//	new ForallGlobally(new Not(new AP(rooms[61])));	
		//	new ForallGlobally(new Not(new AP(co1)));
		//	new ForallGlobally(new Not(new AP(co2)));
		
		// studentRoo:
		//	for (int i = 0; i < 21; ++i) 
		//		new ExistsEventually(new AP(rooms[i + 13]));
		//	for (int i = 0; i < 6; ++i) 
		//		new ExistsEventually(new AP(rooms[i + 52]));
		//	new ForallGlobally(new Not(new AP(rooms[ 9])));
		//	new ForallGlobally(new Not(new AP(rooms[10])));
		//	new ForallGlobally(new Not(new AP(rooms[11])));
		//	new ForallGlobally(new Not(new AP(rooms[12])));
		//	new ForallGlobally(new Not(new AP(rooms[39])));
		//	new ForallGlobally(new Not(new AP(rooms[40])));
		//	new ForallGlobally(new Not(new AP(rooms[47])));
		//	new ForallGlobally(new Not(new AP(rooms[48])));
		//	new ForallGlobally(new Not(new AP(rooms[58])));
		//	new ForallGlobally(new Not(new AP(rooms[59])));
		//	new ForallGlobally(new Not(new AP(rooms[60])));
		//	new ForallGlobally(new Not(new AP(rooms[61])));
		//	for (int i = 0; i < 9; ++i)
		//		new ForallGlobally(new Not(new AP(rooms[i])));
		//	for (int i = 0; i < 5; ++i)
		//		new ForallGlobally(new Not(new AP(rooms[i + 34])));
		//	for (int i = 0; i < 6; ++i)
		//		new ForallGlobally(new Not(new AP(rooms[i + 41])));		
	}
}
