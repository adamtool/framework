package uniolunisaar.adam.ds.petrinetwithtransits;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.Marking;
import uniol.apt.adt.pn.Node;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.analysis.coverability.CoverabilityGraph;
import uniol.apt.io.renderer.RenderException;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.exceptions.pnwt.InconsistencyException;
import uniolunisaar.adam.exceptions.pnwt.NoSuchTransitException;
import uniolunisaar.adam.exceptions.pnwt.NotInitialPlaceException;
import uniolunisaar.adam.util.PNTools;
import uniolunisaar.adam.util.PNWTTools;

/**
 *
 * @author Manuel Gieseking
 */
public class PetriNetWithTransits extends PetriNet {

    private final SortedMap<String, Map<String, Transit>> transits = new TreeMap<>();

    /**
     * Creates a new Petri net with transits with the given name.
     *
     * @param name Name of the Petri net as String.
     */
    public PetriNetWithTransits(String name) {
        super(name);
        PetriNetExtensionHandler.setProcessFamilyID(this, name + Thread.currentThread().getName());
    }

    public PetriNetWithTransits(PetriNet pn) {
        super(pn);
        PetriNetExtensionHandler.setProcessFamilyID(this, pn.getName() + Thread.currentThread().getName());
        // Check if the initial flow places are marked correctly
        // todo: should this still be necessary?
        for (Place p : getPlaces()) {
            if (isInitialTransit(p) && p.getInitialToken().getValue() <= 0) {
                throw new NotInitialPlaceException(p);
            }
        }
        // Initialize for all transition an empty set of tokenflows
        for (Transition t : getTransitions()) {
            transits.put(t.getId(), new HashMap<>());
        }
    }

    /**
     * Copy-Constructor
     *
     * @param pnwt
     */
    public PetriNetWithTransits(PetriNetWithTransits pnwt) {
        super(pnwt);
        // COPY token flows
        for (Map.Entry<String, Map<String, Transit>> entry : pnwt.transits.entrySet()) {
            Map<String, Transit> map = entry.getValue();
            if (map.isEmpty()) {
                transits.put(entry.getKey(), new TreeMap<>());
            }
            for (Transit tfl : map.values()) {
                String[] postset = new String[tfl.getPostset().size()];
                int i = 0;
                for (Place post : tfl.getPostset()) {
                    postset[i] = post.getId();
                    ++i;
                }
                if (tfl.isInitial()) {
                    this.createInitialTransit(tfl.getTransition().getId(), postset);
                } else {
                    this.createTransit(tfl.getPresetPlace().getId(), tfl.getTransition().getId(), postset);
                }
            }
        }
        // the annotations of the objects are already copied by APT
//        for (Transition t : game.getTransitions()) {
////            Collection<TokenFlow> tfls = game.getTransits(t);
////            List<TokenFlow> newtfls = new ArrayList<>();
////            for (Transit tfl : tfls) {
////                if (tfl.isInitial()) {
////                    newtfls.add(createInitialTransit(getTransition(t.getId()), getPlace(tfl.getPostset().iterator().next().getId())));
////                } else {
////                    Set<Place> postset = new HashSet<>();
////                    for (Place post : tfl.getPostset()) {
////                        postset.add(getPlace(post.getId()));
////                    }
////                    newtfls.add(createTransit(getPlace(tfl.getPresetPlace().getId()), getTransition(t.getId()), postset.toArray(new Place[postset.size()])));
////                }
////            }
////            PetriGameExtensionHandler.setTokenFlow(getTransition(t.getId()), newtfls);
//            getTransition(t.getId()).copyExtensions(t);
//        }
//        for (Place p : game.getPlaces()) {
//            getPlace(p.getId()).copyExtensions(p);
//        }
    }

    public void removeNodes(Node... nodes) {
        for (Node node : nodes) {
            super.removeNode(node);
        }
    }

    public void removeNodes(String... ids) {
        for (String id : ids) {
            super.removeNode(id);
        }
    }

