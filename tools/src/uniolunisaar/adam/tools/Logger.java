package uniolunisaar.adam.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Manuel Gieseking
 */
public class Logger {

    public enum OUTPUT {
        NONE,
        STREAMS,
        FILE,
        STREAMS_AND_FILE,
        CLIENT
    }
    private static final Map<ThreadGroup, Logger> instances = new HashMap<>();
//    private static final Semaphore semaphore = new Semaphore(1); // for changing the instances map

    private Logger() {
        this.silent = false;
        this.output = OUTPUT.STREAMS;
        this.path = null;
        this.file = null;
        this.systemOutput = System.out;
        this.shortMessageStream = System.out;
        this.verboseMessageStream = emptyStream;
        this.errorStream = System.err;
        this.warningStream = System.out;
    }

    /**
     * Returns a Logger for the ThreadGroup of the current thread.
     *
     * @return
     */
    public synchronized static Logger getInstance() {
        ThreadGroup cThreadGroup = Thread.currentThread().getThreadGroup();
//        try {
//            semaphore.acquire();
        if (!instances.containsKey(cThreadGroup)) {
            instances.put(cThreadGroup, new Logger());
            cleanLoggerList();
        }
        return instances.get(cThreadGroup);
//            Logger logger = instances.get(cThreadGroup);
//            semaphore.release();
//            return logger;
//        } catch (InterruptedException e) {
//            throw new AcquiringInterruptedException(e);
//        }
    }

    private static void cleanLoggerList() {
//            List<Thread> toRemove = new ArrayList<>();
//            for (Thread thread : instances.keySet()) {
//                if (!Thread.getAllStackTraces().keySet().contains(thread)) {
//                    toRemove.add(thread);
//                }
//            }
//            for (Thread thread : toRemove) {
//                instances.remove(thread);
//            }
        List<ThreadGroup> toRemove = new ArrayList<>();
        for (ThreadGroup threadGroup : instances.keySet()) {
            if (threadGroup.activeCount() <= 0) {
                toRemove.add(threadGroup);
            }
        }
        for (ThreadGroup threadGroup : toRemove) {
            instances.remove(threadGroup);
        }
    }

    private PrintStream errorStream;
    private PrintStream warningStream;
    private PrintStream shortMessageStream;
    private PrintStream verboseMessageStream;
    private final Map<String, PrintStream> messageStreams = new HashMap<>();
    private final PrintStream emptyStream = new PrintStream(new OutputStream() {
        @Override
        public void write(int arg0) throws IOException {
            // keep empty
        }
    });

    private boolean silent;
    private OUTPUT output;
    private String path;
    private PrintWriter file;
    // For server-client-communication
    private ObjectOutputStream writer;
    private Object flag;
    private final PrintStream systemOutput;

    /**
     * Adds a message to the verbose message stream.
     *
     * @param msg
     */
    public void addMessage(String msg) {
        addMessage(msg, true, false);
    }

    /**
     * When force is true, these messages will be shown not matter of the status
     * of the silent flag.
     *
     * @param force
     * @param msg
     */
    public void addMessage(boolean force, String msg) {
        addMessage(msg, false, force);
    }

    /**
     * If verbose is false this message will be sent to the standard message
     * stream 'shortmessages'.
     *
     * @param msg
     * @param verbose
     */
    public void addMessage(String msg, boolean verbose) {
        addMessage(msg, verbose, false);
    }

    /**
     * Adds the message to all the given streams, When the stream don't exists
     * it is done nothing.
     *
     * @param msg
     * @param streams
     */
    public synchronized void addMessage(String msg, String... streams) {
        for (String stream : streams) {
            PrintStream s = messageStreams.get(stream);
            if (s != null) {
                s.println(msg);
                s.flush();
            }
        }
    }

    /**
     * If verbose is false this message will be sent to the standard message
     * stream 'shortmessages'.
     *
     * When forced is true, these messages will be shown not matter of the
     * status of the silent flag.
     *
     *
     * @param msg
     * @param verbose
     * @param forced
     */
    public synchronized void addMessage(String msg, boolean verbose, boolean forced) {
        if (silent && !forced) {
            return;
        }
        if (output == OUTPUT.STREAMS || output == OUTPUT.STREAMS_AND_FILE) {
            boolean bufSilent = silent;
            if (forced && silent) {
                setSilent(false);
            }
//            if (verbose) {
            verboseMessageStream.println(msg);
//            } else {
            if (!verbose) {
                shortMessageStream.println(msg);
            }
//            }
            if (forced && bufSilent) {
                setSilent(true);
            }
        }
        if (output == OUTPUT.FILE || output == OUTPUT.STREAMS_AND_FILE) {
            file.append(msg).append(System.lineSeparator());
            file.flush();
        }
        if (output == OUTPUT.CLIENT) {
            try {
                writer.writeObject(flag);
                writer.writeBoolean(verbose);
                writer.writeObject("[SERVER] " + msg);
            } catch (IOException ex) {
                System.err.println("Could not send message.");
            }
        }
    }

