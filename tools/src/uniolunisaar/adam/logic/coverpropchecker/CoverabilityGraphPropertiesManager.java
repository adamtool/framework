package uniolunisaar.adam.logic.coverpropchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uniol.apt.adt.pn.Marking;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.analysis.coverability.CoverabilityGraph;
import uniol.apt.analysis.coverability.CoverabilityGraphNode;
import uniolunisaar.adam.exceptions.CalculationInterruptedException;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.util.ExtensionManagement;

/**
 *
 * @author Manuel Gieseking
 */
public class CoverabilityGraphPropertiesManager {

    private static CoverabilityGraphPropertiesManager instance = null;

    public static CoverabilityGraphPropertiesManager getInstance() {
        if (instance == null) {
            instance = new CoverabilityGraphPropertiesManager();
        }
        return instance;
    }

    private CoverabilityGraphPropertiesManager() {

    }

    private final Map<PetriNet, List<ICoverabilityGraphPropertyUser>> users = new HashMap<>();

    public void register(PetriNet net, ICoverabilityGraphPropertyUser user) {
        List<ICoverabilityGraphPropertyUser> u = users.get(net);
        if (u == null) {
            u = new ArrayList<>();
            users.put(net, u);
        }
        u.add(user);
    }

    public <G extends PetriNet> void calculateProperties(G net) throws CalculationInterruptedException {
        // which checkers have still be calculated
        final List<ICoverabilityGraphPropertyUser> open = new ArrayList<>();
        for (ICoverabilityGraphPropertyUser user : users.get(net)) {
            if (!ExtensionManagement.getInstance().hasExtension(net, user.getKey())) {
                open.add(user);
            }
        }
        // check the reachability graph
        CoverabilityGraph cg = CoverabilityGraph.get(net);
        for (CoverabilityGraphNode node : cg.getNodes()) {
            Marking m = node.getMarking();
            if (Thread.interrupted()) { // let it be interuptable
                CalculationInterruptedException e = new CalculationInterruptedException();
                Logger.getInstance().addError(e.getMessage(), e);
                throw e;
            }
            final List<ICoverabilityGraphPropertyUser> finishedUsers = new ArrayList<>();
            for (ICoverabilityGraphPropertyUser openUser : open) {
                boolean finished = openUser.calculateProperty(net, m);
                if (finished) {
                    finishedUsers.add(openUser);
                }
            }
            open.removeAll(finishedUsers);
            if (open.isEmpty()) {
                break;
            }
        }
        // notify the open calculator of travering all nodes
        for (ICoverabilityGraphPropertyUser openUsers : open) {
            openUsers.checkedAllNodes(net);
        }
    }

}
