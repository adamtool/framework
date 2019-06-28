package uniolunisaar.adam.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Apaches 
 *  org.apache.commons.io.FileUtils, and 
 *  org.apache.commons.io.IOUtils
 * are not compatible with the GPLv2 (only with GPLv3)
 * 
 * @author Manuel Gieseking
 */
public class IOUtils {

    static String readFileToString(File file) throws IOException {
//       return FileUtils.readFileToString(file); // needs org.apache.commons.io.FileUtils (currently not license compatible)
        return Files.lines(Paths.get(file.toURI()), StandardCharsets.UTF_8).collect(Collectors.joining());
    }

    public static String streamToString(InputStream stream) throws IOException {
//        return IOUtils.toString(stream); // needs org.apache.commons.io.IOUtils (currently not license compatible)
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

}