    public synchronized void addError(String msg, Exception e) {
        msg = "[ERROR] " + msg;
        if (output != OUTPUT.CLIENT) {
            errorStream.println(msg);
            msg = "\n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace());
            verboseMessageStream.println(msg);
        }
        if (output == OUTPUT.FILE || output == OUTPUT.STREAMS_AND_FILE) {
            file.append(msg).append(System.lineSeparator());
            file.flush();
        }
        if (output == OUTPUT.CLIENT) {
            try {
                writer.writeObject(flag);
                writer.writeObject("[SERVER] " + msg);
            } catch (IOException ex) {
                System.err.println("Could not send message.");
            }
        }
    }

    public synchronized void addWarning(String msg) {
        msg = "[WARNING] " + msg;
        if (output != OUTPUT.CLIENT) {
            warningStream.println(msg);
            verboseMessageStream.println(msg);
        }
        if (output == OUTPUT.FILE || output == OUTPUT.STREAMS_AND_FILE) {
            file.append(msg).append(System.lineSeparator());
            file.flush();
        }
        if (output == OUTPUT.CLIENT) {
            try {
                writer.writeObject(flag);
                writer.writeObject("[SERVER] " + msg);
            } catch (IOException ex) {
                System.err.println("Could not send message.");
            }
        }
    }

    public synchronized void setOutput(OUTPUT output) {
        if (path == null && (output == OUTPUT.FILE || output == OUTPUT.STREAMS_AND_FILE)) {
            System.err.println("Error: no path set.");
        }
        if ((flag == null || writer == null) & output == OUTPUT.CLIENT) {
            System.err.println("Error: flag and writer must be set for sending to a client.");
        } else {
            this.output = output;
        }
    }

    public synchronized void resetFile() throws FileNotFoundException, UnsupportedEncodingException {
        if (path != null) {
            file = new PrintWriter(path, "UTF-8");
        }
    }

    public synchronized void setPath(String path) throws FileNotFoundException, UnsupportedEncodingException {
        this.path = path;
        this.file = new PrintWriter(path, "UTF-8");
    }

    public synchronized void setVerbose(boolean verbose) {
        if (verbose) {
            verboseMessageStream = System.out;
            shortMessageStream = emptyStream;
        } else {
            verboseMessageStream = emptyStream;
            shortMessageStream = System.out;
        }
    }

    public synchronized void setSilent(boolean silent) {
        this.silent = silent;
        if (silent) {
            System.setOut(emptyStream);
        } else {
            System.setOut(systemOutput);
        }
    }

    public synchronized void close() {
        if (file != null) {
            file.close();
        }
    }

    public synchronized void set2Client(ObjectOutputStream writer, Object flag) {
        this.output = OUTPUT.CLIENT;
        this.writer = writer;
        this.flag = flag;
    }

    public synchronized void addMessageStream(String key, PrintStream messsageStream) {
        this.messageStreams.put(key, messsageStream);
    }

    public synchronized PrintStream getMessageStream(String key) {
        return this.messageStreams.get(key);
    }

    public synchronized void setErrorStream(PrintStream errorStream) {
        if (errorStream == null) {
            this.errorStream = emptyStream;
        } else {
            this.errorStream = errorStream;
        }
    }

    public synchronized void setWarningStream(PrintStream warningStream) {
        if (warningStream == null) {
            this.warningStream = emptyStream;
        } else {
            this.warningStream = warningStream;
        }
    }

    public synchronized void setShortMessageStream(PrintStream shortMessageStream) {
        if (shortMessageStream == null) {
            this.shortMessageStream = emptyStream;
        } else {
            this.shortMessageStream = shortMessageStream;
        }
    }

    public synchronized void setVerboseMessageStream(PrintStream verboseMessageStream) {
        if (verboseMessageStream == null) {
            this.verboseMessageStream = emptyStream;
        } else {
            this.verboseMessageStream = verboseMessageStream;
        }
    }
}
