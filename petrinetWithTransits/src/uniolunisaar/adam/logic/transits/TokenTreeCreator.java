package uniolunisaar.adam.logic.transits;

//package uniolunisaar.adam.logic.tokenflow;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//import uniol.apt.adt.pn.Marking;
//import uniol.apt.adt.pn.Place;
//import uniol.apt.adt.pn.Transition;
//import uniol.apt.analysis.coverability.CoverabilityGraph;
//import uniol.apt.analysis.coverability.CoverabilityGraphEdge;
//import uniol.apt.analysis.coverability.CoverabilityGraphNode;
//import uniolunisaar.adam.ds.petrigame.PetriGame;
//import uniolunisaar.adam.ds.petrigame.TokenChain;
//import uniolunisaar.adam.ds.petrigame.TokenFlow;
//import uniolunisaar.adam.ds.petrigame.TokenTree;
//
///**
// *
// * @author Manuel Gieseking
// */
//@Deprecated
//public class TokenTreeCreator {
//
//    // Next three methods only moved from game, since this class is not needed anymore 
//    public static List<TokenTree> getTokenTrees(PetriGame game) {
//        return (List<TokenTree>) game.getExtension("tokentrees");
//    }
//
//    private static boolean hasTokenTrees(PetriGame game) {
//        return game.hasExtension("tokentrees");
//    }
//
//    private static void setTokenTrees(PetriGame game, List<TokenTree> trees) {
//        game.putExtension("tokentrees", trees);
//    }
//
//    @Deprecated
//    public static void createAndAnnotateTokenTree(PetriGame game) {
//        if (hasTokenTrees(game)) {
//            return;
//        }
//
//        List<Place> initalPlaces = new ArrayList<>();
//        // initial token
//        Marking m = game.getInitialMarking();
//        for (Place place : game.getPlaces()) {
//            if (m.getToken(place).getValue() > 0) {
//                initalPlaces.add(place);
//            }
//        }
//        // new created token
//        for (Transition t : game.getTransitions()) {
//            List<TokenFlow> tfls = game.getTokenFlow(t);
//            for (TokenFlow tfl : tfls) {
//                if (tfl.getPreset().isEmpty()) {
//                    for (Place post : tfl.getPostset()) {
//                        initalPlaces.add(post);
//                    }
//                }
//            }
//        }
//
//        List<TokenChain> chains = TokenChainGenerator.createTokenChains(game);
//        // merge all 
//        List<TokenTree> trees = new ArrayList<>();
//        for (Place init : initalPlaces) {
//            TokenTree tree = new TokenTree();
//            for (TokenChain c : chains) {
//                if (c.contains(init)) {
//                    tree.addAllPlaces(c);
//                }
//            }
//            trees.add(tree);
//        }
//
//        setTokenTrees(game, trees);
//    }
//
//    @Deprecated
//    private static void addTokenTreesTransitionViewAll(PetriGame game, List<TokenTree> trees
//    ) {
//        List<Transition> visitedTransitions = new ArrayList<>();
//        for (Transition t : game.getTransitions()) {
//            if (!visitedTransitions.contains(t)) {
//                addTokenTrees(game, trees, t, visitedTransitions);
//            }
//        }
//
//    }
//
//    @Deprecated
//    private static void addTokenTrees(PetriGame game, List<TokenTree> trees, Transition t, List<Transition> visitedTransitions
//    ) {
//        visitedTransitions.add(t);
//        List<TokenFlow> tfls = game.getTokenFlow(t);
//        List<TokenTree> newTrees = new ArrayList<>();
//        System.out.println(trees.toString());
//        for (TokenFlow flow : tfls) {
//            for (Place p : flow.getPreset()) { // try add to end
//                boolean added = false;
//                for (TokenTree c : trees) {
//                    if (c.contains(p)) {
//                        added = true;
//                        for (Iterator<Place> iterator = flow.getPostset().iterator(); iterator.hasNext();) {
//                            Place place = iterator.next();
//                            if (!iterator.hasNext()) { // is last one
//                                c.add(place);
//                            } else {
//                                TokenTree nc = new TokenTree(c);
//                                nc.add(place);
//                                newTrees.add(nc);
//                            }
//                        }
//                    }
//                }
//                if (!added) {
//                    for (Place post : flow.getPostset()) { // try add to front
//                        for (TokenTree c : trees) {
//                            if (c.contains(post)) {
//                                added = true;
//                                c.add(p);
////                                for (Iterator<Place> iterator = flow.getPreset().iterator(); iterator.hasNext();) {
////                                    Place place = iterator.next();
////                                    if (!iterator.hasNext()) { // is last one
////                                        c.add(place);
////                                    } else {
////                                        TokenTree nc = new TokenTree(c);
////                                        nc.add(place);
////                                        newTrees.add(nc);
////                                    }
////                                }
//                            }
//                        }
//                    }
//                }
//                if (!added) {
//                    for (Place place : flow.getPostset()) {
//                        TokenTree c = new TokenTree();
//                        c.add(p);
//                        c.add(place);
//                        newTrees.add(c);
//                    }
//                }
//            }
//        }
//        trees.addAll(newTrees);
//
//        for (Place p : t.getPreset()) {
//            for (Transition tran : p.getPreset()) {
//                if (!visitedTransitions.contains(tran)) {
//                    addTokenTrees(game, trees, tran, visitedTransitions);
//                }
//            }
//        }
//
//        for (Place p : t.getPostset()) {
//            for (Transition tran : p.getPostset()) {
//                if (!visitedTransitions.contains(tran)) {
//                    addTokenTrees(game, trees, tran, visitedTransitions);
//                }
//            }
//        }
//
//    }
//
//    /**
//     * Silly idea, places can (and must able to) be in different tokentrees
//     *
//     * @param game
//     * @param trees
//     * @deprecated
//     */
//    @Deprecated
//    private static void addTokenTreesTransitionView(PetriGame game, List<TokenTree> trees
//    ) {
//        for (Transition t : game.getTransitions()) {
//            addTokenTrees(game, trees, t);
//        }
//
//        // merge
//        List<TokenTree> deleteTrees;
//        System.out.println(game.getName());
//        do {
//            System.out.println(trees.toString());
//            deleteTrees = new ArrayList<>();
//            for (int i = 0; i < trees.size(); i++) {
//                TokenTree a = trees.get(i);
//                for (int j = i + 1; j < trees.size(); j++) {
//                    TokenTree b = trees.get(j);
//                    if (a.notEmptyIntersection(b)) {
//                        a.addAll(b);
//                        deleteTrees.add(b);
//                    }
//                }
//            }
//            trees.removeAll(deleteTrees);
//        } while (!deleteTrees.isEmpty());
//
//    }
//
//    @Deprecated
//    private static void addTokenTrees(PetriGame game, List<TokenTree> trees, Transition t
//    ) {
//        List<TokenFlow> tfls = game.getTokenFlow(t);
//        List<TokenTree> newTrees = new ArrayList<>();
//        for (TokenFlow flow : tfls) {
//            for (Place p : flow.getPreset()) { // try add to end
//                boolean added = false;
//                for (TokenTree c : trees) {
//                    if (c.contains(p)) {
//                        added = true;
//                        for (Iterator<Place> iterator = flow.getPostset().iterator(); iterator.hasNext();) {
//                            Place place = iterator.next();
//                            if (!iterator.hasNext()) { // is last one
//                                c.add(place);
//                            } else {
//                                TokenTree nc = new TokenTree(c);
//                                nc.add(place);
//                                newTrees.add(nc);
//                            }
//                        }
//                    }
//                }
//                if (!added) {
//                    for (Place post : flow.getPostset()) { // try add to front
//                        for (TokenTree c : trees) {
//                            if (c.contains(post)) {
//                                added = true;
////                                for (Iterator<Place> iterator = flow.getPreset().iterator(); iterator.hasNext();) {
////                                    Place place = iterator.next();
////                                    if (!iterator.hasNext()) { // is last one
//                                c.add(p);
////                                    } else {
////                                        TokenTree nc = new TokenTree(c);
////                                        nc.add(place);
////                                        newTrees.add(nc);
////                                    }
//                            }
//                        }
//                    }
//                }
//                if (!added) {
//                    for (Place place : flow.getPostset()) {
//                        TokenTree c = new TokenTree();
//                        c.add(p);
//                        c.add(place);
//                        newTrees.add(c);
//                    }
//                }
//            }
//        }
//        trees.addAll(newTrees);
//    }
//
//    @Deprecated
//    private static void addTokenTreesOverCoverabilityGraph(PetriGame game, List<TokenTree> trees
//    ) {
//        // @deprecated for tokenchains
////        List<TokenChain> chains = new ArrayList<>();
////        Marking m = net.getInitialMarking();
////        for (Place place : net.getPlaces()) {
////            if (m.getToken(place).getValue() > 0) {
////                TokenChain c = new TokenChain();
////                c.add(place);
////                chains.add(c);
////            }
////        }
//        Marking m = game.getInitialMarking();
//        for (Place place : game.getPlaces()) {
//            if (m.getToken(place).getValue() > 0) {
//                TokenTree c = new TokenTree();
//                c.add(place);
//                trees.add(c);
//            }
//        }
//
//        //@deprecated for token chains
//////        // Extend chains
////        CoverabilityGraph reach = CoverabilityGraph.get(net);
////        for (CoverabilityGraphEdge edge : reach.getInitialNode().getPostsetEdges()) {
////            addSuccessorChains(chains, edge);
////        }
////        System.out.println(chains.toString());
//        // Create token trees
//        CoverabilityGraph reach = CoverabilityGraph.get(game);
//        List<CoverabilityGraphNode> visited = new ArrayList<>();
//        for (CoverabilityGraphEdge edge : reach.getInitialNode().getPostsetEdges()) {
//            addSuccessorTrees(game, trees, edge, visited);
//        }
//    }
//
//    /**
//     * @param trees
//     * @param edge
//     */
//    @Deprecated
//    private static void addSuccessorTrees(PetriGame game, List<TokenTree> trees, CoverabilityGraphEdge edge, List<CoverabilityGraphNode> visited) {
//        CoverabilityGraphNode prev = edge.getSource();
//        if (visited.contains(prev)) {
//            return;
//        }
//        visited.add(prev);
//        Transition t = edge.getTransition();
//        List<TokenFlow> tfls = game.getTokenFlow(t);
//        List<TokenTree> newTrees = new ArrayList<>();
//        for (TokenFlow flow : tfls) {
//            for (Place p : flow.getPreset()) {
//                boolean added = false;
//                for (TokenTree c : trees) {
//                    if (c.contains(p)) {
//                        added = true;
//                        for (Iterator<Place> iterator = flow.getPostset().iterator(); iterator.hasNext();) {
//                            Place place = iterator.next();
//                            if (!iterator.hasNext()) { // is last one
//                                c.add(place);
//                            } else {
//                                TokenTree nc = new TokenTree(c);
//                                nc.add(place);
//                                newTrees.add(nc);
//                            }
//                        }
//                    }
//                }
//                if (!added) {
//                    for (Place place : flow.getPostset()) {
//                        TokenTree c = new TokenTree();
//                        c.add(p);
//                        c.add(place);
//                        newTrees.add(c);
//                    }
//                }
//            }
//
//        }
//        trees.addAll(newTrees);
//        for (CoverabilityGraphEdge e : edge.getTarget().getPostsetEdges()) {
//            addSuccessorTrees(game, trees, e, visited);
//        }
//    }
//
//    /**
//     * @param trees
//     * @param edge
//     */
//    @Deprecated
//    private static void addSuccessorTreesTargetView(PetriGame game, List<TokenTree> trees, CoverabilityGraphEdge edge, List<CoverabilityGraphNode> visited
//    ) {
//        if (visited.contains(edge.getTarget())) {
//            return;
//        }
//        CoverabilityGraphNode prev = edge.getSource();
//        visited.add(prev);
//        Transition t = edge.getTransition();
//        System.out.println(t.toString());
//        List<TokenFlow> tfls = game.getTokenFlow(t);
//        Set<TokenTree> newTrees = new HashSet<>();
//        for (TokenFlow flow : tfls) {
//            for (Place p : flow.getPreset()) {
//                for (TokenTree c : trees) {
//                    if (c.contains(p)) {
//                        for (Iterator<Place> iterator = flow.getPostset().iterator(); iterator.hasNext();) {
//                            Place place = iterator.next();
//                            if (!iterator.hasNext()) { // is last one
//                                c.add(place);
//                            } else {
//                                TokenTree nc = new TokenTree(c);
//                                nc.add(place);
//                                newTrees.add(nc);
//                            }
//                        }
//                    }
//                }
//            }
//            if (flow.getPreset().isEmpty()) {
//                for (Place place : flow.getPostset()) {
//                    TokenTree nt = new TokenTree();
//                    nt.add(place);
//                    newTrees.add(nt);
//                    System.out.println("drin");
//                }
//            }
//        }
//        trees.addAll(newTrees);
//        for (CoverabilityGraphEdge e : edge.getTarget().getPostsetEdges()) {
//            addSuccessorTrees(game, trees, e, visited);
//        }
//    }
//
//    /**
//     * @deprecated because don't need chain but need trees
//     * @param chains
//     * @param edge
//     */
//    @Deprecated
//    private static void addSuccessorChains(PetriGame game, List<TokenChain> chains, CoverabilityGraphEdge edge
//    ) {
//        Transition t = edge.getTransition();
//        List<TokenFlow> tfls = game.getTokenFlow(t);
//        List<TokenChain> newChains = new ArrayList<>();
//        for (TokenFlow flow : tfls) {
//            for (Place p : flow.getPreset()) {
//                boolean added = false;
//                for (TokenChain c : chains) {
//                    if (c.getLast() == p) {
//                        c.add(t);
//                        added = true;
//                        boolean first = true;
//                        for (Place place : flow.getPostset()) {
//                            if (first) {
//                                c.add(place);
//                                first = false;
//                            } else {
//                                TokenChain nc = new TokenChain(c);
//                                nc.add(place);
//                                newChains.add(nc);
//                            }
//                        }
//                    }
//                }
//                if (!added) {
//                    for (Place place : flow.getPostset()) {
//                        TokenChain c = new TokenChain();
//                        c.add(t);
//                        c.add(place);
//                        newChains.add(c);
//                    }
//                }
//            }
//
//        }
//        chains.addAll(newChains);
//        CoverabilityGraphNode next = edge.getTarget();
//        for (CoverabilityGraphEdge e : next.getPostsetEdges()) {
//            addSuccessorChains(game, chains, e);
//        }
//    }
//
//}
