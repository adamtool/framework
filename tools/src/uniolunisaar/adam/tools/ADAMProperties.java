package uniolunisaar.adam.tools;

import java.util.Enumeration;
import java.util.Properties;

public class ADAMProperties {

    private static ADAMProperties instance = null;
    private static Properties props;
    private static final String LIBFOLDER = "libfolder";

    private ADAMProperties() {//throws ClassNotFoundException, IOException {
        props = new Properties();
//        InputStream input = null;
//        String filename = "ADAM.prop";
//        input = ADAMProperties.class.getClassLoader().getResourceAsStream(filename);
//        if (input == null) {
//            throw new ClassNotFoundException(filename);
//        }
//
//        //load a properties file from class path, inside static method
//        props.load(input);
//
//        input.close();
        String folder = System.getProperty(LIBFOLDER);
        props.setProperty(LIBFOLDER, folder == null ? "./lib" : folder);
    }

    public static ADAMProperties getInstance() {//throws ClassNotFoundException, IOException {
        if (instance == null) {
            instance = new ADAMProperties();
        }
        return instance;
    }
    
    public String getLibFolder() {
        return props.getProperty(LIBFOLDER);
    }

    public void setLibFolder(String folder) {
        props.setProperty(LIBFOLDER, folder);
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
