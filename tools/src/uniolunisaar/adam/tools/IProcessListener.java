package uniolunisaar.adam.tools;

import java.util.EventListener;

/**
 *
 * @author Manuel Gieseking
 */
public interface IProcessListener extends EventListener {

    void processFinished(Process process);
}
