package uniolunisaar.adam.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class AdamProperties {

    private static AdamProperties instance = null;
    private static Properties props;
    private static final String PROPERTY_FILE = "PROPERTY_FILE";
    public static final String LIBRARY_FOLDER = "libraryFolder";
    public static final String AIGER_TOOLS = "aigertools";
    public static final String MC_HYPER = "mcHyper";
    public static final String ABC = "abcBin";
    public static final String DOT = "dot";
    public static final String QUABS = "quabs";
    public static final String TIME = "time";

    private AdamProperties() {
        // Load the property file which location is given as parameter of the console
        String file = System.getProperty(PROPERTY_FILE);
        InputStream in = null;
        try {
            if (file == null) {
                throw new RuntimeException("Couldn't find the property file of ADAM. It has not specified as parameter of the java call of ADAM.");
            }
            props = new Properties();
            in = new FileInputStream(file);
            props.load(in);
        } catch (IOException ex) {
            throw new RuntimeException("Couldn't read the property file of ADAM: " + file);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException("Couldn't close the property file of ADAM: " + file);
                }
            }
        }
    }

    public static AdamProperties getInstance() {
        if (instance == null) {
            instance = new AdamProperties();
        }
        return instance;
    }

    public synchronized Object setProperty(String key, String value) {
        return props.setProperty(key, value);
    }

    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public Enumeration<?> propertyNames() {
        return props.propertyNames();
    }

}
