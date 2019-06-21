package uniolunisaar.adam.generators.pnwt.util.sdnencoding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import uniol.apt.adt.extension.ExtensionProperty;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.adt.ts.Arc;
import uniol.apt.adt.ts.State;
import uniol.apt.adt.ts.TransitionSystem;
import uniol.apt.io.parser.ParseException;
import uniol.apt.io.parser.impl.AptLTSParser;
import uniol.apt.util.Pair;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 * 
 * @author Jesko Hecking-Harbusch
 *
 */

// TODO debug why 40 55 65 161 234 237 238 244 246 248 take so long and/or HeapSpaceError?

public class TopologyToPN {
	private TransitionSystem ts;
	private String formula;
	private Set<String> switches = new HashSet<>();

	static boolean useEmptyFull = false;
	
	// TODO XOR between these or none for connectivity
	static boolean packetCoherence = false;
	static boolean loopFreedom = false;
	static boolean dropFreedom = false;
	static boolean eventualDropFreedom = false;
	
	public TopologyToPN(File file) throws ParseException, IOException {
		ts = new AptLTSParser().parseFile(file);
	}
	
	private Pair<String, String> chooseIngressAndEgress(int seed) {
		Random random = new Random(seed);
		String ingress = "";
		String egress = "";
		int size = ts.getEdges().size();
		
		// find ingress (and egress) different from sw000 as it should be unreachable; TODO still path could go through it?
		while (ingress.equals("") || ingress.equals("sw000")) {
			int item = random.nextInt(size);
			int i = 0;
			for(Arc arc : ts.getEdges()) {
				if (i == item) {
					ingress = arc.getSourceId();
				}
			    i++;
			}
		}
		
		// search for different egress
		while (egress.equals("") || ingress.equals(egress)) {
			int item = random.nextInt(size);
			int i = 0;
			for(Arc arc : ts.getEdges()) {
				if (i == item) {
			        egress = arc.getTargetId();
				}
			    i++;
			}
		}
		
		return new Pair<>(ingress, egress);
	}
	
	private Pair<Pair<String,String>,Set<List<String>>> getAllConfigurations () {
		Set<List<String>> allConfigurations = new HashSet<>();
		int seed = 42;
		String ingress = "";
		String egress = "";
		// allConfigurations.size() < 1000 to prevent HeapSpaceError
		while (allConfigurations.size() < 2 && seed < 100) {
			allConfigurations.clear();
			Pair<String, String> ingressAndEgress = chooseIngressAndEgress(seed++);
			ingress = ingressAndEgress.getFirst();
			egress = ingressAndEgress.getSecond();
			
			State start = ts.getNode(ingress);
			State end = ts.getNode(egress);
			Set<State> initialClosed = new HashSet<>();
			initialClosed.add(start);
			Pair<State, Pair<List<String>, Set<State>>> next = null;
			Queue<Pair<State, Pair<List<String>, Set<State>>>> queue = new LinkedList<>();
			queue.add(new Pair<>(start, new Pair<>(new ArrayList<>(), initialClosed)));
			State currentState = null;
			List<String> configuration = null;
			Set<State> closed = null;
		
			// search for all paths
			while ((next = queue.poll()) != null) {
				currentState = next.getFirst();
				configuration = next.getSecond().getFirst();
				closed = next.getSecond().getSecond();
				if (currentState == end) {
					allConfigurations.add(configuration);
				} else {
					for (State nextState : currentState.getPostsetNodes()) {
						if (!closed.contains(nextState)) {
							List<String> newConfiguration = new ArrayList<>(configuration);
							newConfiguration.add(currentState.getId() + "fwdTo" + nextState.getId());
							Set<State> newClosed = new HashSet<>(closed);
							newClosed.add(nextState);
							queue.add(new Pair<>(nextState, new Pair<>(newConfiguration, newClosed)));
						}
					}
				}
			}
		}
		return new Pair<>(new Pair<>(ingress, egress), allConfigurations);
	}
	
