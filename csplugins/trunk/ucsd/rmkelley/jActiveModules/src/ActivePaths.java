// ActivePaths.java:  a plugin for CytoscapeWindow,
// which uses VERA & SAM expression data
// to propose active gene regulatory paths
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package csplugins.jActiveModules;
//------------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.JOptionPane;
import giny.model.*;
import giny.view.*;
import java.io.*;
import java.util.*;
import cytoscape.view.*;
import cytoscape.data.*;
import cytoscape.data.servers.*;
import cytoscape.data.readers.*;
import cytoscape.undo.*;
import csplugins.jActiveModules.data.*;
import csplugins.jActiveModules.dialogs.*;
//import cytoscape.vizmap.*;
import cytoscape.layout.*;
import cytoscape.*;

//-----------------------------------------------------------------------------------
public class ActivePaths implements ActivePathViewer, Runnable {

    protected CyWindow cytoscapeWindow;
    protected boolean showTable = true;
    protected boolean hideOthers = true;
    protected ExpressionData expressionData = null;
    protected JMenuBar menubar;
    protected JMenu expressionConditionsMenu;
    protected ConditionsVsPathwaysTable tableDialog;
    protected String currentCondition = "none";
    protected Component [] activePaths;
    protected String [] conditionNames;
    protected static boolean activePathsFindingIsAvailable;
    protected JButton activePathToolbarButton;
    protected JFrame mainFrame;
    protected GraphPerspective perspective;
    protected String titleForCurrentSelection;
    protected ActivePathFinderParameters apfParams;


    //----------------------------------------------------------------
    public ActivePaths (CyWindow cytoscapeWindow)
    {
	this.cytoscapeWindow = cytoscapeWindow;
	apfParams = new ActivePathFinderParameters();
	expressionData = cytoscapeWindow.getNetwork().getExpressionData ();
	conditionNames = expressionData.getConditionNames ();
	menubar = cytoscapeWindow.getCyMenus().getMenuBar ();
	mainFrame = cytoscapeWindow.getMainFrame ();
	perspective = cytoscapeWindow.getView().getGraphPerspective();
    } // ctor

    //--------------------------------------------------------------
    protected void setShowTable (boolean showTable) { this.showTable = showTable; }
    protected void clearActivePaths ()              { this.activePaths = null; }
    
    //------------------------------------------------------------------------------
    protected class ExpressionMenuListener implements ActionListener {
    
	public void actionPerformed (ActionEvent e) {
	    String conditionName = e.getActionCommand ();
	    if (conditionName.equalsIgnoreCase ("none"))
		currentCondition = null;
	    else
		currentCondition = conditionName;
	}

    } // inner class ExpressionMenuListener

    //------------------------------------------------------------------------------

    //------------------------------------------------------------------------------
    //protected void runActivePathsFinder()
    

    public void setParams(ActivePathFinderParameters params){
	apfParams.setParams(params);
    }

    public void run()
    {
	String callerID = "jActiveModules";
	cytoscapeWindow.getNetwork().beginActivity(callerID);
	System.gc();
	long start = System.currentTimeMillis ();
	ActivePathsFinder apf = new ActivePathsFinder(expressionData,conditionNames,cytoscapeWindow,apfParams);
	activePaths = apf.findActivePaths();

	//setShowTable(true);
	long duration = System.currentTimeMillis () - start;
	int numberOfPathsFound = activePaths.length;
	System.out.println ("-------------- back from finderBridge: " +
			    numberOfPathsFound + " paths, " + duration + " msecs");
	tableDialog = null;
	//if(hideOthers){
	//	Vector uniqueNodes = combinePaths (activePaths);
		//cytoscapeWindow.showNodesByName(uniqueNodeNames);
	//}
	if(apfParams.getExit()){
		System.exit(0);
	}
	if(showTable){
		showConditionsVsPathwaysTable();
	}
	cytoscapeWindow.getNetwork().endActivity(callerID);

    } // runActivePathsFinder
    
    //------------------------------------------------------------------------------
    
    protected Component getHighScoringPath(){
	return activePaths[0];
    }
 
    protected void showConditionsVsPathwaysTable () {
	//if (activePaths == null) {
	//    tableDialog = new ConditionsVsPathwaysTable (cytoscapeWindow.getMainFrame(),
	//						 cytoscapeWindow,
	//						 conditionNames, this);
	//    activePaths = tableDialog.getActivePaths();
	//}
	//else if (tableDialog == null)  {
	    tableDialog = new ConditionsVsPathwaysTable (cytoscapeWindow.getMainFrame(),
							 cytoscapeWindow,
							 conditionNames, activePaths, this);
	//}
	tableDialog.pack ();
	tableDialog.setLocationRelativeTo (mainFrame);
	tableDialog.setVisible (true);
	addActivePathToolbarButton ();
    }
    
