//ExpressionRandomizer.java
//------------------------------------------------------------------------------
package csplugins.jActiveModules;
//------------------------------------------------------------------------------

import java.awt.event.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import java.util.*;
import java.io.*;
import junit.framework.*;   

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.undo.*;
import csplugins.jActiveModules.data.*;
import csplugins.jActiveModules.dialogs.*;
import cytoscape.view.CyWindow;


//------------------------------------------------------------------------------
/**
 * This class provides various routines for randomizing expression values
 * on the network
 */
public class ScoreDistribution extends AbstractPlugin {

    protected ActivePaths activePaths;
    protected ActivePathFinderParameters apfParams;
    protected CyWindow cytoscapeWindow;
    protected ExpressionData expressionData = null;
    protected String [] conditionNames;
    protected Random randomGenerator;
	protected String distributionFile = "distribution.txt";

    public ScoreDistribution(CyWindow cytoscapeWindow) {
	if(cytoscapeWindow.getNetwork().getExpressionData()==null) {
	    System.out.println("No expression data; " +
			       "not starting ExpressionRandomizer plugin.");
	    return;
	}
	System.out.println("Starting ExpressionRandomizer plugin");

	// initialize active Path Finders
	apfParams = new ActivePathFinderParameters();
	activePaths = new ActivePaths(cytoscapeWindow);
	activePaths.setShowTable(false);

	// initialize variables
	this.cytoscapeWindow = cytoscapeWindow;
	expressionData = cytoscapeWindow.getNetwork().getExpressionData ();
	conditionNames = expressionData.getConditionNames ();
	randomGenerator = new Random();

	// create plugin menu items
	cytoscapeWindow.getCyMenus().getOperationsMenu().add (new DoRandomizeAndRun ());

    }
    
    public String describe () {
	String desc = "ExpressionRandomizer Plug-in";
	return desc;
    }

    private void randomizeGeneNames() {
	Vector newNames = new Vector();
	Vector newDescripts = new Vector();
	Vector geneNames = expressionData.getGeneNamesVector();
	Vector geneDescripts = expressionData.getGeneDescriptorsVector();
	int s = geneNames.size();
	int i;
	String name, descript;
	System.out.println("size = " + s);
	while (s > 0) {
	    i = randomGenerator.nextInt(s--);
	    name = (String) geneNames.remove(i);
	    descript = (String) geneDescripts.remove(i);
	    newNames.add(name);
	    newDescripts.add(descript);
	    // System.out.println(name);
	}
	//System.out.println("size = " + newNames.size());
	expressionData.setGeneNames(newNames);
	expressionData.setGeneDescriptors(newDescripts);
	cytoscapeWindow.redrawGraph();	
    }


    
    //------------------------------------------------------------
    protected class DoRandomizeAndRun extends AbstractAction  
	implements Runnable, ActivePathsParametersPopupDialogListener {
	
	private boolean cancelActivePathsFinding;
	DoRandomizeAndRun () { super ("Score Distribution"); }

	public void actionPerformed (ActionEvent e) {
	    JFrame mainFrame = cytoscapeWindow.getMainFrame ();
	    JDialog paramsDialog = new ActivePathsParametersPopupDialog 
		(this, mainFrame, "Find Active Modules Parameters", apfParams);
	    paramsDialog.pack ();
	    paramsDialog.setLocationRelativeTo (mainFrame);
	    paramsDialog.setVisible (true);
	    
	    // added this if-clause to handle the Dismiss button.
	    if(cancelActivePathsFinding) {
		cytoscapeWindow.setInteractivity (true);
		apfParams.setToUseMCFile(false);
	    }
	    else {
		cancelActivePathsFinding = !getMontecarloFile();
		if (cancelActivePathsFinding) {
		    apfParams.setToUseMCFile(false);
		}
		else{
		        Thread t = new Thread(this);
			t.start();	
		}
	    }
	} // actionPerformed
	public void run() {
	int i, f;
	JFrame mainFrame = cytoscapeWindow.getMainFrame();
	String inputValue = JOptionPane.showInputDialog(mainFrame, "Number of runs");
	int numberOfRuns = Integer.parseInt(inputValue);
	PrintStream p = null;	
	try{
		FileOutputStream out = new FileOutputStream(JOptionPane.showInputDialog(mainFrame, "Output file"));
		p = new PrintStream(out);	
	}catch(Exception e){
		System.out.println("Problem opening file for output");
		System.exit(-1);
	}

	// random runs to accumlate node frequencies
	activePaths.setParams(apfParams);
	activePaths.showTable = false;
	activePaths.hideOthers = false;
	Thread t = new Thread(activePaths);
	t.start();
	try{
		t.join();
		p.println(""+activePaths.getHighScoringPath().getScore());
	}catch(Exception e){
		e.printStackTrace();
		System.err.println("Failed to join thread");
		System.exit(-1);
	}
	if(!(apfParams.getToUseMCFile())) {
	    apfParams.setToUseMCFile(true);
	    //File file = new File("last.mc");
	    //file.delete();
	    apfParams.setMcFileName("last.mc");
	    activePaths.setParams(apfParams);
	}
	for (i=1; i<numberOfRuns; i++) {
	    randomizeGeneNames();
	    t = new Thread(activePaths);
	    t.start();
	    try{
		t.join();
		//debug
		//
		p.println(""+activePaths.getHighScoringPath().getScore());
		//
		//debug

	    }catch(Exception e){
		e.printStackTrace();
		System.err.println("Failed to join thread");
		System.exit(-1);
	    }   
	}
	try{
		p.close();
	}catch(Exception e){
		e.printStackTrace();
		System.exit(-1);
	}

    }
	public void cancelActivePathsFinding () { // called when the popup dialog is dismissed
	    cancelActivePathsFinding = true;
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
    }

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
	JFileChooser fChooser = new JFileChooser();	
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

} // ExpressionRandomizer class

            
        