    public Node rename(Node n, String newId) {
        if (n == null) {
            throw new IllegalArgumentException("n == null");
        }
        if (newId == null) {
            throw new IllegalArgumentException("newId == null");
        }
        if (containsTransition(n.getId())) {
            // Create a new copy
            Transition newNode = createTransition(newId);
            newNode.copyExtensions(n);
            // copy the edges
            Set<Flow> preset = n.getPresetEdges();
            for (Flow pre : preset) {
                createFlow(pre.getSource().getId(), newNode.getId(), pre.getWeight());
            }
            Set<Flow> postset = n.getPostsetEdges();
            for (Flow post : postset) {
                createFlow(newNode.getId(), post.getTarget().getId(), post.getWeight());
            }
            // copy the tokenflows
            Map<String, Transit> tfls = transits.get(n.getId());
            for (Transit tfl : tfls.values()) {
                Place[] tflPost = tfl.getPostset().toArray(new Place[tfl.getPostset().size()]);
                if (tfl.isInitial()) {
                    createInitialTransit(newNode, tflPost);
                } else {
                    PetriNetWithTransits.this.createTransit(tfl.getPresetPlace(), newNode, tflPost);
                }
            }
            // now delete
            removeTransition(n.getId());
        } else if (containsPlace(n.getId())) {
            Place p = (Place) n;
            // Create a new copy
            Place newNode = createPlace(newId);
            newNode.copyExtensions(n);
            // copy the edges
            Set<Flow> preset = n.getPresetEdges();
            for (Flow pre : preset) {
                createFlow(pre.getSource().getId(), newNode.getId(), pre.getWeight());
            }
            Set<Flow> postset = n.getPostsetEdges();
            for (Flow post : postset) {
                createFlow(newNode.getId(), post.getTarget().getId(), post.getWeight());
            }

            //is initially marked?
            Marking initM = getInitialMarking();
            long val = initM.getToken(n.getId()).getValue();
            if (val > 0) {
                newNode.setInitialToken(val);
            }

            // copy the tokenflows
            // postset flows
            for (Transition t : p.getPostset()) {
                Map<String, Transit> tfls = transits.get(t.getId());
                Transit tfl = tfls.get(p.getId());
                if (tfl != null) {
                    Place[] tflPost = tfl.getPostset().toArray(new Place[tfl.getPostset().size()]);
                    PetriNetWithTransits.this.createTransit(newNode, t, tflPost);
                }
            }
            // preset flows
            for (Transition t : p.getPreset()) {
                Map<String, Transit> tfls = transits.get(t.getId());
                // initial
                Transit init = tfls.get(Transit.INIT_KEY);
                if (init != null) {
                    if (init.getPostset().contains(p)) {
                        init.addPostsetPlace(newNode);
                    }
                }
                // others
                for (Transit tfl : tfls.values()) {
                    if (tfl.getPostset().contains(p)) {
                        tfl.addPostsetPlace(newNode);
                    }
                }
            }

            // now delete
            removePlace(n.getId());
        }
        return n;
    }

    /**
     * Creating a flow to a place 'pre' which already exists adds the postset
     * places 'postset' to this flow.
     *
     * @param preID
     * @param transitionID
     * @param postsetIDs
     * @return
     */
    public Transit createTransit(String preID, String transitionID, String... postsetIDs) {
        if (preID == null) {
            throw new IllegalArgumentException("pre == null");
        }
        if (transitionID == null) {
            throw new IllegalArgumentException("t == null");
        }
        if (postsetIDs == null) {
            throw new IllegalArgumentException("postset == null");
        }
        Place[] postset = new Place[postsetIDs.length];
        for (int i = 0; i < postsetIDs.length; i++) {
            postset[i] = getPlace(postsetIDs[i]);
        }
        return PetriNetWithTransits.this.createTransit(getPlace(preID), getTransition(transitionID), postset);
    }

