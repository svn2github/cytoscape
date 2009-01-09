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
import cytoscape.*;
import java.io.*;

public class JarPluginLoaderAction extends AbstractAction {
    protected CytoscapeWindow cytoscapeWindow;
    protected File file;
    JarPluginLoaderAction(CytoscapeWindow cytoscapeWindow) {
	super ("Plugin Jar File");
	this.cytoscapeWindow = cytoscapeWindow;
    }
    public void actionPerformed (ActionEvent e) {
	if(!getFile()) return;
	String jarString = file.getPath();
	System.out.println("Chose: " + jarString);
	try {
	    JarClassLoader jcl = new JarClassLoader("file:" + jarString,
						    cytoscapeWindow);
	    jcl.loadRelevantClasses();
	}
	catch (Exception e1) {
	    System.err.println ("Error 1: " + e1.getMessage ());
	}
    }
    

    /** file browser
     */
    private boolean getFile() {
        JFileChooser fChooser =
	    new JFileChooser(cytoscapeWindow.getCurrentDirectory());
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
	    cytoscapeWindow.setCurrentDirectory(file);
            return true;
        default:
            // cancel or error
            return false;
        }
    }
    



} // JarLoaderAction

	
