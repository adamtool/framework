package uniolunisaar.adam.tools;

import java.util.HashMap;
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
        for (Map.Entry<String, ExternalProcessHandler> entry : processes.entrySet()) {
            if (!entry.getValue().isAlive()) {
                processes.remove(entry.getKey(), entry.getValue());
            }
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
