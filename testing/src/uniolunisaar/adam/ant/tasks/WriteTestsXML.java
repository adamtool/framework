/*-
 * APT - Analysis of Petri Nets and labeled Transition systems
 * Copyright (C) 2015  Uli Schlachter
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package uniolunisaar.adam.ant.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.scannotation.AnnotationDB;

/**
 * Ant task to prepare a TestNG XML file.
 *
 * Adaption of the WriteTestsXML of the APT package by Uli Schlachter, to not
 * find classes ending with 'Test', but those classes using the @Test annotation
 * of testng.
 *
 * @author Manuel Gieseking
 */
public class WriteTestsXML {

    private WriteTestsXML() {
    }

    /**
     * Program entry point. Arguments are output file, directory to scan
     *
     * @param args Program arguments.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Need exactly three arguments: output file, directory to scan");
        }
        File outputFile = new File(args[0]);
        File directoryToScan = new File(args[1]);

        Worker worker = new Worker();

        try {
            AnnotationDB db = new AnnotationDB();
            // Just scan Class Annotations  
            db.setScanFieldAnnotations(false);
            db.setScanMethodAnnotations(false);
            db.setScanParameterAnnotations(false);

            // Do the scanning
            db.scanArchives(directoryToScan.toURI().toURL());

            Map<String, Set<String>> annotationIndex = db.getAnnotationIndex();

            // Classes with the @Test annotation of testng  
            Set<String> entities = annotationIndex.get("org.testng.annotations.Test");
            for (String entity : entities) {
                worker.addClass(entity);

            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        try {
            worker.write(outputFile);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Worker {

        private static final String FILE_HEADER = "<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">\n"
                + "<suite name=\"Suite\">\n";
        private static final String FILE_FOOTER = "</suite>\n";
        private static final String TEST_HEADER_FORMAT = "    <test name=\"%s\">\n"
                + "        <classes>\n";
        private static final String TEST_FOOTER = "        </classes>\n"
                + "    </test>\n";
        private static final String CLASS_TEMPLATE = "            <class name=\"%s\" />\n";

        private final Map<String, Set<String>> testsToClasses = new HashMap<>();

        public void addClass(String clazz) {
            // Split foo.bar.classname into package and classname
            String thepackage = clazz.substring(0, clazz.lastIndexOf('.'));
            //String clazzname = clazz.substring(clazz.lastIndexOf('.') + 1);
            Set<String> set = testsToClasses.get(thepackage);
            if (set == null) {
                set = new HashSet<>();
                testsToClasses.put(thepackage, set);
            }

            set.add(clazz);
        }

        public void write(File file) throws FileNotFoundException, UnsupportedEncodingException {
            try (Formatter format = new Formatter(file, "UTF-8")) {
                format.format(FILE_HEADER);
                for (Map.Entry<String, Set<String>> entry : testsToClasses.entrySet()) {
                    format.format(TEST_HEADER_FORMAT, entry.getKey());
                    for (String klass : entry.getValue()) {
                        format.format(CLASS_TEMPLATE, klass);
                    }
                    format.format(TEST_FOOTER);
                }
                format.format(FILE_FOOTER);
            }
        }
    }
}
