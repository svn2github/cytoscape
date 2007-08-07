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
import cytoscape.*;
import java.io.*;

public class JarPluginDirectoryAction extends AbstractAction {
    protected CytoscapeWindow cytoscapeWindow;
    protected File file;
    protected boolean ready=false;
    JarPluginDirectoryAction(CytoscapeWindow cytoscapeWindow) {
	super ("Plugin Jar Directory");
	this.cytoscapeWindow = cytoscapeWindow;
    }
    public void actionPerformed (ActionEvent e) {
	if(!getDir()) return;
	ready=true;
	tryDirectory();
    }
    /** sets directory manually */
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
    /** tries directory manually */
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
							cytoscapeWindow);
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
    

    /** file browser
     */
    private boolean getDir() {
        JFileChooser fChooser =
	    new JFileChooser(cytoscapeWindow.getCurrentDirectory());
        fChooser.setDialogTitle("Load Plugin from Jar Directory");
	fChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        switch (fChooser.showOpenDialog(null)) {
	    
        case JFileChooser.APPROVE_OPTION:
            file = fChooser.getSelectedFile();
	    cytoscapeWindow.setCurrentDirectory(file);
            return true;
        default:
            // cancel or error
            return false;
        }
    }
    



} // JarPluginDirectoryAction

	
