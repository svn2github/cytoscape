package edu.ucsd.bioeng.idekerlab.biomartui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import edu.ucsd.bioeng.idekerlab.biomartui.ui.BiomartMainDialog;

public class BiomartUIPlugin extends CytoscapePlugin {
	
	public BiomartUIPlugin() {

		
		final JMenu menu = new JMenu("Import Attributes from Biomart");

		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("File.Import")
				.add(menu);
		
		menu.add(new JMenuItem(new AbstractAction("ID Mapping") {
			public void actionPerformed(ActionEvent e) {
				BiomartMainDialog.showUI();
			}
		}));
		
	}
	
	
	
}