	private List<Update> getPathCHANGEUpdate(PetriNetWithTransits pn, List<String> initialConfiguration, List<String> finalConfiguration) {
		List<Update> updateList = new ArrayList<>();
		for (int i = finalConfiguration.size() - 1; i >= 0; --i) {
			String update = finalConfiguration.get(i);
			boolean addConfig = true;
			for (String initial : initialConfiguration) {
				if (update.startsWith(initial.substring(0, 5))) {
					addConfig = false;
					updateList.add(new SwitchUpdate(initial.substring(0, 5), initial.substring(initial.length() - 5, initial.length()), update.substring(update.length() - 5, update.length())));
					break;
				}
			}
			if (addConfig) {
				String switchToUpdate = update.substring(0, 5);
				String original = "";
				String destination = update.substring(update.length() - 5, update.length());
				
				for (State bogus : ts.getNode(switchToUpdate).getPostsetNodes()) {
					if (!bogus.getId().equals(destination)) {
						original = bogus.getId();
						break;
					}
				}
				
				if (original.equals("")) {
					pn.getPlace(update).setInitialToken(1);
				} else {
					pn.getPlace(switchToUpdate + "fwdTo" + original).setInitialToken(1);
					updateList.add(new SwitchUpdate(switchToUpdate, original, destination));
				}
			}
		}
		return updateList;
	}
	
	public String setUpdate(PetriNetWithTransits pn) {
		Pair<Pair<String,String>,Set<List<String>>> result = getAllConfigurations();
		String ingress = result.getFirst().getFirst();
		String egress = result.getFirst().getSecond();
		Set<List<String>> allConfigurations = result.getSecond();
		
		if (allConfigurations.size() < 2) {
			throw new Error(pn.getName() + ": Even after 50 seeds, no two points with more than one route could be found, this example is weird...");
		}
		
		// find initial and final configuration
		Random random = new Random(42);
		List<String> initialConfiguration = null;
		List<String> finalConfiguration = null;
		int size = allConfigurations.size();
		int item = random.nextInt(size);
		int c = 0;
		for(List<String> config : allConfigurations) {
			if (c == item) {
				initialConfiguration = config;
			}
		    c++;
		}
		
		// search for different final configuration
		while (finalConfiguration == null || initialConfiguration.equals(finalConfiguration)) {
			item = random.nextInt(size);
			c = 0;
			for(List<String> config : allConfigurations) {
				if (c == item) {
					finalConfiguration = config;
				}
			    c++;
			}
		}
		
		if (initialConfiguration.size() <= 25) {
			System.out.println("INITIAL CONFIG: " + initialConfiguration);
		} else {
			System.out.println("INITIAL CONFIG is very long: " + initialConfiguration.size());
		}
		if (finalConfiguration.size() <= 25) {
			System.out.println("INITIAL CONFIG: " + finalConfiguration);
		} else {
			System.out.println("INITIAL CONFIG is very long: " + finalConfiguration.size());
		}
	
		
		// packet coherence
		if (packetCoherence) {
			List<String> pathOne = getSwitchesOfConfiguration(initialConfiguration);
			List<String> pathTwo = getSwitchesOfConfiguration(finalConfiguration);
			formula = "A (G" + orSwitches(pathOne) +" OR G" + orSwitches(pathTwo) + ")";
		}
		
		// loop freedom
		if (loopFreedom) {
			List<String> implications = new LinkedList<>();
			for (String sw : switches) {
				if (egress.equals("sw000") && sw.equals("sw001")) { // ingress
					String add = "( NEG sw000 OR (sw000 U NEG sw000))";
					implications.add(add);
				} else if ( ! sw.equals(egress)) {
					String add = "( NEG " + sw + " OR (" + sw + " U NEG " + sw +"))";
					implications.add(add);
				}
			}
			formula = "A G " + andSwitches(implications);
		}
		
		// drop freedom
		if (dropFreedom || eventualDropFreedom) {
			List<String> forward = new LinkedList<>();
			forward.add("pOut");
			forward.add("ingress" + ingress);
			for (Arc arc : ts.getEdges()) {
				forward.add("fwd" + arc.getSourceId() + "to" + arc.getTargetId());
				forward.add("fwd" + arc.getTargetId() + "to" + arc.getSourceId());
			}
			if (dropFreedom) {
				formula = "A G " + orSwitches(forward);
			} else {
				// eventualDropFreedom
				formula = "A F G " + orSwitches(forward);
			}
		}
		
		if (packetCoherence || loopFreedom || dropFreedom || eventualDropFreedom) {
			pn.putExtension("formula", formula, ExtensionProperty.WRITE_TO_FILE);
		}
		
		// Set tokens for initial configuration
		for (String inital : initialConfiguration) {
			pn.getPlace(inital).setInitialToken(1);
		}
		
		// Calculate update
		
		List<Update> updateList;
		updateList = getPathCHANGEUpdate(pn, initialConfiguration, finalConfiguration);
		
		Place updateStart = pn.createPlace("updateStart");
		updateStart.setInitialToken(1);
		new SequentialUpdate(updateList).addUpdate(pn, updateStart);
		
		
		if (useEmptyFull) {
			// empty -> full
			Transition transition = pn.createTransition("ingress" + ingress + "_1");
			String transitionID = transition.getId();
			pn.createFlow(ingress, transitionID);
			pn.createFlow(transitionID, ingress);
			pn.createInitialTransit(transition, pn.getPlace(ingress));
			pn.createTransit(ingress, transitionID, ingress);
			pn.setWeakFair(transition);
			pn.createFlow(ingress + "_empty", transitionID);
			pn.createFlow(transitionID, ingress + "_full");
			// full -> full
			Transition transition2 = pn.createTransition("ingress" + ingress + "_2");
			String transition2ID = transition2.getId();
			pn.createFlow(ingress, transition2ID);
			pn.createFlow(transition2ID, ingress);
			pn.createInitialTransit(transition2, pn.getPlace(ingress));
			pn.createTransit(ingress, transition2ID, ingress);
			pn.setWeakFair(transition2);
			pn.createFlow(ingress + "_empty", transition2ID);
			pn.createFlow(transition2ID, ingress + "_full");
		} else {
			Transition transition = pn.createTransition("ingress" + ingress);
			String transitionID = transition.getId();
			pn.createFlow(ingress, transitionID);
			pn.createFlow(transitionID, ingress);
			pn.createInitialTransit(transition, pn.getPlace(ingress));
			pn.createTransit(ingress, transitionID, ingress);
			pn.setWeakFair(transition);
		}
		
		
		
		pn.rename(pn.getPlace(egress), "pOut");
		pn.getPlace("pOut").setInitialToken(1);
		// sw000 can be renamed to pOut for connectivity, we still need it for unreachablity and therefore rename sw001
		if (egress.equals("sw000")) {
			pn.rename(pn.getPlace("sw001"), "sw000");
			pn.getPlace("sw000").setInitialToken(1);
		}
		return egress;
	}	
	
