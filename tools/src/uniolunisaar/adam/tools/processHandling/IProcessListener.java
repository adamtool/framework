package uniolunisaar.adam.tools.processHandling;

import java.util.EventListener;

/**
 *
 * @author Manuel Gieseking
 */
public interface IProcessListener extends EventListener {

    void processFinished(Process process);
}
