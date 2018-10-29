package uniolunisaar.adam.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Using the idea of
 * https://thilosdevblog.wordpress.com/2011/11/21/proper-handling-of-the-processbuilder/
 *
 * @author Manuel Gieseking
 */
public class ExternalProcessHandler {

    private StringWriter procOutputStream = null;
    private StringWriter procErrorStream = null;
    private OutputStream procInput = null;
    private String[] command;
    private File directory;
    private int status;

    private ProcessOutputReaderThread out;
    private ProcessOutputReaderThread error;
    private Process proc;

    public ExternalProcessHandler(String... command) {
        this(null, command);
    }

    public ExternalProcessHandler(File directory, String... command) {
        this.command = command;
        this.directory = directory;
        this.procOutputStream = new StringWriter();
        this.procErrorStream = new StringWriter();
    }

    public void start(boolean verbose) throws IOException {
        ProcessBuilder procBuilder = new ProcessBuilder(command);
        // if we want to start the proc from a different directory
        if (directory != null) {
            procBuilder.directory(directory);
        }
        proc = procBuilder.start();
        procInput = proc.getOutputStream();
        out = new ProcessOutputReaderThread(proc.getInputStream(), new PrintWriter(procOutputStream, true), verbose);
        error = new ProcessOutputReaderThread(proc.getErrorStream(), new PrintWriter(procErrorStream, true), verbose);
        out.start();
        error.start();
    }

    public int waitFor() throws InterruptedException {
        status = proc.waitFor();
        out.join();
        error.join();
        return status;
    }

    public int startAndWaitFor(boolean verbose) throws IOException, InterruptedException {
        ProcessBuilder procBuilder = new ProcessBuilder(command);
        // if we want to start the proc from a different directory
        if (directory != null) {
            procBuilder.directory(directory);
        }
        proc = procBuilder.start();
        procInput = proc.getOutputStream();
        out = new ProcessOutputReaderThread(proc.getInputStream(), new PrintWriter(procOutputStream, true), verbose);
        error = new ProcessOutputReaderThread(proc.getErrorStream(), new PrintWriter(procErrorStream, true), verbose);

        out.start();
        error.start();
        status = proc.waitFor();
        out.join();
        error.join();
        return status;
    }

    public String getErrors() throws ProcessNotStartedException {
        if (procErrorStream == null) {
            throw new ProcessNotStartedException("Process has not been started yet. Error stream is null");
        }
        return procErrorStream.toString();
    }

    public String getOutput() throws ProcessNotStartedException {
        if (procOutputStream == null) {
            throw new ProcessNotStartedException("Process has not been started yet. Output stream is null");
        }
        return procOutputStream.toString();
    }

    public OutputStream getProcessInput() throws ProcessNotStartedException {
        if (procInput == null) {
            throw new ProcessNotStartedException("Process has not been started yet. Cannot get the stream to put the input for the process in.");
        }
        return procInput;
    }

    public int getStatus() {
        return status;
    }

    class ProcessOutputReaderThread extends Thread {

        private final InputStream is;
        private final PrintWriter buffer;
        private final boolean verbose;

        ProcessOutputReaderThread(InputStream is, PrintWriter buffer, boolean verbose) {
            this.is = is;
            this.verbose = verbose;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // send it to the logger
                    Logger.getInstance().addMessage(line, verbose);
                    // also buffer it here if we want to get back to the ouput
                    buffer.println(line);
                }
            } catch (IOException ex) {
                throw new RuntimeException("Buffering the output of '" + Arrays.toString(command) + "'failed. This should not happen.", ex);
            }
        }
    }
}
