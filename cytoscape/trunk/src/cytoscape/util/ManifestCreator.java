package cytoscape.util;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Custom Ant Task to automatically generate Cytoscape manifest file.
 * Manifest File is automatically based on all JAR Files in cytoscape/lib.
 *
 */
public class ManifestCreator extends Task {
    private static final String MANIFEST_FILE = "build/cytoscape.manifest";

    public void execute() throws BuildException {
        try {
        StringBuffer output = new StringBuffer();
        FileWriter fileOut = new FileWriter (MANIFEST_FILE);
            createManifestHeader(output);

        //  Get All JAR Files in lib directory.
        File file = new File ("lib");
        JarFilter jarFilter = new JarFilter();
        File jars[] = file.listFiles(jarFilter);
        for (int i=0; i<jars.length; i++) {
            String name = jars[i].getName();
            output.append ("lib/" + name +" ");
        }
        output.append ("\n");
        fileOut.write(output.toString());
        fileOut.close();
        System.out.println("Manifest File Written to:  " + MANIFEST_FILE);
        } catch (IOException e) {
            throw new BuildException (e);
        }
    }

    private void createManifestHeader(StringBuffer output) {
        output.append ("Main-Class: cytoscape.CyMain\n");
        output.append ("Class-Path: ");
    }

}

/**
 * Jar Filter to Find All Files Ending in .jar File Extension.
 */
class JarFilter implements FilenameFilter {

    /**
     * Accepts Files Ending in .jar extension.
     * @param dir Directory.
     * @param name File Name.
     * @return Accept or Reject Flag
     */
    public boolean accept (File dir, String name) {
        if (name.endsWith(".jar")) {
            return true;
        } else {
            return false;
        }
    }

}
