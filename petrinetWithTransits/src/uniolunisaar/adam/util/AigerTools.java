package uniolunisaar.adam.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.exceptions.ExternalToolException;
import uniolunisaar.adam.exceptions.ProcessNotStartedException;
import uniolunisaar.adam.logic.externaltools.pnwt.AigToDot;
import uniolunisaar.adam.logic.transformers.pn2aiger.AigerRenderer;
import uniolunisaar.adam.tools.AdamProperties;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.processHandling.ExternalProcessHandler;
import uniolunisaar.adam.tools.processHandling.ProcessPool;

/**
 *
 * @author Manuel Gieseking
 */
public class AigerTools {

    public static void save2Aiger(PetriNet net, AigerRenderer renderer, String path) throws FileNotFoundException {
        String aigerFile = renderer.render(net).toString();
        // save aiger file
        try (PrintStream out = new PrintStream(path + ".aag")) {
            out.println(aigerFile);
        }
    }

    public static void saveAiger2Dot(String input, String output, String procFamilyID) throws ExternalToolException, IOException, InterruptedException {
        AigToDot.call(input, output, procFamilyID);
    }

    public static Thread saveAiger2DotAndPDF(String input, String output, String procFamilyID) throws IOException, InterruptedException, ExternalToolException {
        saveAiger2Dot(input, output, procFamilyID);
        String dot = AdamProperties.getInstance().getProperty(AdamProperties.DOT);
        String[] command = {dot, "-Tpdf", output + ".dot", "-o", output + ".pdf"};
        // Mac:
        // String[] command = {"/usr/local/bin/dot", "-Tpdf", output + ".dot", "-o", output + ".pdf"};
        ExternalProcessHandler procH = new ExternalProcessHandler(true, command);
        ProcessPool.getInstance().putProcess(procFamilyID + "#dot2pdf", procH);
        // start it in an extra thread
        Thread thread = new Thread(() -> {
            try {
                procH.startAndWaitFor();
//                    if (deleteDot) {
//                        // Delete dot file
//                        new File(path + ".dot").delete();
//                        Logger.getInstance().addMessage("Deleted: " + path + ".dot", true);
//                    }
            } catch (IOException | InterruptedException ex) {
                String errors = "";
                try {
                    errors = procH.getErrors();
                } catch (ProcessNotStartedException e) {
                }
                Logger.getInstance().addError("Saving pdf from dot failed.\n" + errors, ex);
            }
        });
        thread.start();
        return thread;
    }

    public static Thread saveAiger2PDF(String input, String output, String procFamiliyID) throws IOException, InterruptedException, ExternalToolException {
        String bufferpath = output + "_" + System.currentTimeMillis();
        Thread dot = saveAiger2DotAndPDF(input, bufferpath, procFamiliyID);
        Thread mvPdf = new Thread(() -> {
            try {
                dot.join();
                // Delete dot file
                new File(bufferpath + ".dot").delete();
                Logger.getInstance().addMessage("Deleted: " + bufferpath + ".dot", true);
                // move to original name
                Files.move(new File(bufferpath + ".pdf").toPath(), new File(output + ".pdf").toPath(), REPLACE_EXISTING);
                Logger.getInstance().addMessage("Moved: " + bufferpath + ".pdf --> " + output + ".pdf", true);
            } catch (IOException | InterruptedException ex) {
                Logger.getInstance().addError("Deleting the buffer files and moving the pdf failed", ex);
            }
        });
        mvPdf.start();
        return mvPdf;
    }
}
