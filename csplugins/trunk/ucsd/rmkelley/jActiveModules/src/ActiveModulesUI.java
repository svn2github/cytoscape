//ActiveModulesUI.java
//------------------------------------------------------------------------------
package csplugins.jActiveModules;
//------------------------------------------------------------------------------

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

import junit.framework.*;   

import cytoscape.view.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.data.servers.*;
import cytoscape.data.readers.*;
import csplugins.jActiveModules.data.*;
import csplugins.jActiveModules.dialogs.*;

//------------------------------------------------------------------------------
/**
 * UI for Active Modules
 */
public class ActiveModulesUI extends AbstractPlugin {

    protected CyWindow cytoscapeWindow;
    protected ActivePaths activePaths;
    protected ActivePathFinderParameters apfParams;

    public ActiveModulesUI (CyWindow cytoscapeWindow) {
	if(cytoscapeWindow.getNetwork().getExpressionData()==null) {
	    System.out.println("No expression data; " +
			       "not starting ActiveModules plugin.");
	    return;
	}
	System.out.println("Starting jActiveModules plugin!\n");
	/* initialize variables */
	this.cytoscapeWindow = cytoscapeWindow;
	apfParams = new ActivePathFinderParameters();
	activePaths = new ActivePaths(cytoscapeWindow);
	
	/* Add function calls to Cytoscape menus */
	cytoscapeWindow.getCyMenus().getOperationsMenu().add ( new FindActivePathsAction () );
	cytoscapeWindow.getCyMenus().getOperationsMenu().add ( new ScoreSubComponentAction () );

	/* check for command line arguments to run right away */
	String[] args = cytoscapeWindow.getCytoscapeObj().getConfiguration().getArgs();
	ActivePathsCommandLineParser parser = new ActivePathsCommandLineParser(args);
	if (parser.shouldRunActivePaths()) {
	    activePaths.setParams(parser.getActivePathFinderParameters());
	    Thread t = new Thread(activePaths);
	    t.start();
	}
	//if(parser.getTableFileName()!=null) {
	//    System.out.println("getting active modules table file.");
	//    activePaths.clearActivePaths();
	//    activePaths.showConditionsVsPathwaysTable(parser.getTableFileName());
	//}
    }

    public String describe () {
	String desc = "ActiveModules is a plugin that searches a molecular " + 
	    "interaction network to find expression activated subnetworks, " +
	    "i.e., modules.";
	return desc;
    }

    protected class FindActivePathsAction extends AbstractAction  
	implements ActivePathsParametersPopupDialogListener {
	
	private boolean cancelActivePathsFinding;
	FindActivePathsAction () { super ("Active Modules: Find Modules"); }
	
	public void actionPerformed (ActionEvent e) {
	    JFrame mainFrame = cytoscapeWindow.getMainFrame ();
	    JDialog paramsDialog = new ActivePathsParametersPopupDialog 
		(this, mainFrame, "Find Active Modules Parameters", apfParams);
	    paramsDialog.pack ();
	    paramsDialog.setLocationRelativeTo (mainFrame);
	    paramsDialog.setVisible (true);
	    
	    // added this if-clause to handle the Dismiss button.
	    if(cancelActivePathsFinding) {
		apfParams.setToUseMCFile(false);
	    }
	    else {
		// owo 2002.04.05
		cancelActivePathsFinding = !getMontecarloFile();
		if (cancelActivePathsFinding) {
		    apfParams.setToUseMCFile(false);
		}
		else{
			activePaths.setParams(apfParams);
			Thread t = new Thread(activePaths);
			t.start();
		}    
	}
	} // actionPerformed

	public void cancelActivePathsFinding () { // called when the popup dialog is dismissed
	    cancelActivePathsFinding = true;
	    cytoscapeWindow.getNetwork().endActivity("jActiveModules");
	}

	public void setActivePathsParameters (ActivePathFinderParameters incomingApfParams) {
	    
	    // note: don't use the copy construcor, because
	    // doing so will destroy the referential link
	    // between the FAPA's apfParams and the AP's apfParams.
	    // instead, use setParams.
	    apfParams.setParams(incomingApfParams);
	    
	    // this line added to undo dismiss button pushes.
	    cancelActivePathsFinding = false;
	    
	} // setActivePathsParameters
    } // FindActivePathsAction
    
    //------------------------------------------------------------------------------
    protected class ScoreSubComponentAction extends AbstractAction {
	
	ScoreSubComponentAction () { super ("Active Modules: Score Selected Nodes"); }
	public void actionPerformed (ActionEvent e) {
	    if(apfParams.getIsDefault()) {
		Object[] options = { "OK"};
		JOptionPane.showOptionDialog(null,
					     "Active Module Parameters need to be set.\n" +
					     "Try \"Find Active Modules\" first.\n\n" +
					     "(Use the \"Save Params\" option within " +
					     "Find Active Modules.)",
					     "Set Parameters First.",
					     JOptionPane.DEFAULT_OPTION,
					     JOptionPane.WARNING_MESSAGE,
					     null, options, options[0]);
	    }
	    else{
 		activePaths.scoreActivePath (apfParams);
	    }
	} // actionPerformed
	
    } // ScoreSubComponentAction
    //------------------------------------------------------------------------------
    // owo 2002.04.05 based on code originally by Dan Ramage
    protected boolean getMontecarloFile() {
	// step 0: pop up dialog asking to load file or calculate from scratch
	boolean askMode = true;
	if(apfParams.getMCboolean()) {
	    while (askMode) {
		
		Object[] options = { "Load From File", "Generate", "Cancel" };
		switch ((int)JOptionPane.showOptionDialog(null,
							  "Load or Generate Monte Carlo File?",
							  "Monte Carlo File",
							  JOptionPane.DEFAULT_OPTION,
							  JOptionPane.WARNING_MESSAGE,
							  null, options, options[0])) {
		    
		case 0:
		    // montecarlo: Load From File
		    // keep going if file not loaded
		    askMode = (!mcLoad());
		    break;
		    
		case 1:
		    // montecarlo: Generate
		    //return cmGenerate();
		    apfParams.setToUseMCFile(false);
		    return true;
		    
		case 2:
		    // montecarlo: Cancel
		default:
		    apfParams.setToUseMCFile(false);
		    return false;
		}
	    }
	}
	
	// default: nothing loaded
	return true;
    } // getMonteCarloFile
    
    //------------------------------------------------------------------------------
    // based on code originally by Dan Ramage
    protected boolean mcLoad() {
	JFileChooser fChooser = new JFileChooser(cytoscapeWindow.getCytoscapeObj().getCurrentDirectory());	
	fChooser.setDialogTitle("Load Monte Carlo File");
	
	String MC_VERSION = "#MC:0.1";
	
	switch (fChooser.showOpenDialog(null)) {
	    
	case JFileChooser.APPROVE_OPTION:
	    File file = fChooser.getSelectedFile();
	    apfParams.setToUseMCFile(true);
	    apfParams.setMcFileName(file.getAbsolutePath());
	    return true;
	    
	default:
	    // cancel or error
	    return false;
	}
    } // mcLoad
}
