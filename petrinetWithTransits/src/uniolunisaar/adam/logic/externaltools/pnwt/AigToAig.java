package uniolunisaar.adam.logic.externaltools.pnwt;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import uniolunisaar.adam.exceptions.ExternalToolException;
import uniolunisaar.adam.tools.AdamProperties;
import uniolunisaar.adam.tools.processHandling.ExternalProcessHandler;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.processHandling.ProcessPool;
import uniolunisaar.adam.tools.Tools;

/**
 * A class for calling the external tool aigtoaig from the Aiger tools:
 * http://fmv.jku.at/aiger/
 * 
 * @author Manuel Gieseking
 */
public class AigToAig {

    public static final String LOGGER_AIGER_OUT = "aigerOut";
    public static final String LOGGER_AIGER_ERR = "aigerErr";

    /**
     * Creates an .aig file from an .aag file with the external aigertool aigtoaig.
     * 
     * @param inputFile
     * @param output
     * @param verbose
     * @param procFamilyID
     * @throws IOException
     * @throws InterruptedException
     * @throws ExternalToolException 
     */
    public static void call(String inputFile, String output, boolean verbose, String procFamilyID) throws IOException, InterruptedException, ExternalToolException {
        String[] aiger_command = {AdamProperties.getInstance().getProperty(AdamProperties.AIGER_TOOLS) + "aigtoaig", inputFile, output};

        Logger.getInstance().addMessage("", false);
        Logger.getInstance().addMessage("Calling Aiger ...", false);
        Logger.getInstance().addMessage(Arrays.toString(aiger_command), true);
        ExternalProcessHandler procAiger = new ExternalProcessHandler(aiger_command);
        ProcessPool.getInstance().putProcess(procFamilyID + "#aiger", procAiger);
        PrintStream out = Logger.getInstance().getMessageStream(LOGGER_AIGER_OUT);
        PrintStream err = Logger.getInstance().getMessageStream(LOGGER_AIGER_ERR);
        PrintWriter outStream = null;
        if (out != null) {
            outStream = new PrintWriter(out, true);
        }
        PrintWriter errStream = null;
        if (err != null) {
            errStream = new PrintWriter(err, true);
        }
        int exitValue = procAiger.startAndWaitFor(outStream, errStream);
        if (!verbose) { // cleanup
            Tools.deleteFile(inputFile);
        }
        if (exitValue != 0) {
            throw new ExternalToolException("Aigertools didn't finish correctly. 'aigtoaig' couldn't produce an 'aig'-file from '" + inputFile + "'");
        }
        Logger.getInstance().addMessage("... finished calling Aiger.", false);
        Logger.getInstance().addMessage("", false);
    }
}
