package uniolunisaar.adam.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Manuel Gieseking
 */
public class ProcessPool implements IProcessListener {

    private static ProcessPool instance = null;

    public static ProcessPool getInstance() {
        if (instance == null) {
            instance = new ProcessPool();
        }
        return instance;
    }

    private ProcessPool() {
    }

    private final Map<String, ExternalProcessHandler> processes = new HashMap<>();

    public ExternalProcessHandler putProcess(String key, ExternalProcessHandler value) {
        value.addListener(this);
        return processes.put(key, value);
    }

    public void clean() {
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, ExternalProcessHandler> entry : processes.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isAlive()) {
                toRemove.add(entry.getKey());
            }
        }
        for (String key : toRemove) {
            processes.remove(key);
        }
    }

    public void destroyProcessesOfNet(String id) {
        for (Map.Entry<String, ExternalProcessHandler> entry : processes.entrySet()) {
            if (entry.getKey().startsWith(id + "#")) {
                entry.getValue().destroy();
            }
        }
    }

    public void destroyForciblyProcessesOfNet(String id) {
        for (Map.Entry<String, ExternalProcessHandler> entry : processes.entrySet()) {
            if (entry.getKey().startsWith(id + "#")) {
                entry.getValue().destroyForcibly();
            }
        }
    }

    @Override
    public void processFinished(Process process) {
        clean();
    }
}
