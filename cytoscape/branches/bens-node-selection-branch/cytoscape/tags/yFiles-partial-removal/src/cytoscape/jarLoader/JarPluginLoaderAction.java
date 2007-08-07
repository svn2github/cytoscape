// JarLoaderAction: prompts user for which Jar to load a plugin from.
//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.jarLoader;
//--------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import cytoscape.view.CyWindow;

/**
 * This class provides an action for loading Cytoscape plugins from a jar file.
 * When triggered, it prompts the user to select a jar file and passes this file
 * to a new instance of JarClassLoader to search for plugins in that jar file.
 */
public class JarPluginLoaderAction extends AbstractAction {
    protected CyWindow cyWindow;
    protected File file;  //the jar file selected by the user
    JarPluginLoaderAction(CyWindow cyWindow) {
	super ("Plugin Jar File");
	this.cyWindow = cyWindow;
    }
    public void actionPerformed (ActionEvent e) {
	if(!getFile()) return;
	String jarString = file.getPath();
	System.out.println("Chose: " + jarString);
	try {
	    JarClassLoader jcl = new JarClassLoader("file:" + jarString,
						    cyWindow);
	    jcl.loadRelevantClasses();
	}
	catch (Exception e1) {
	    System.err.println ("Error 1: " + e1.getMessage ());
	}
    }
    

    /**
     * file browser.
     *
     * @return true if a valid file was chosen, false otherwise
     */
    private boolean getFile() {
        JFileChooser fChooser =
	    new JFileChooser(cyWindow.getCytoscapeObj().getCurrentDirectory());
        fChooser.setDialogTitle("Load Plugin from Jar");
        switch (fChooser.showOpenDialog(null)) {
	    
        case JFileChooser.APPROVE_OPTION:
            file = fChooser.getSelectedFile();
            String s;
	    
            try {
                FileReader fin = new FileReader(file);
                BufferedReader bin = new BufferedReader(fin);
                fin.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.toString(),
					      "Error Reading \"" +
					      file.getName()+"\"",
					      JOptionPane.ERROR_MESSAGE);
                return false;
            }
	    cyWindow.getCytoscapeObj().setCurrentDirectory(file);
            return true;
        default:
            // cancel or error
            return false;
        }
    }
    



} // JarLoaderAction

	
