//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.actions;
//--------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import cytoscape.CytoscapeObj;
import cytoscape.plugin.jar.JarLoader;
import cytoscape.data.readers.TextHttpReader;
//--------------------------------------------------------------------------
/**
 * This class provides a method for loading plugins from a list of jar files
 * that contain the plugins. Currently the actionPerformed method does nothing;
 * one must call the functional method directly.
 *
 * The input is a string that defines a URL giving the location of a file
 * holding the plugin list. The file should contain one string per line,
 * where each line is a string defining a URL that is a jar file. This class
 * will pass each line of the file to an instance of JarClassLoader.
 */
public class LoadPluginListAction extends AbstractAction {
    protected CytoscapeObj cyObj;
    
    public LoadPluginListAction(CytoscapeObj cyObj) {
        super("Load plugins from list of jar URLs");
        this.cyObj = cyObj;
    }
    
    public void actionPerformed(ActionEvent ae) {
        //curerntly this doesn't do anything. Eventually this should
        //prompt the user for a string URL and try to open it.
    }
    
    /**
     * Attempts to load plugins from a list of jar files. The argument is a
     * String representing a URL that contains a list of jar files, one per line.
     * Each line should be a URL of a jar file; this will be passed to an
     * instance of JarClassLoader to try to load plugins from that jar file.
     */
    public void parsePluginList(String location) {
        try {
            TextHttpReader reader = new TextHttpReader(location);
            reader.read();
            String text = reader.getText();
            String lineSep = System.getProperty("line.separator");
            String[] allLines = text.split(lineSep);
            for (int i=0; i<allLines.length; i++) {
                String pluginLoc = allLines[i];
                try {
                  JarLoader.loadJar( pluginLoc );
                  //JarClassLoader jcl = new JarClassLoader(pluginLoc, cyObj);
                  //  jcl.loadRelevantClasses();
                } catch (Exception e) {
                    System.err.println("Error loading jar: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing plugin list: " + e.getMessage());
        }
    }
}

        