    /** This version of showConditionsVsPathwaysTable accepts a 
     *  filename argument, in case the table file was specified
     *  on the command line. */
    //protected void showConditionsVsPathwaysTable (String filename) {
	//if (activePaths == null) {
	  //  tableDialog = new ConditionsVsPathwaysTable (cytoscapeWindow.getMainFrame(),
	//						 cytoscapeWindow,
	//						 conditionNames, this, filename);
	  //  activePaths = tableDialog.getActivePaths();
	//}
	//else if (tableDialog == null)  {
	 //   tableDialog = new ConditionsVsPathwaysTable (cytoscapeWindow.getMainFrame(),
	//						 cytoscapeWindow,
	//						 conditionNames, activePaths, this);
	//}
	//tableDialog.pack ();
	//tableDialog.setLocationRelativeTo (mainFrame);
	//tableDialog.setVisible (true);
	//addActivePathToolbarButton ();
    //}
    //------------------------------------------------------------------------------

    protected ConditionsVsPathwaysTable getConditionsVsPathwaysTable () { 
	return tableDialog; 
    }

    //------------------------------------------------------------------------------
    protected void scoreActivePath (ActivePathFinderParameters params)
    {
	String callerID = "jActiveModules";
	cytoscapeWindow.getNetwork().beginActivity(callerID);
	apfParams.setParams(params);
	ActivePathsFinder apf = new ActivePathsFinder(expressionData,conditionNames,cytoscapeWindow,apfParams);

	long start = System.currentTimeMillis ();
	cytoscapeWindow.redrawGraph ();

	double score = apf.scoreSelected();
	long duration = System.currentTimeMillis () - start;
	System.out.println ("-------------- back from score: " + duration + " msecs");
	System.out.println ("-------------- score: " + score + " \n");
	JOptionPane.showMessageDialog (mainFrame, "Score: " + score);
	cytoscapeWindow.getNetwork().endActivity(callerID);

    } // scoreActivePath

    //---------------------------------------------------------------------------------------
    protected class ActivePathControllerLauncherAction extends AbstractAction  {
	ActivePathControllerLauncherAction () {
	    super ("Active Modules"); 
	} // ctor
	public void actionPerformed (ActionEvent e) {
	    showConditionsVsPathwaysTable();
	}
    } // AppearanceControllerLauncher
    //------------------------------------------------------------------------------
    /**
     * find all of the unique node names in the full set of active paths.  there may
     * be duplicates since some nodes may appear in several paths
     */
    protected Vector combinePaths  (Component [] activePaths)
    {
	HashSet set = new HashSet();
	for (int i=0; i < activePaths.length; i++) {
	    set.addAll(activePaths[i].getNodes());
	} // for i
	return new Vector(set);

    } // combinePaths
    //------------------------------------------------------------------------------
    protected void addActivePathToolbarButton ()
    {
	if (activePathToolbarButton != null)
	    cytoscapeWindow.getCyMenus().getToolBar().remove (activePathToolbarButton);

	activePathToolbarButton = 
	    cytoscapeWindow.getCyMenus().getToolBar().add (new ActivePathControllerLauncherAction ());

    } // addActivePathToolbarButton 
    //------------------------------------------------------------------------------
    public void displayPath (Component activePath, boolean clearOthersFirst,
			     String pathTitle)
    {
	titleForCurrentSelection = pathTitle;
	//cytoscapeWindow.selectNodesByName (activePath.getNodes (), clearOthersFirst);
       	Vector nodes = activePath.getNodes();
	GraphView view = cytoscapeWindow.getView();
	Iterator nodeViewIt = view.getSelectedNodes().iterator();
	while(nodeViewIt.hasNext()){
		((NodeView)nodeViewIt.next()).setSelected(false);
	}
	for(int i=0;i<nodes.size();i++){
		NodeView nodeview= view.getNodeView((Node)nodes.get(i));
		nodeview.setSelected(!nodeview.isSelected());
	} 
    }
    //------------------------------------------------------------------------------
    public void displayPath (Component activePath, String pathTitle)
    {
	displayPath (activePath, true, pathTitle);
    }
    //------------------------------------------------------------------------------

} // class ActivePaths (a CytoscapeWindow plugin)