    /**
     * Creating a flow to a place 'pre' which already exists adds the postset
     * places 'postset' to this flow.
     *
     * @param pre
     * @param t
     * @param postset
     * @return
     */
    public Transit createTransit(Place pre, Transition t, Place... postset) {
        if (pre == null) {
            throw new IllegalArgumentException("pre == null");
        }
        if (t == null) {
            throw new IllegalArgumentException("t == null");
        }
        if (postset == null) {
            throw new IllegalArgumentException("postset == null");
        }
//        if (postset.length == 0) {
//            throw new IllegalArgumentException("post.length == 0");
//        }
        if (!t.getPreset().contains(pre)) {
            throw new InconsistencyException(pre.getId() + " is not in the preset of transition " + t.getId());
        }
        Map<String, Transit> tfls = transits.get(t.getId());
        if (tfls == null) {
            tfls = new HashMap<>();
            transits.put(t.getId(), tfls);
        }
        Set<Place> postSet = new HashSet<>();
        Transit tfl = null;
        if (tfls.containsKey(pre.getId())) {
            tfl = tfls.get(pre.getId());
        }
        for (Place post : postset) {
            if (!t.getPostset().contains(post)) {
                throw new InconsistencyException(post.getId() + " is not in the postset of transition " + t.getId());
            }
            if (tfl != null) {
                tfl.addPostsetPlace(post);
            } else {
                postSet.add(post);
            }
        }
        if (tfl == null) {
            tfl = new Transit(this, pre, t, postSet);
            tfls.put(pre.getId(), tfl);
        }
        // s.th. changed, notify listeners
//        invokeListeners(); // don't want the notify the listeners which listen on the changes of the Petri net, would have to implement an own list
        return tfl;
    }

    public Transit createInitialTransit(Transition t, Place... postset) {
        if (t == null) {
            throw new IllegalArgumentException("t == null");
        }
        if (postset == null) {
            throw new IllegalArgumentException("post == null");
        }
        if (postset.length == 0) {
            throw new IllegalArgumentException("post.length == 0");
        }
        Map<String, Transit> tfls = transits.get(t.getId());
        if (tfls == null) {
            tfls = new HashMap<>();
            transits.put(t.getId(), tfls);
        }
        Set<Place> postSet = new HashSet<>();
        Transit tfl = null;
        if (tfls.containsKey(Transit.INIT_KEY)) {
            tfl = tfls.get(Transit.INIT_KEY);
        }
        for (Place post : postset) {
            if (!t.getPostset().contains(post)) {
                throw new InconsistencyException(post.getId() + " is not in the postset of transition " + t.getId());
            }
            if (tfl != null) {
                tfl.addPostsetPlace(post);
            } else {
                postSet.add(post);
            }
        }
        if (tfl == null) {
            tfl = new Transit(this, t, postSet);
            tfls.put(Transit.INIT_KEY, tfl);
        }
        // s.th. changed, notify listeners
//        invokeListeners(); // don't want the notify the listeners which listen on the changes of the Petri net, would have to implement an own list
        return tfl;
    }

    public Transit createInitialTransit(String transitionID, String... postsetIDs) {
        if (transitionID == null) {
            throw new IllegalArgumentException("t == null");
        }
        if (postsetIDs == null) {
            throw new IllegalArgumentException("postset == null");
        }
        Place[] postset = new Place[postsetIDs.length];
        for (int i = 0; i < postsetIDs.length; i++) {
            postset[i] = getPlace(postsetIDs[i]);
        }
        return createInitialTransit(getTransition(transitionID), postset);
    }

//    public boolean removeTransit(String sourceId, String transitionId, String targetId) {
//        if (sourceId == null) {
//            throw new IllegalArgumentException("sourcetId == null");
//        }
//        if (transitionId == null) {
//            throw new IllegalArgumentException("transitionId == null");
//        }
//        if (targetId == null) {
//            throw new IllegalArgumentException("targetId == null");
//        }
//        if (!containsTransition(transitionId)) {
//            throw new NoSuchNodeException(this, transitionId);
//        }
//        Transit tfls = tokenflows.get(transitionId).get(sourceId);
//        boolean ret = tfls.removePostsetPlace(targetId);
//        if (tfls.getPostset().isEmpty()) {
//            tokenflows.get(transitionId).remove(sourceId);
//        }
//        return ret;
//    }
    public void removeTransit(Flow f) {
        if (f == null) {
            throw new IllegalArgumentException("f == null");
        }
        PetriNetWithTransits.this.removeTransit(f.getSource().getId(), f.getTarget().getId());
    }

