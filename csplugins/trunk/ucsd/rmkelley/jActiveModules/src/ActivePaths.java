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
//import cytoscape.undo.*;
import csplugins.jActiveModules.data.*;
import csplugins.jActiveModules.dialogs.*;
//import cytoscape.vizmap.*;
//import cytoscape.layout.*;
import cytoscape.*;
import cytoscape.data.CyNetworkFactory;

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
  protected String [] attrNames;
  protected static boolean activePathsFindingIsAvailable;
  protected JButton activePathToolbarButton;
  protected JFrame mainFrame;
  protected GraphPerspective perspective;
  protected String titleForCurrentSelection;
  protected ActivePathFinderParameters apfParams;


  //----------------------------------------------------------------
  public ActivePaths (CyWindow cytoscapeWindow, ActivePathFinderParameters apfParams)
  {
    this.cytoscapeWindow = cytoscapeWindow;
    this.apfParams = apfParams;
    expressionData = cytoscapeWindow.getNetwork().getExpressionData ();
    attrNames = expressionData.getConditionNames ();
    menubar = cytoscapeWindow.getCyMenus().getMenuBar ();
    mainFrame = cytoscapeWindow.getMainFrame ();
    perspective = cytoscapeWindow.getView().getGraphPerspective();
  } // ctor

  //--------------------------------------------------------------
  protected void setShowTable (boolean showTable) { this.showTable = showTable; }
  protected void clearActivePaths ()              { this.activePaths = null; }
  
  public void run()
  {
    String callerID = "jActiveModules";
    cytoscapeWindow.getNetwork().beginActivity(callerID);
    System.gc();
    GraphViewController gvc = cytoscapeWindow.getGraphViewController();
    gvc.stopListening();
    long start = System.currentTimeMillis ();

    //run the path finding algorithm
    ActivePathsFinder apf = new ActivePathsFinder(expressionData,attrNames,cytoscapeWindow.getNetwork(),apfParams,mainFrame);
    activePaths = apf.findActivePaths();

    long duration = System.currentTimeMillis () - start;
    int numberOfPathsFound = activePaths.length;
    System.out.println ("-------------- back from finderBridge: "+numberOfPathsFound+" paths, "+duration+" msecs");
    tableDialog = null;
    cytoscapeWindow.getNetwork().endActivity(callerID);
    gvc.resumeListening();
    if(apfParams.getExit()){
      System.exit(0);
    }
    if(showTable){
      showConditionsVsPathwaysTable();
    }
  } 
    
  /**
   * Returns the best scoring path from the last run. This is
   * mostly used by the score distribution when calculating the 
   * distribution
   */
  protected Component getHighScoringPath(){
    return activePaths[0];
  }
 
  protected void showConditionsVsPathwaysTable () {
     tableDialog = new ConditionsVsPathwaysTable (cytoscapeWindow.getMainFrame(),
						  cytoscapeWindow,
						  attrNames, 
						  activePaths, 
						  this);
 
    tableDialog.pack ();
    tableDialog.setLocationRelativeTo (mainFrame);
    tableDialog.setVisible (true);
    addActivePathToolbarButton ();
  }

  protected ConditionsVsPathwaysTable getConditionsVsPathwaysTable () { 
    return tableDialog; 
  }


  /**
   * Scores the currently selected nodes in the graph, and pops up a window with the result
   */
  protected void scoreActivePath ()  
  {
    String callerID = "jActiveModules";
    cytoscapeWindow.getNetwork().beginActivity(callerID);
    ActivePathsFinder apf = new ActivePathsFinder(expressionData,attrNames,cytoscapeWindow.getNetwork(),apfParams,mainFrame);

    long start = System.currentTimeMillis ();
    cytoscapeWindow.redrawGraph ();
    Vector result = new Vector();
    Iterator it = cytoscapeWindow.getView().getSelectedNodes().iterator();
    while(it.hasNext()){
      result.add(((NodeView)it.next()).getNode());
    }
	
    double score = apf.scoreList(result);
    long duration = System.currentTimeMillis () - start;
    System.out.println ("-------------- back from score: " + duration + " msecs");
    System.out.println ("-------------- score: " + score + " \n");
    JOptionPane.showMessageDialog (mainFrame, "Score: " + score);
    cytoscapeWindow.getNetwork().endActivity(callerID);
  } // scoreActivePath


  protected class ActivePathControllerLauncherAction extends AbstractAction  {
    ActivePathControllerLauncherAction () {
      super ("Active Modules"); 
    } // ctor
    public void actionPerformed (ActionEvent e) {
      showConditionsVsPathwaysTable();
    }
  } 
    
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

  } 

  
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