	// get set of packets for packet coherence
	private List<String> getSwitchesOfConfiguration(List<String> configuration) {
		List<String> result = new LinkedList<>();
		for (String forwarding : configuration) {
			result.add(forwarding.substring(0, 5));
		}
		
		result.add("pOut");
		return result;
	}
	
	// make OR tree
	private String orSwitches (List<String> switches) {
		if (switches.size() == 1) {
			return switches.get(0);
		} else if (switches.size() > 1) {
			int middle = switches.size() / 2;
			return "(" + orSwitches(switches.subList(0, middle)) + " OR " + orSwitches(switches.subList(middle, switches.size())) + ")"; 
		} else {
			// size 0 should not happy and error should be thrown
		}
		
		return null;
	}
	
	// make AND tree
	private String andSwitches (List<String> switches) {
		if (switches.size() == 1) {
			return switches.get(0);
		} else if (switches.size() > 1) {
			int middle = switches.size() / 2;
			return "(" + andSwitches(switches.subList(0, middle)) + " OR " + andSwitches(switches.subList(middle, switches.size())) + ")"; 
		} else {
			// size 0 should not happy and error should be thrown
		}
		
		return null;
	}
	
	public PetriNetWithTransits generatePetriNet() {
		PetriNetWithTransits pn = new PetriNetWithTransits(ts.getName());
		// PetriGameExtensionHandler.setWinningConditionAnnotation(pn, Condition.Objective.LTL);
		for (Arc arc : ts.getEdges()) {
			if (!pn.containsPlace(arc.getSourceId())) {
				if (useEmptyFull) {
					createSwitchWithEmpty(pn, arc.getSourceId());
				} else {
					createSwitch(pn, arc.getSourceId());
				}
			}
			if (!pn.containsPlace(arc.getTargetId())) {
				if (useEmptyFull) {
					createSwitchWithEmpty(pn, arc.getTargetId());
				} else {
					createSwitch(pn, arc.getTargetId());
				}
			}
			if (useEmptyFull) {
				createTransitionWithEmpty(pn, arc.getSourceId(), arc.getTargetId());
			} else {
				createTransition(pn, arc.getSourceId(), arc.getTargetId());
			}
		}
		return pn;
	}
	
