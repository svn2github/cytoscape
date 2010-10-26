package CyAnimator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;



public class CyAnimator extends CytoscapePlugin {
	static public CyLogger logger = CyLogger.getLogger(CyAnimator.class);
		
	/**
	 * @param args
	 */
	public CyAnimator() {
		JMenuItem item = new JMenuItem("CyAnimator");
		item.addActionListener(new CyAnimatorCommandListener());
		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins");
		pluginMenu.add(item);
		//CyAnimatorAction action = new CyAnimatorAction();
		//action.setPreferredMenu("Plugins");
		//Cytoscape.getDesktop().getCyMenus().addAction(action);
		

    }		
	
	//class CyAnimatorAction extends CytoscapeAction {
	class CyAnimatorCommandListener implements ActionListener {

		//public CyAnimatorAction() {super("CyAnimator"); }	
		
		public CyAnimatorCommandListener(){}
		
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
