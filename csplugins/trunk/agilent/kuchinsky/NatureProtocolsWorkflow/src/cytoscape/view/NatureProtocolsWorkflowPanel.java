
package cytoscape.view;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

/**
 * Build a  workflow panel for Nature Protocols
 * # Load networks.
    * Agilent LitSearch
    * cPath
    * Pathway Commons?
    *  IntAct and/or DIP? 
*
*# Load attribute, synonym, and annotation information about the networks.
    * GO annotations
    * attribute files
    * synonyms?
    * Gene expression data (ArrayExpress?) 
* # Analyze the networks.
*
    * MCODE
    * jActiveModules
    * BiNGO
    * Cytoscape's built in tools like filters, selection, etc.. 
*
* Make joural quality images and publication materials.

    * Export graphics
    * Export session to web
 * @author allankuchinsky
 *
 */

public class NatureProtocolsWorkflowPanel extends CytoscapePlugin {

	public NatureProtocolsWorkflowPanel () {
		NewWorkflowPanelAction mainAction = new NewWorkflowPanelAction ();
		Cytoscape.getDesktop().getCyMenus().getViewMenu().add(mainAction);
		mainAction.actionPerformed(null);		
	}

	// ~ Inner Classes
	// //////////////////////////////////////////////////////////

	public class NewWorkflowPanelAction extends CytoscapeAction {
		/**
		 * Creates a new NewWorkflowPanelAction object.
		 */
		public NewWorkflowPanelAction() {
			super("Nature Protocols Workflow");
			setPreferredMenu("View");
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @param e DOCUMENT ME!
		 */
//		public void actionPerformed(ActionEvent e) {
//	        JPanel pane = new JPanel();
//	        pane.setMinimumSize(new Dimension (250, 200));
////	        pane.add( BorderLayout.LINE_START, new WorkflowTree());
//	        pane.add( BorderLayout.NORTH, new WorkflowTree());
//
//	        
//	        
//	        //	        pane.setPreferredSize(new Dimension (new Dimension (2 * Cytoscape.getDesktop().getNetworkPanel().getWidth(),
////	        		Cytoscape.getDesktop().getNetworkPanel().getHeight())));
////	        pane.setAlignmentX(Component.CENTER_ALIGNMENT);
//	     
//	        CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
//	    
//	     
//	        
////	        System.out.println ("EAST Cytopanel is: " + cytoPanel);
////	        System.out.println ("State of EAST CYTOPANEL is: " + cytoPanel.getState());
//	        cytoPanel.setState(CytoPanelState.DOCK);
////	        System.out.println ("after DOCK command, State of EAST CYTOPANEL is: " + cytoPanel.getState());
//	        
//	        cytoPanel.add("Nature Protocols Workflow", pane);
//	        
//	        Object parent = ((Component) cytoPanel).getParent();
//	        System.out.println ("Parent of Cytopanel = " + parent);
//	       			
//			
//		}
		

		public void actionPerformed(ActionEvent e) {
			CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
			cytoPanel.add("Nature Protocols Workflow", new WorkflowTree());
			cytoPanel.setState(CytoPanelState.DOCK);
		}		
		
	}
}

