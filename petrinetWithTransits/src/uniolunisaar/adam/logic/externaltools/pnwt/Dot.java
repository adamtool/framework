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
 * A class for calling the external tool dot for rendering dot files to pdf
 *
 * @author Manuel Gieseking
 */
public class Dot {

    public static final String LOGGER_DOT_OUT = "dotOut";
    public static final String LOGGER_DOT_ERR = "dotErr";

    /**
     *
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
        String dot = AdamProperties.getInstance().getProperty(AdamProperties.DOT);
        String[] command = {dot, "-Tpdf", inputFile, "-o", output + ".pdf"};
        // Mac:
        //String[] command = {"/usr/local/bin/dot", "-Tpdf", path + ".dot", "-o", path + ".pdf"};         
        Logger.getInstance().addMessage("Calling dot ...", true);
        Logger.getInstance().addMessage(Arrays.toString(command), true);

        ExternalProcessHandler procDot = new ExternalProcessHandler(command);
        ProcessPool.getInstance().putProcess(procFamilyID + "#dot", procDot);

        PrintStream out = Logger.getInstance().getMessageStream(LOGGER_DOT_OUT);
        PrintStream err = Logger.getInstance().getMessageStream(LOGGER_DOT_ERR);
        PrintWriter outStream = null;
        if (out != null) {
            outStream = new PrintWriter(out, true);
        }
        PrintWriter errStream = null;
        if (err != null) {
            errStream = new PrintWriter(err, true);
        }
        int exitValue = procDot.startAndWaitFor(outStream, errStream);
        if (!verbose) { // cleanup
            Tools.deleteFile(inputFile);
        }
        if (exitValue != 0) {
            throw new ExternalToolException("Dot didn't finsh correctly. 'dot' couldn't produce an PDF file from '" + inputFile + "'");
        }
        Logger.getInstance().addMessage("... finished calling Dot.", true);
        Logger.getInstance().addMessage("", true);
    }
}
