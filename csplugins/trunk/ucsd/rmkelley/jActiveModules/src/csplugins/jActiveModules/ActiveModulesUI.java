//ActiveModulesUI.java
//------------------------------------------------------------------------------
package csplugins.jActiveModules;
//------------------------------------------------------------------------------

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import csplugins.jActiveModules.data.ActivePathFinderParameters;
import csplugins.jActiveModules.dialogs.ActivePathsParametersPopupDialog;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

//------------------------------------------------------------------------------
/**
 * UI for Active Modules. Manages the various menu items
 */
public class ActiveModulesUI extends CytoscapePlugin {

  protected ActivePaths activePaths;
  protected ActivePathFinderParameters apfParams;

  public ActiveModulesUI () {
    System.err.println("Starting jActiveModules plugin!\n");
    /* initialize variables */
    JMenu topMenu = new JMenu("jActiveModules");
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(topMenu);

    /* Add function calls to Cytoscape menus */
    topMenu.add ( new SetParametersAction() );
    topMenu.add ( new FindActivePathsAction () );
    //topMenu.add ( new ScoreSubComponentAction () );
    topMenu.add ( new RandomizeAndRunAction () );

    //cytoscapeWindow.getCyMenus().getOperationsMenu().add ( new ScoreSubComponentAction () );
    //cytoscapeWindow.getCyMenus().getOperationsMenu().add ( new RandomizeAndRunAction () );

    /* check for command line arguments to run right away */
    String[] args = Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
    //String [] args = CytoscapeInit.getArgs();
    ActivePathsCommandLineParser parser = new ActivePathsCommandLineParser(args);
    apfParams = parser.getActivePathFinderParameters();
    if (parser.shouldRunActivePaths()) {
      activePaths = new ActivePaths(Cytoscape.getCurrentNetwork(),apfParams);
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
      JFrame mainFrame = Cytoscape.getDesktop();
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
	
    public void actionPerformed (ActionEvent ae) {
      try{
	activePaths = new ActivePaths(Cytoscape.getCurrentNetwork(),apfParams);  
	Thread t = new Thread(activePaths);
	t.start();
      }
      catch(Exception e){
	JOptionPane.showMessageDialog(Cytoscape.getDesktop(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  } 
     
  /**
   * This action will generate a score for the currently selected
   * nodes in the view
   */
  protected class ScoreSubComponentAction extends AbstractAction {
	
     ScoreSubComponentAction () { super ("Active Modules: Score Selected Nodes"); }
     public void actionPerformed (ActionEvent e) {
       activePaths = new ActivePaths(Cytoscape.getCurrentNetwork(),apfParams);  
       activePaths.scoreActivePath ();
     } 
   }


   protected class RandomizeAndRunAction extends AbstractAction{  

     public RandomizeAndRunAction () { super ("Active Modules: Score Distribution"); }
     
     public void actionPerformed (ActionEvent e) {
       JFrame mainFrame = Cytoscape.getDesktop().getMainFrame ();
       activePaths = new ActivePaths(Cytoscape.getCurrentNetwork(),apfParams);
       Thread t = new ScoreDistributionThread(Cytoscape.getCurrentNetwork(),activePaths,apfParams);
       t.start();	
     }
   }
}
