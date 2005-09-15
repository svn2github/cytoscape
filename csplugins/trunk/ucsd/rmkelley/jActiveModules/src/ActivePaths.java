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
//import cytoscape.data.servers.*;
import cytoscape.data.readers.*;
//import cytoscape.undo.*;
import csplugins.jActiveModules.data.*;
import csplugins.jActiveModules.dialogs.*;
//import cytoscape.vizmap.*;
//import cytoscape.layout.*;
import cytoscape.*;
//import cytoscape.data.CyNetworkFactory;

//-----------------------------------------------------------------------------------
public class ActivePaths implements ActivePathViewer, Runnable {

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
  protected CyNetwork cyNetwork;
  protected String titleForCurrentSelection;
  protected ActivePathFinderParameters apfParams;
  protected static double MIN_SIG = 0.0000000000001;
  protected static double MAX_SIG = 1-MIN_SIG;



  //----------------------------------------------------------------
  public ActivePaths (CyNetwork cyNetwork, ActivePathFinderParameters apfParams)
  {
    this.apfParams = apfParams;
    expressionData = cyNetwork.getExpressionData ();
    if(expressionData == null){
      throw new RuntimeException("No expression data loaded");
    }
    attrNames = expressionData.getConditionNames ();
    Arrays.sort(attrNames);
    menubar = Cytoscape.getDesktop().getCyMenus().getMenuBar ();
    mainFrame = Cytoscape.getDesktop();
    this.cyNetwork = cyNetwork;
    if(expressionData.getSignificanceType() == ExpressionData.LAMBDA){
      expressionData.convertLambdasToPvals();
    }
  } // ctor

  //--------------------------------------------------------------
  protected void setShowTable (boolean showTable) { this.showTable = showTable; }
  protected void clearActivePaths ()              { this.activePaths = null; }
  
  public void run()
  {
    System.gc();
    //GraphViewController gvc = Cytoscape.getDesktop().getGraphViewController();
    //gvc.stopListening();
    long start = System.currentTimeMillis ();
    HashMap expressionMap = generateExpressionMap();
    //run the path finding algorithm
    ActivePathsFinder apf = new ActivePathsFinder(expressionMap,attrNames,cyNetwork,apfParams,mainFrame);
    activePaths = apf.findActivePaths();

    long duration = System.currentTimeMillis () - start;
    int numberOfPathsFound = activePaths.length;
    System.out.println ("-------------- back from finderBridge: "+numberOfPathsFound+" paths, "+duration+" msecs");
    tableDialog = null;
    //gvc.resumeListening();
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
    System.err.println("High Scoring Path:");
    System.err.println(activePaths);
    System.err.println("Score: "+activePaths[0].getScore());
    int size = activePaths[0].getNodes().size();
    System.err.println("Size: "+size);
    System.err.println("Raw score: "+activePaths[0].calculateSimpleScore());
    System.err.println("Mean: "+Component.pStats.getMean(size));
    System.err.println("Std: "+Component.pStats.getStd(size));
    return activePaths[0];
  }

  protected HashMap generateExpressionMap(){
    //set up the HashMap which is used to map from nodes
    //to z values. At this point, we are mapping from the
    //p values for expression to z values
    System.out.println("Processing Expression Data into Hash");
    HashMap tempHash = new HashMap();
    GraphObjAttributes nodeAttributes = cyNetwork.getNodeAttributes();
    for (Iterator nodeIt = cyNetwork.nodesIterator();nodeIt.hasNext();) {
      double [] tempArray = new double[attrNames.length];
      Node current = (Node)nodeIt.next();
      for(int j = 0;j<attrNames.length;j++){
	mRNAMeasurement tempmRNA = expressionData.getMeasurement(nodeAttributes.getCanonicalName(current),attrNames[j]);
	if(tempmRNA == null){
	  tempArray[j] = ZStatistics.oneMinusNormalCDFInverse(.5);
	}
	else{
	  double sigValue = tempmRNA.getSignificance();
	  if (sigValue < MIN_SIG) {
	    sigValue = MIN_SIG;
	    System.out.println("Warning: value for "+nodeAttributes.getCanonicalName(current)+" adjusted to "+MIN_SIG); 
	  } // end of if ()
	  if (sigValue > MAX_SIG) {
	    sigValue = MAX_SIG;
	    System.out.println("Warning: value for "+nodeAttributes.getCanonicalName(current)+" adjusted to "+MAX_SIG); 
	  } // end of if ()
	  //transform the p-value into a z-value and store it in the array of z scores for this particular node
	  tempArray[j] = ZStatistics.oneMinusNormalCDFInverse(sigValue);
	}
      }
      tempHash.put(current,tempArray);
    }
    System.out.println("Done processing into Hash");
    return tempHash;
  }
 

  protected void showConditionsVsPathwaysTable () {
     tableDialog = new ConditionsVsPathwaysTable (mainFrame,
						  cyNetwork,
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
    ActivePathsFinder apf = new ActivePathsFinder(generateExpressionMap(),attrNames,cyNetwork,apfParams,mainFrame);

    long start = System.currentTimeMillis ();
    Vector result = new Vector();
    Iterator it = cyNetwork.getFlaggedNodes().iterator();
    while(it.hasNext()){
      result.add(it.next());
    }
	
    double score = apf.scoreList(result);
    long duration = System.currentTimeMillis () - start;
    System.out.println ("-------------- back from score: " + duration + " msecs");
    System.out.println ("-------------- score: " + score + " \n");
    JOptionPane.showMessageDialog (mainFrame, "Score: " + score);
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
    //if (activePathToolbarButton != null)
    //  cytoscapeWindow.getCyMenus().getToolBar().remove (activePathToolbarButton);

    //activePathToolbarButton = 
    //  cytoscapeWindow.getCyMenus().getToolBar().add (new ActivePathControllerLauncherAction ());

  } // addActivePathToolbarButton 
    //------------------------------------------------------------------------------
  public void displayPath (Component activePath, boolean clearOthersFirst,
			   String pathTitle)
  {
    titleForCurrentSelection = pathTitle;
    FlagFilter flagger = cyNetwork.getFlagger();
    //cytoscapeWindow.selectNodesByName (activePath.getNodes (), clearOthersFirst);
    if(clearOthersFirst){
      //cyNetwork.unFlagAllNodes();
      flagger.unflagAllNodes();
    }
    flagger.setFlaggedNodes(activePath.getNodes(),true);
    //cyNetwork.setFlaggedNodes(activePath.getNodes(),true);
    
  }
  //------------------------------------------------------------------------------
  public void displayPath (Component activePath, String pathTitle)
  {
    displayPath (activePath, true, pathTitle);
  }
  //------------------------------------------------------------------------------

} // class ActivePaths (a CytoscapeWindow plugin)
