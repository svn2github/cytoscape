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
import cytoscape.plugin.*;
import csplugins.jActiveModules.data.*;
import csplugins.jActiveModules.dialogs.*;

//------------------------------------------------------------------------------
/**
 * UI for Active Modules. Manages the various menu items
 */
public class ActiveModulesUI extends AbstractPlugin {

  protected CyWindow cytoscapeWindow;
  protected ActivePaths activePaths;
  protected ActivePathFinderParameters apfParams;

  public ActiveModulesUI (CyWindow cytoscapeWindow) {
    System.out.println("Starting jActiveModules plugin!\n");
    /* initialize variables */
    this.cytoscapeWindow = cytoscapeWindow;
	
    /* Add function calls to Cytoscape menus */
    cytoscapeWindow.getCyMenus().getOperationsMenu().add ( new SetParametersAction() );
    cytoscapeWindow.getCyMenus().getOperationsMenu().add ( new FindActivePathsAction () );
    cytoscapeWindow.getCyMenus().getOperationsMenu().add ( new ScoreSubComponentAction () );
    cytoscapeWindow.getCyMenus().getOperationsMenu().add ( new RandomizeAndRunAction () );

    /* check for command line arguments to run right away */
    String[] args = cytoscapeWindow.getCytoscapeObj().getConfiguration().getArgs();
    ActivePathsCommandLineParser parser = new ActivePathsCommandLineParser(args);
    apfParams = parser.getActivePathFinderParameters();
    activePaths = new ActivePaths(cytoscapeWindow,apfParams);
    if (parser.shouldRunActivePaths()) {
      Thread t = new Thread(activePaths);
      t.start();
    }
  }

  /**
   * Description of the plugin
   */
  public String describe () {
    String desc = "ActiveModules is a plugin that searches a molecular " + 
      "interaction network to find expression activated subnetworks, " +
      "i.e., modules.";
    return desc;
  }

  /**
   * Action to allow the user to change the current options
   * for running jActiveModules, wiht a gui interface
   */
  protected class SetParametersAction extends AbstractAction {
    public SetParametersAction(){
      super("Active Modules: Set Parameters");
    }

    public void actionPerformed(ActionEvent e){
      JFrame mainFrame = cytoscapeWindow.getMainFrame ();
      JDialog paramsDialog = new ActivePathsParametersPopupDialog 
	(mainFrame, "Find Active Modules Parameters", apfParams);
      paramsDialog.pack ();
      paramsDialog.setLocationRelativeTo (mainFrame);
      paramsDialog.setVisible (true);
    }
  }

  /**
   * This action will run activePaths with the current parameters
   */
  protected class FindActivePathsAction extends AbstractAction{  
    
    FindActivePathsAction () { super ("Active Modules: Find Modules"); }
	
    public void actionPerformed (ActionEvent e) {
  	  Thread t = new Thread(activePaths);
	  t.start();
    } 
  } 
    
  /**
   * This action will generate a score for the currently selected
   * nodes in the view
   */
  protected class ScoreSubComponentAction extends AbstractAction {
	
    ScoreSubComponentAction () { super ("Active Modules: Score Selected Nodes"); }
    public void actionPerformed (ActionEvent e) {
  	activePaths.scoreActivePath ();
    } 
  }


  protected class RandomizeAndRunAction extends AbstractAction{  

    public RandomizeAndRunAction () { super ("Active Modules: Score Distribution"); }

    public void actionPerformed (ActionEvent e) {
      JFrame mainFrame = cytoscapeWindow.getMainFrame ();
      Thread t = new ScoreDistributionThread(cytoscapeWindow,activePaths,apfParams);
      t.start();	
    }
  }
}