    public void removeTransit(Place source, Transition target) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        if (target == null) {
            throw new IllegalArgumentException("target == null");
        }
        PetriNetWithTransits.this.removeTransit(source.getId(), target.getId());
    }

    public void removeTransit(Transition source, Place target) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        if (target == null) {
            throw new IllegalArgumentException("target == null");
        }
        PetriNetWithTransits.this.removeTransit(source.getId(), target.getId());
    }

    public void removeTransit(String sourceId, String targetId) {
        if (sourceId == null) {
            throw new IllegalArgumentException("sourceId == null");
        }
        if (targetId == null) {
            throw new IllegalArgumentException("targetId == null");
        }
        if (!getPostsetNodes(sourceId).contains(getNode(targetId))) {
            throw new InconsistencyException(targetId + " is not in the postset of  " + sourceId);
        }
        if (containsTransition(sourceId) && containsPlace(targetId)) { // transition -> place
            Map<String, Transit> tfls = transits.get(sourceId);
            List<String> del = new ArrayList<>();
            for (Map.Entry<String, Transit> tfl : tfls.entrySet()) {
                tfl.getValue().removePostsetPlace(targetId);
                if (tfl.getValue().isEmpty()) { // no flow is existent anymore
                    del.add(tfl.getKey());
                }
            }
            for (String plID : del) {
                tfls.remove(plID);
            }
        } else if (containsPlace(sourceId) && containsTransition(targetId)) { // place -> transition
            Map<String, Transit> tfls = transits.get(targetId);
            tfls.remove(sourceId);
        } else {
            throw new NoSuchTransitException(this, sourceId, targetId);
        }
        // s.th. changed, notify listeners
//        invokeListeners(); // don't want the notify the listeners which listen on the changes of the Petri net, would have to implement an own list
    }

    public void removeInitialTransit(Transition source, Place target) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        if (target == null) {
            throw new IllegalArgumentException("target == null");
        }
        PetriNetWithTransits.this.removeInitialTransit(source.getId(), target.getId());
    }

    public void removeInitialTransit(String transitionId, String targetId) {
        if (transitionId == null) {
            throw new IllegalArgumentException("transitionId == null");
        }
        if (targetId == null) {
            throw new IllegalArgumentException("targetId == null");
        }
        getTransition(transitionId);
        Transit tf = transits.get(transitionId).get(Transit.INIT_KEY);
        tf.removePostsetPlace(targetId);
        if (tf.getPostset().isEmpty()) {
            transits.get(transitionId).remove(Transit.INIT_KEY);
        }
        // s.th. changed, notify listeners
//        invokeListeners(); // don't want the notify the listeners which listen on the changes of the Petri net, would have to implement an own list
    }

    public Transit getTransit(Transition t, Place preset) {
        return transits.get(t.getId()).get(preset.getId());
    }

    public Collection<Transit> getTransits(Transition t) {
        return Collections.unmodifiableCollection(transits.get(t.getId()).values());
    }

    public Transit getInitialTransit(Transition t) {
        return transits.get(t.getId()).get(Transit.INIT_KEY);
    }

    public boolean hasTransit(Transition t) {
//        Map<String, Transit> tfls = tokenflows.get(t.getId());
//        return tfls != null && !tfls.isEmpty();
        return !transits.get(t.getId()).isEmpty();
    }

    public CoverabilityGraph getReachabilityGraph() {
        return CoverabilityGraph.getReachabilityGraph(this);
    }

    public void checkTransitConsistency(Transit transit) {
        // when one place is env in the preset, then all places in pre and postset must
        // be env.
//        boolean allPreEnv = true;
//        boolean allPreSys = true;
//        for (Place p : preset.values()) {
//            if (PetriGameExtensionHandler.isEnvironment(p)) {
//                allPreSys = false;
//            } else {
//                allPreEnv = false;
//            }
//        }
//        if (!allPreEnv && !allPreSys) {
//            throw new InconsistencyException("You mixed system and enviroment places in the preset of the tokenflow of transition " + t.getId());
//        }
        Place prePlace = transit.getPresetPlace();
        Set<Place> postset = transit.getPostset();
        if (prePlace == null && postset.size() > 1) {
            throw new InconsistencyException("You added more than one postset place to a token flow which is marked as initial. "
                    + "Please use a new token flow object for each newly created flow of transition " + transit.getTransition().getId());
        }
    }

    public String toAPT(boolean withAnnotationPartition, boolean withCoordinates) throws RenderException {
        return PNWTTools.getAPT(this, withAnnotationPartition, withCoordinates);
    }

