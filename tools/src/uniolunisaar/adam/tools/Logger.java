package uniolunisaar.adam.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

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
    private static Logger instance = null;

    private Logger() {
        this.silent = false;
        this.output = OUTPUT.STREAMS;
        this.path = null;
        this.file = null;
        this.systemOutput = System.out;
        this.shortMessageStream = System.out;
        this.verboseMessageStream = System.out;
        this.errorStream = System.err;
        this.warningStream = System.out;
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    private PrintStream errorStream;
    private PrintStream warningStream;
    private PrintStream shortMessageStream;
    private PrintStream verboseMessageStream;
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

    public void addMessage(String msg) {
        addMessage(msg, true, false);
    }

    public void addMessage(boolean force, String msg) {
        addMessage(msg, false, force);
    }

    public void addMessage(String msg, boolean verbose) {
        addMessage(msg, verbose, false);
    }

    public void addMessage(String msg, boolean verbose, boolean forced) {
        if (silent && !forced) {
            return;
        }
        if (output == OUTPUT.STREAMS || output == OUTPUT.STREAMS_AND_FILE) {
            if (forced) {
                setSilent(false);
            }
            if (verbose) {
                verboseMessageStream.println(msg);
            } else {
                shortMessageStream.println(msg);
            }
            if (forced) {
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

    public void addError(String msg, Exception e) {
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

    public void addWarning(String msg) {
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

    public void setOutput(OUTPUT output) {
        if (path == null && (output == OUTPUT.FILE || output == OUTPUT.STREAMS_AND_FILE)) {
            System.err.println("Error: no path set.");
        }
        if ((flag == null || writer == null) & output == OUTPUT.CLIENT) {
            System.err.println("Error: flag and writer must be set for sending to a client.");
        } else {
            this.output = output;
        }
    }

    public void resetFile() throws FileNotFoundException, UnsupportedEncodingException {
        if (path != null) {
            file = new PrintWriter(path, "UTF-8");
        }
    }

    public void setPath(String path) throws FileNotFoundException, UnsupportedEncodingException {
        this.path = path;
        this.file = new PrintWriter(path, "UTF-8");
    }

    public void setVerbose(boolean verbose) {
        if (verbose) {
            verboseMessageStream = System.out;
            shortMessageStream = emptyStream;
        } else {
            verboseMessageStream = emptyStream;
            shortMessageStream = System.out;
        }
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
        if (silent) {
            System.setOut(emptyStream);
        } else {
            System.setOut(systemOutput);
        }
    }

    public void close() {
        if (file != null) {
            file.close();
        }
    }

    public void set2Client(ObjectOutputStream writer, Object flag) {
        this.output = OUTPUT.CLIENT;
        this.writer = writer;
        this.flag = flag;
    }

    public void setErrorStream(PrintStream errorStream) {
        this.errorStream = errorStream;
    }

    public void setWarningStream(PrintStream warningStream) {
        this.warningStream = warningStream;
    }

    public void setShortMessageStream(PrintStream shortMessageStream) {
        this.shortMessageStream = shortMessageStream;
    }

    public void setVerboseMessageStream(PrintStream verboseMessageStream) {
        this.verboseMessageStream = verboseMessageStream;
    }
}
