package CyAnimator;

import java.util.*;
import java.awt.event.ActionEvent;


import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;




public class CyAnimator extends CytoscapePlugin {

		
	/**
	 * @param args
	 */
	public CyAnimator() {
		CyAnimatorAction action = new CyAnimatorAction();
		action.setPreferredMenu("Plugins");
		Cytoscape.getDesktop().getCyMenus().addAction(action);
		

    }		
	
	class CyAnimatorAction extends CytoscapeAction {
		

		public CyAnimatorAction() {super("CyAnimator"); }	
		
		public void actionPerformed(ActionEvent e) {
			//create the dialog
			CyAnimatorDialog animationDialog = new CyAnimatorDialog();
			// Pop it up
			animationDialog.actionPerformed(e);
			 
		}
		
	}
	/**
	 * @param args
	 */
	/*public CyAnimator() {
		
		JMenuItem item = new JMenuItem("CyAnimator");
		
		item.addActionListener(new CyAnimatorCommandListener());
		
		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins");
		
		pluginMenu.add(item);
			
	}
	*/
	/*
	class CyAnimatorCommandListener implements ActionListener {
		//BooleanAlgorithm alg = null;

		public CyAnimatorCommandListener() {
			//this.alg = algorithm;
		}

		public void actionPerformed(ActionEvent e) {
			//if (alg != null) {
				// Create the dialog
				CyAnimatorDialog  animationDialog = new CyAnimatorDialog();
				// Pop it up
				animationDialog.actionPerformed(e);
			//} 
		}
	}
	*/
}
