package uniolunisaar.adam.logic.transits;

//package uniolunisaar.adam.logic.tokenflow;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import uniol.apt.adt.pn.Marking;
//import uniol.apt.adt.pn.Place;
//import uniol.apt.adt.pn.Transition;
//import uniolunisaar.adam.ds.petrigame.PetriGame;
//import uniolunisaar.adam.ds.petrigame.TokenChain;
//import uniolunisaar.adam.ds.petrigame.TokenFlow;
//import uniolunisaar.adam.ds.util.AdamExtensions;
//
///**
// *
// * @author Manuel Gieseking
// */
//@Deprecated
//public class TokenChainGenerator {
//    // Next methods only moved from game, since this class is not needed anymore
//    private static boolean hasTokenTrees(PetriGame game) {
//        return game.hasExtension("tokentrees");
//    }
//    
//    public static List<TokenChain> getTokenChains(PetriGame game) {
//        return (List<TokenChain>) game.getExtension("tokenchains");
//    }
//
//    public static boolean hasTokenChains(PetriGame game) {
//        return game.hasExtension("tokenchains");
//    }
//
//    private static void setTokenChains(PetriGame game, List<TokenChain> chains) {
//        game.putExtension("tokenchains", chains);
//    }
//    
//    public static void createAndAnnotateTokenChains(PetriGame game) {
//        if (hasTokenTrees(game)) {
//            return;
//        }
//
//        setTokenChains(game, createTokenChains(game));
//    }
//
//    public static List<TokenChain> createTokenChains(PetriGame game) {
//        List<TokenChain> chains = new ArrayList<>();
//        // Add chains for initial token
//        Marking m = game.getInitialMarking();
//        for (Place place : game.getPlaces()) {
//            if (m.getToken(place).getValue() > 0) {
//                TokenChain c = new TokenChain();
//                c.addLast(place);
//                chains.add(c);
//            }
//        }
//        // Add chain for new created token
//        for (Transition t : game.getTransitions()) {
//            List<TokenFlow> tfls = game.getTokenFlow(t);
//            for (TokenFlow tfl : tfls) {
//                if (tfl.getPreset().isEmpty()) {
//                    for (Place post : tfl.getPostset()) {
//                        TokenChain c = new TokenChain();
//                        c.addLast(post);
//                        chains.add(c);
//                    }
//                }
//            }
//        }
//
//        boolean added = true;
//        while (added) {
//            added = false;
//            List<TokenChain> newChains = new ArrayList<>();
//            for (TokenChain chain : chains) {
//                if (!chain.isFinished()) {
//                    TokenChain chainToCopy = new TokenChain(chain);
//                    Place last = (Place) chain.getLast();
//                    for (Transition t : last.getPostset()) {
//                        if (chain.getLast().equals(last) && !chain.isFinished()) { // not already extended this chain
//                            added = addTokenFlows(game, t, last, chain, newChains);
//                        } else {
//                            TokenChain nc = new TokenChain(chainToCopy);
//                            added = addTokenFlows(game, t, last, nc, newChains);
//                            newChains.add(nc);
//                        }
//                    }
//                }
//            }
//            chains.addAll(newChains);
//        }
//        return chains;
//    }
//
//    private static boolean addTokenFlows(PetriGame game, Transition t, Place last, TokenChain chain, List<TokenChain> newChains) {
//        boolean added = false;
//        List<TokenFlow> tfls = game.getTokenFlow(t);
//        for (TokenFlow tfl : tfls) {
//            if (tfl.getPreset().contains(last)) {
//                if (!chain.contains(t)) {
//                    for (Iterator<Place> iterator = tfl.getPostset().iterator(); iterator.hasNext();) {
//                        Place post = iterator.next();
//                        if (!iterator.hasNext()) { // is last one
//                            chain.addLast(t);
//                            chain.addLast(post);
//                        } else {
//                            TokenChain nc = new TokenChain(chain);
//                            nc.addLast(t);
//                            nc.addLast(post);
//                            newChains.add(nc);
//                        }
//                    }
//                    if (tfl.getPostset().isEmpty()) { // token has died
//                        chain.setFinished(true);
//                    } else {
//                        added = true;
//                    }
//                }
//            }
//        }
//        return added;
//    }
//}
