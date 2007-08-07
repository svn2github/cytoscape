// JarPluginDirectoryAction: prompts user for which dir to load Jars from.
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
 * This class provides an action for loading Cytoscape plugins from jar files
 * found in a directory selected by the user. When triggered, it prompts the
 * user to select a directory, gets every jar file within that directory, and
 * passes each jar file to a new instance of JarClassLoader to search for
 * plugins.
 */
public class JarPluginDirectoryAction extends AbstractAction {
    protected CyWindow cyWindow;
    protected File file;
    protected boolean ready=false;
    JarPluginDirectoryAction(CyWindow cyWindow) {
	super ("Plugin Jar Directory");
	this.cyWindow = cyWindow;
    }
    public void actionPerformed (ActionEvent e) {
	if(!getDir()) return;
	ready=true;
	tryDirectory();
    }
    /**
     * sets directory manually
     */
    public void setDir(String newFile) {
	try {
	    file = new File(newFile);
	    if(file.isDirectory()) ready=true;
	    else {
		String s = file.getParent();
		file = new File(s);
		if(file.isDirectory()) ready=true;
		else { ready=false; }
	    }
	}
	catch (Exception e) {
	    System.err.println ("Not file: "+newFile+"\n"+e.getMessage());
	    ready=false;
	}
    }
    /*
     * tries the currently selected directory. Can be called manually or
     * automatically from actionPerformed.
     */
    public void tryDirectory() {
	if(ready==false) return;
	String[] fileList = file.list();

	String slashString="";
	if(!(file.getPath().endsWith("/"))) slashString="/";

	for(int i=0; i<fileList.length; i++) {
	    if(!(fileList[i].endsWith(".jar"))) continue;
	    String jarString = file.getPath() + slashString + fileList[i];
	    try {
		//System.out.println(jarString);
		JarClassLoader jcl = new JarClassLoader("file:" + jarString,
							cyWindow);
		jcl.loadRelevantClasses();
	    }
	    catch (Exception e1) {
		System.err.println ("Error loading jar: " + e1.getMessage ());
	    }
	    /*
	    System.out.println("Chose: " + jarString);
	    */
	}
    }
    

    /**
     * file browser
     */
    private boolean getDir() {
        JFileChooser fChooser =
	    new JFileChooser(cyWindow.getCytoscapeObj().getCurrentDirectory());
        fChooser.setDialogTitle("Load Plugin from Jar Directory");
	fChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        switch (fChooser.showOpenDialog(null)) {
	    
        case JFileChooser.APPROVE_OPTION:
            file = fChooser.getSelectedFile();
	    cyWindow.getCytoscapeObj().setCurrentDirectory(file);
            return true;
        default:
            // cancel or error
            return false;
        }
    }
    



} // JarPluginDirectoryAction

	
