package uniolunisaar.adam.tools.processHandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uniolunisaar.adam.tools.Logger;

/**
 *
 * @author Manuel Gieseking
 */
public class ProcessPool implements IProcessListener {

    private static ProcessPool instance = null;
//    private static final Semaphore semaphore = new Semaphore(1);

    public static ProcessPool getInstance() {
        if (instance == null) {
            instance = new ProcessPool();
        }
        return instance;
    }

    private ProcessPool() {
    }

    private final Map<String, ExternalProcessHandler> processes = new HashMap<>();

    public synchronized ExternalProcessHandler putProcess(String key, ExternalProcessHandler value) { //throws InterruptedException {
        value.addListener(this);
//        semaphore.acquire();
        ExternalProcessHandler h = processes.put(key, value);
//        semaphore.release();
        return h;
    }

    public synchronized void clean() { //throws InterruptedException {
//        semaphore.acquire();
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, ExternalProcessHandler> entry : processes.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isAlive()) {
                toRemove.add(entry.getKey());
            }
        }
        for (String key : toRemove) {
            processes.remove(key);
        }
//        semaphore.release();
    }

    public synchronized void destroyProcessesOfNet(String id) { //throws InterruptedException {
//        semaphore.acquire();
        for (Map.Entry<String, ExternalProcessHandler> entry : processes.entrySet()) {
            if (entry.getKey().startsWith(id + "#")) {
                entry.getValue().destroy();
            }
        }
//        semaphore.release();
    }

    public synchronized void destroyProcess(String id) {
        if (processes.containsKey(id)) {
            processes.get(id).destroy();
        } else {
            Logger.getInstance().addWarning("Couldn't find a process with id '" + id + "' to destroy.");
        }
    }

    public synchronized void destroyForciblyProcess(String id) {
        if (processes.containsKey(id)) {
            processes.get(id).destroyForcibly();
        } else {
            Logger.getInstance().addWarning("Couldn't find a process with id '" + id + "' to destroy.");
        }
    }

    public synchronized void destroyForciblyProcessAndChildren(String id) {
        if (processes.containsKey(id)) {
            processes.get(id).destroyForciblyWithChildren();
        } else {
            Logger.getInstance().addWarning("Couldn't find a process with id '" + id + "' to destroy.");
        }
    }

    public synchronized void destroyForciblyProcessesOfNet(String id) throws InterruptedException {
//        semaphore.acquire();
        for (Map.Entry<String, ExternalProcessHandler> entry : processes.entrySet()) {
            if (entry.getKey().startsWith(id + "#")) {
                entry.getValue().destroyForcibly();
            }
        }
//        semaphore.release();
    }

    public synchronized void destroyForciblyProcessesAndChildrenOfNet(String id) throws InterruptedException {
//        semaphore.acquire();
        for (Map.Entry<String, ExternalProcessHandler> entry : processes.entrySet()) {
            if (entry.getKey().startsWith(id + "#")) {
                entry.getValue().destroyForciblyWithChildren();
            }
        }
//        semaphore.release();
    }

    @Override
    public void processFinished(Process process) {
//        try {
        clean();
//        } catch (InterruptedException ex) {
//        }
    }
}
