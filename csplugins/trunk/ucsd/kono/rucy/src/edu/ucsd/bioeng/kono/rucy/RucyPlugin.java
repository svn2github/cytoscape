package edu.ucsd.bioeng.kono.rucy;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import edu.ucsd.bioeng.kono.rucy.ui.SelectScriptDialog;

public class RucyPlugin extends CytoscapePlugin {

	/**
	 * Constructor
	 * 
	 * Will set up the menu entries in the
	 * 
	 * <pre>
	 * Plugins
	 * </pre>
	 * 
	 * menu.
	 * 
	 */
	public RucyPlugin() {

		final JMenu menu = new JMenu("Scripts...");

		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins")
				.add(menu);

		menu.add(new JMenuItem(new AbstractAction("Run Ruby Script") {
			public void actionPerformed(ActionEvent e) {
				SelectScriptDialog.showDialog();

			}
		}));

	}

}