package CyAnimator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

public class CyAnimator extends CytoscapePlugin {

	/**
	 * @param args
	 */
	public CyAnimator() {
		
		JMenuItem item = new JMenuItem("CyAnimator");
		
		item.addActionListener(new CyAnimatorCommandListener());
		
		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins");
		
		pluginMenu.add(item);
		
		
	}

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
}
