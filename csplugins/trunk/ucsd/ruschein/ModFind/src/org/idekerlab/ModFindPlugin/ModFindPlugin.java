package org.idekerlab.ModFindPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

import org.idekerlab.ModFindPlugin.ui.SearchPropertyPanel;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

/**
 * 
 * @author kono, ruschein
 *
 */
public class ModFindPlugin extends CytoscapePlugin {

	// Main GUI Panel for this plugin.  Should be a singleton.
	private SearchPropertyPanel searchPanel;


	public ModFindPlugin() {
		final JMenuItem menuItem = new JMenuItem("ModFind...");
		menuItem.addActionListener(new PluginAction());
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu(
				"Plugins.Module Finders...").add(menuItem);
	}

	class PluginAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (searchPanel == null)
				searchPanel = new SearchPropertyPanel();

			final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(
					SwingConstants.WEST);
			int index = cytoPanel.indexOfComponent(searchPanel);
			if (index < 0) {
				searchPanel.updateState();
				searchPanel.setVisible(true);
				cytoPanel.add("ModFind", searchPanel);
				index = cytoPanel.indexOfComponent(searchPanel);
			}
			cytoPanel.setSelectedIndex(index);
			cytoPanel.setState(CytoPanelState.DOCK);
		}
	}
}