	private void createSwitch(PetriNetWithTransits pn, String id) {
		Place sw = pn.createPlace(id);
		switches.add(id);
		sw.setInitialToken(1);
	}

	private void createTransition(PetriNetWithTransits pn, String pre, String post) {
		Transition transition = pn.createTransition("fwd" + pre + "to" + post);
		pn.setWeakFair(transition);
		String transitionID = transition.getId();
		pn.createFlow(pre, transitionID);
		pn.createFlow(transitionID, pre);
		pn.createFlow(post, transitionID);
		pn.createFlow(transitionID, post);
		Place place = pn.createPlace(pre + "fwdTo" + post);
		pn.createFlow(place, transition);
		pn.createFlow(transition, place);
		pn.createTransit(pre, transitionID, post);
		pn.createTransit(post, transitionID, post);
	}
	
	// Create two additional places (empty and full) per switch between which a token
	// changes depending on whether there are data flows in the switch
	private void createSwitchWithEmpty(PetriNetWithTransits pn, String id) {
		Place sw = pn.createPlace(id);
		switches.add(id);
		sw.setInitialToken(1);
		Place swEmpty = pn.createPlace(id + "_empty");
		switches.add(id + "_empty");
		swEmpty.setInitialToken(1);
		pn.createPlace(id + "_full");
		switches.add(id + "_full");
		swEmpty.setInitialToken(1);
	}
	
	private void createTransitionWithEmpty(PetriNetWithTransits pn, String pre, String post) {
		// empty -> full (at post)
		Transition transition = pn.createTransition("fwd" + pre + "to" + post + "_1");
		pn.setWeakFair(transition);
		String transitionID = transition.getId();
		pn.createFlow(pre, transitionID);
		pn.createFlow(transitionID, pre);
		pn.createFlow(post, transitionID);
		pn.createFlow(transitionID, post);
		pn.createFlow(pre + "_full", transitionID);
		pn.createFlow(transitionID, pre + "_empty");
		pn.createFlow(post + "_empty", transitionID);
		pn.createFlow(transitionID, post + "_full");
		Place place = pn.createPlace(pre + "fwdTo" + post);
		pn.createFlow(place, transition);
		pn.createFlow(transition, place);
		pn.createTransit(pre, transitionID, post);
		pn.createTransit(post, transitionID, post);
		// full -> full (at post)
		Transition transition2 = pn.createTransition("fwd" + pre + "to" + post + "_2");
		pn.setWeakFair(transition2);
		String transition2ID = transition2.getId();
		pn.createFlow(pre, transition2ID);
		pn.createFlow(transition2ID, pre);
		pn.createFlow(post, transition2ID);
		pn.createFlow(transition2ID, post);
		pn.createFlow(pre + "_full", transition2ID);
		pn.createFlow(transition2ID, pre + "_empty");
		pn.createFlow(post + "_full", transition2ID);
		pn.createFlow(transition2ID, post + "_full");
		pn.createFlow(place, transition2);
		pn.createFlow(transition2, place);
		pn.createTransit(pre, transition2ID, post);
		pn.createTransit(post, transition2ID, post);
	}
}
