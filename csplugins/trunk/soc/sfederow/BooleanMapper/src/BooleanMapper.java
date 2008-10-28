package src;

/* 
 * Steve Federowicz 
 * Google Summer of Code
 * 
 * This is the driver class for the entire program.  Essentially it just adds the Boolean Mapper
 * to the plugins menu and sets up an action command to fire the dialog window in BooleanSettingsDialog.
 * 
 */
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;




import giny.model.Node;
import giny.view.NodeView;


import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.view.CyNetworkView;
import cytoscape.data.Semantics;

import cytoscape.layout.*;



public class BooleanMapper extends CytoscapePlugin {
	
	
	public BooleanMapper() {
		
		JMenuItem item = new JMenuItem("Boolean Mapper");
		
		item.addActionListener(new BooleanSettingsDialog());
		
		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins");
		
		pluginMenu.add(item);
		
		
	}
	
	
	class BooleanMapperCommandListener implements ActionListener {
		//BooleanAlgorithm alg = null;

		//public BooleanMapperCommandListener(BooleanAlgorithm algorithm) {
			//this.alg = algorithm;
		//}

		public void actionPerformed(ActionEvent e) {
			//if (alg != null) {
				// Create the dialog
				BooleanSettingsDialog  settingsDialog = new BooleanSettingsDialog();
				// Pop it up
				settingsDialog.actionPerformed(e);
			//} 
		}
	}
}

	


