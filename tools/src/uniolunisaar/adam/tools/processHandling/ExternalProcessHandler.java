package uniolunisaar.adam.tools.processHandling;

import uniolunisaar.adam.exceptions.ProcessNotStartedException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Using the idea of
 * https://thilosdevblog.wordpress.com/2011/11/21/proper-handling-of-the-processbuilder/
 *
 * @author Manuel Gieseking
 */
public class ExternalProcessHandler {

    private boolean buffer;
    private StringWriter procOutputStream = null;
    private StringWriter procErrorStream = null;
    private OutputStream procInput = null;
    private String[] command;
    private File directory;
    private int status = -1;

    private ProcessOutputReaderThread out;
    private ProcessOutputReaderThread error;
    private Process proc;

    private List<IProcessListener> listeners = new ArrayList<>();

    public ExternalProcessHandler(String... command) {
        this(null, command);
    }

    public ExternalProcessHandler(boolean buffer, String... command) {
        this(null, buffer, command);
    }

    public ExternalProcessHandler(File directory, String... command) {
        this(directory, false, command);
    }

    public ExternalProcessHandler(File directory, boolean buffer, String... command) {
        this.command = command;
        this.directory = directory;
        this.buffer = buffer;
        if (buffer) {
            this.procOutputStream = new StringWriter();
            this.procErrorStream = new StringWriter();
        }
    }

    /**
     * Start with omitting the output.
     *
     * @throws IOException
     */
    public void start() throws IOException {
        start(null, null);
    }

    /**
     * Start with sending the error and output to the given streams. When they
     * are null, the output is omitted.
     *
     * @param outputStream
     * @param errorStream
     * @throws IOException
     */
    public void start(PrintWriter outputStream, PrintWriter errorStream) throws IOException {
        ProcessBuilder procBuilder = new ProcessBuilder(command);
        // if we want to start the proc from a different directory
        if (directory != null) {
            procBuilder.directory(directory);
        }
        proc = procBuilder.start();
        procInput = proc.getOutputStream();
        if (buffer) {
            out = new ProcessOutputReaderThread(proc.getInputStream(), new PrintWriter(procOutputStream, true), outputStream);
            error = new ProcessOutputReaderThread(proc.getErrorStream(), new PrintWriter(procErrorStream, true), errorStream);
        } else {
            out = new ProcessOutputReaderThread(proc.getInputStream(), outputStream);
            error = new ProcessOutputReaderThread(proc.getErrorStream(), errorStream);
        }
        out.start();
        error.start();
    }

    public int waitFor() throws InterruptedException {
        status = proc.waitFor();
        out.join();
        error.join();
        for (IProcessListener listener : listeners) {
            listener.processFinished(proc);
        }
        return status;
    }

    public int startAndWaitFor() throws IOException, InterruptedException {
        return startAndWaitFor(null, null);
    }

    /**
     * Start with sending the error and output to the given streams. When they
     * are null, the output is omitted.
     *
     * @param outputStream
     * @param errorStream
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public int startAndWaitFor(PrintWriter outputStream, PrintWriter errorStream) throws IOException, InterruptedException {
        start(outputStream, errorStream);
        waitFor();
        return status;
    }

    public boolean isAlive() {
        return proc != null && proc.isAlive();
    }

    public void destroy() {
        proc.destroy();
    }

    public Process destroyForcibly() {
        return proc.destroyForcibly();
    }

    public Process destroyForciblyWithChildren() {
        // todo: check why this doesn't work
//        destroyForciblyAllChildrenRecursively(proc.toHandle());
        for (Iterator<ProcessHandle> iterator = proc.children().iterator(); iterator.hasNext();) {
            ProcessHandle childProc = iterator.next();
            childProc.destroyForcibly();
        }
        return proc.destroyForcibly();
    }

    private void destroyForciblyAllChildrenRecursively(ProcessHandle child) {
        for (Iterator<ProcessHandle> iterator = child.children().iterator(); iterator.hasNext();) {
            ProcessHandle childProc = iterator.next();
            destroyForciblyAllChildrenRecursively(childProc);
            childProc.destroyForcibly();
        }
    }

    public String getErrors() throws ProcessNotStartedException {
        if (procErrorStream == null) {
            throw new ProcessNotStartedException("Process has not been started yet or you decided to not buffer the stream. Error stream is null.");
        }
        return procErrorStream.toString();
    }

    public String getOutput() throws ProcessNotStartedException {
        if (procOutputStream == null) {
            throw new ProcessNotStartedException("Process has not been started yet or you decided to not buffer the stream. Output stream is null.");
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

    public boolean addListener(IProcessListener listener) {
        return listeners.add(listener);
    }

    public boolean removeListener(IProcessListener listener) {
        return listeners.remove(listener);
    }

    //todo: when we have java9 take ProcessHandle to get the CPU time of the process and so on.
    class ProcessOutputReaderThread extends Thread {

        private final InputStream is;
        private final PrintWriter out;
        private final PrintWriter buffer;

        ProcessOutputReaderThread(InputStream is, PrintWriter buffer, PrintWriter out) {
            this.is = is;
            this.buffer = buffer;
            this.out = out;
        }

        ProcessOutputReaderThread(InputStream is, PrintWriter out) {
            this.is = is;
            this.out = out;
            this.buffer = null;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // send it to the given output
                    if (out != null) {
                        out.println(line);
                    }
                    // also buffer it here if we want to get back to the ouput
                    if (buffer != null) {
                        buffer.println(line);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException("Buffering the output of '" + Arrays.toString(command) + "'failed. This should not happen.", ex);
            }
        }
    }
}