//    Safe some memory and don't do it automatically for every place
//    /**
//     * Override to automatically add a label to the place
//     * @param id
//     * @return 
//     */
//    @Override
//    public Place createPlace(String id) {
//        Place p = super.createPlace(id);
//        PetriNetExtensionHandler.setLabel(p, id);
//        return p;
//    }
    // Overriden methods to handle tokenflow
    /**
     * The others methods do not have to be overriden since all fall back to
     * this method
     *
     * @param id
     * @return
     */
    @Override
    public Transition createTransition(String id, String label) {
        Transition t = super.createTransition(id, label);
        transits.put(t.getId(), new HashMap<>());
        return t;
    }

    /**
     * The others methods do not have to be overriden since all fall back to
     * this method
     *
     * @param id
     */
    @Override
    public void removeTransition(String id) {
        super.removeTransition(id);
        transits.remove(id);
    }

    /**
     * The others methods do not have to be overriden since all fall back to
     * this method (remove node, etc)
     *
     * @param sourceId
     * @param targetId
     */
    @Override
    public void removeFlow(String sourceId, String targetId) {
        PetriNetWithTransits.this.removeTransit(sourceId, targetId);
        super.removeFlow(sourceId, targetId);
    }

    // Firing with inhibitor arcs
    /**
     * Checks whether the given transition t is fireable in the given marking m.
     * Different to t.isFireable(m) this method also correctly handles inhibitor
     * arcs.
     *
     * @param t - the transition which is checked to be fireable
     * @param m - the marking in which the transition should be fireeable
     * @return true iff t is fireable in m (m[t>) respecting inhibitor arcs.
     */
    public boolean isFireable(Transition t, Marking m) {
        return PNTools.isFireable(t, m);
    }

    /**
     * Fires the given transition t in the given marking m. Different to
     * t.fire(m) this method also correctly handles inhibitor arcs.
     *
     * @param t - the transition which should be fired
     * @param m - the marking in which the transition should be fired
     * @return M' for m[t>M' respecting inhibitor arcs.
     */
    public Marking fire(Transition t, Marking m) {
        return PNTools.fire(t, m);
    }

    // Set extensions  
    // For nodes
    public boolean hasXCoord(Node node) {
        return PetriNetWithTransitsExtensionHandler.hasXCoord(node);
    }

    public double getXCoord(Node node) {
        return PetriNetWithTransitsExtensionHandler.getXCoord(node);
    }

    public void setXCoord(Node node, double id) {
        PetriNetWithTransitsExtensionHandler.setXCoord(node, id);
    }

    public boolean hasYCoord(Node node) {
        return PetriNetWithTransitsExtensionHandler.hasYCoord(node);
    }

    public double getYCoord(Node node) {
        return PetriNetWithTransitsExtensionHandler.getYCoord(node);
    }

    public void setYCoord(Node node, double id) {
        PetriNetWithTransitsExtensionHandler.setYCoord(node, id);
    }

    // For places    
    public boolean isSpecial(Place place) {
        return PetriNetWithTransitsExtensionHandler.isSpecial(place);
    }

    public void setSpecial(Place place, Condition.Objective con) {
        PetriNetWithTransitsExtensionHandler.setSpecial(place, con);
    }

    public void removeSpecial(Place place, Condition.Objective con) {
        PetriNetWithTransitsExtensionHandler.removeSpecial(place, con);
    }

    public boolean isBad(Place place) {
        return PetriNetWithTransitsExtensionHandler.isBad(place);
    }

    public void setBad(Place place) {
        PetriNetWithTransitsExtensionHandler.setBad(place);
    }

    public void removeBad(Place place) {
        PetriNetWithTransitsExtensionHandler.removeBad(place);
    }

    public boolean isReach(Place place) {
        return PetriNetWithTransitsExtensionHandler.isReach(place);
    }

    public void setReach(Place place) {
        PetriNetWithTransitsExtensionHandler.setReach(place);
    }

    public void removeReach(Place place) {
        PetriNetWithTransitsExtensionHandler.removeReach(place);
    }

    public boolean isBuchi(Place place) {
        return PetriNetWithTransitsExtensionHandler.isBuchi(place);
    }

    public void setBuchi(Place place) {
        PetriNetWithTransitsExtensionHandler.setBuchi(place);
    }

    public void removeBuchi(Place place) {
        PetriNetWithTransitsExtensionHandler.removeBuchi(place);
    }

    public boolean isInitialTransit(Place place) {
        return PetriNetWithTransitsExtensionHandler.isInitialTokenflow(place);
    }

    public void setInitialTransit(Place place) {
        if (place.getInitialToken().getValue() <= 0) {
            throw new NotInitialPlaceException(place);
        }
        PetriNetWithTransitsExtensionHandler.setInitialTransit(place);
    }

    public void removeInitialTransit(Place place) {
        PetriNetWithTransitsExtensionHandler.removeInitialTransit(place);
    }

    public int getPartition(Place place) {
        return PetriNetExtensionHandler.getPartition(place);
    }

    public boolean hasPartition(Place place) {
        return PetriNetExtensionHandler.hasPartition(place);
    }

    public void setPartition(Place place, int token) {
        PetriNetExtensionHandler.setPartition(place, token);
    }

    public int getID(Place place) {
        return PetriNetWithTransitsExtensionHandler.getID(place);
    }

    public void setID(Place place, int id) {
        PetriNetWithTransitsExtensionHandler.setID(place, id);
    }

    public String getOrigID(Place place) {
        return PetriNetExtensionHandler.getOrigID(place);
    }

    public void setOrigID(Place place, String id) {
        PetriNetExtensionHandler.setOrigID(place, id);
    }

    // For transitions
    /**
     * Means strong fairness
     *
     * @param t
     * @return
     */
    public boolean isStrongFair(Transition t) {
        return PetriNetWithTransitsExtensionHandler.isStrongFair(t);
    }

    /**
     *
     * Means strong fairness
     *
     * @param t
     */
    public void setStrongFair(Transition t) {
        PetriNetWithTransitsExtensionHandler.setStrongFair(t);
    }

    /**
     * Means strong fairness
     *
     * @param t
     */
    public void removeStrongFair(Transition t) {
        PetriNetWithTransitsExtensionHandler.removeStrongFair(t);
    }

    public boolean isWeakFair(Transition t) {
        return PetriNetWithTransitsExtensionHandler.isWeakFair(t);
    }

    public void setWeakFair(Transition t) {
        PetriNetWithTransitsExtensionHandler.setWeakFair(t);
    }

    public void removeWeakFair(Transition t) {
        PetriNetWithTransitsExtensionHandler.removeWeakFair(t);
    }

    // for flows
    public boolean isInhibitor(Flow f) {
        return PetriNetWithTransitsExtensionHandler.isInhibitor(f);
    }

    public void setInhibitor(Flow f) {
        PetriNetWithTransitsExtensionHandler.setInhibitor(f);
    }

    public void removeInhibitor(Flow f) {
        PetriNetWithTransitsExtensionHandler.removeInhibitor(f);
    }

    // For the net   
