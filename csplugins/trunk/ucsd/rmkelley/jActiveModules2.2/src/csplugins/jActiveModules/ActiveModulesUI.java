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
import java.beans.*;

import csplugins.jActiveModules.data.ActivePathFinderParameters;
import csplugins.jActiveModules.dialogs.ActivePathsParametersPopupDialog;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinitionListener;
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
    String [] args = CytoscapeInit.getArgs();
    ActivePathsCommandLineParser parser = new ActivePathsCommandLineParser(args);
    apfParams = parser.getActivePathFinderParameters();
    AttrChangeListener acl = new AttrChangeListener();
    Cytoscape.getPropertyChangeSupport().addPropertyChangeListener( Cytoscape.ATTRIBUTES_CHANGED, acl );
    Cytoscape.getNodeAttributes().getMultiHashMapDefinition().addDataDefinitionListener( acl );
    if (apfParams.getRun()) {
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
       JFrame mainFrame = Cytoscape.getDesktop();
       activePaths = new ActivePaths(Cytoscape.getCurrentNetwork(),apfParams);
       Thread t = new ScoreDistributionThread(Cytoscape.getCurrentNetwork(),activePaths,apfParams);
       t.start();	
     }
   }

   /**
    * This is used to update the expression attributes in the params object so that
    * they match those that exist in CyAttributes.
    */
   protected class AttrChangeListener implements PropertyChangeListener, 
                                                 MultiHashMapDefinitionListener {

   	public void propertyChange(PropertyChangeEvent e) {
		if ( e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED) ) 
			apfParams.reloadExpressionAttributes();     
	}

	/**
	 * There is no point in listening to attributeDefined events because
	 * this only defines the attr and when this is fired, no attr values
	 * actually exist.
	 */
	public void attributeDefined(String attributeName) { }

	public void attributeUndefined(String attributeName) {
		apfParams.reloadExpressionAttributes();     
	}
   }
}
