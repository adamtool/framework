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

/**
 *
 * @author Manuel Gieseking
 */
public class AigToDot {

    public static final String LOGGER_AIGER_DOT_OUT = "aigerDotOut";
    public static final String LOGGER_AIGER_DOT_ERR = "aigerDotErr";

    public static void call(String inputFile, String output, String procFamilyID) throws IOException, InterruptedException, ExternalToolException {
        String[] aiger_command = {AdamProperties.getInstance().getProperty(AdamProperties.AIGER_TOOLS) + "aigtodot", inputFile, output + ".dot"};

        Logger.getInstance().addMessage("", false);
        Logger.getInstance().addMessage("Calling Aiger ...", false);
        Logger.getInstance().addMessage(Arrays.toString(aiger_command), true);
        ExternalProcessHandler procAiger = new ExternalProcessHandler(aiger_command);
        ProcessPool.getInstance().putProcess(procFamilyID + "#aig2dot", procAiger);
        PrintStream out = Logger.getInstance().getMessageStream(LOGGER_AIGER_DOT_OUT);
        PrintStream err = Logger.getInstance().getMessageStream(LOGGER_AIGER_DOT_ERR);
        PrintWriter outStream = null;
        if (out != null) {
            outStream = new PrintWriter(out, true);
        }
        PrintWriter errStream = null;
        if (err != null) {
            errStream = new PrintWriter(err, true);
        }
        int exitValue = procAiger.startAndWaitFor(outStream, errStream);
        if (exitValue != 0) {
            throw new ExternalToolException("Aigertools didn't finshed correctly. 'aigtodot' couldn't produce an 'dot'-file from '" + inputFile + "'");
        }
        Logger.getInstance().addMessage("... finished calling Aiger.", false);
        Logger.getInstance().addMessage("", false);
    }
}