//    public void setPartialObservation(PetriGame game, boolean po) {
//        PetriGameExtensionHandler.setPartialObservation(game, po);
//    }
    // Does not make any sense, since most of them are for the nodes and so on
    // the other I could directly add here as attributes.
//    @Override
//    public void removeExtension(String key) {
//        for (AdamExtensions ext : AdamExtensions.values()) {
//            if (ext.name().equals(key)) {
//                throw new RuntimeException("You are not allowed to remove the key: '" + key + "' since it is used by the Petri game itself");
//            }
//        }
//        super.removeExtension(key);
//    }
//
//    @Override
//    public void putExtension(String key, Object value) {
//        for (AdamExtensions ext : AdamExtensions.values()) {
//            if (ext.name().equals(key)) {
//                throw new RuntimeException("You are not allowed to put s.th. to the key: '" + key + "' since it is used by the Petri game itself");
//            }
//        }
//        super.putExtension(key, value);
//    }
//
//    @Override
//    public void putExtension(String key, Object value, ExtensionProperty... properties) {
//        for (AdamExtensions ext : AdamExtensions.values()) {
//            if (ext.name().equals(key)) {
//                throw new RuntimeException("You are not allowed to put s.th. to the key: '" + key + "' since it is used by the Petri game itself");
//            }
//        }
//        super.putExtension(key, value, properties);
//    }
}
