package uniolunisaar.adam.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Manuel Gieseking
 */
public class Logger {

    public enum OUTPUT {

        NONE,
        CONSOLE,
        FILE,
        CONSOLE_AND_FILE,
        CLIENT
    }
    private static Logger instance = null;

    private Logger() {
        this.verbose = true;
        this.output = OUTPUT.CONSOLE;
        this.path = null;
        this.file = null;
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
    private boolean verbose;
    private OUTPUT output;
    private String path;
    private PrintWriter file;
    // For server-client-communication
    private ObjectOutputStream writer;
    private Object flag;

    public void addMessage(String msg) {
        addMessage(msg, true);
    }

    public void addMessage(String msg, boolean verbose) {
        if (!this.verbose && verbose) {
            return;
        }
        if (output == OUTPUT.CONSOLE || output == OUTPUT.CONSOLE_AND_FILE) {
            System.out.println(msg);
        }
        if (output == OUTPUT.FILE || output == OUTPUT.CONSOLE_AND_FILE) {
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

    public void setOutput(OUTPUT output) {
        if (path == null && (output == OUTPUT.FILE || output == OUTPUT.CONSOLE_AND_FILE)) {
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
        this.verbose = verbose;
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
}
