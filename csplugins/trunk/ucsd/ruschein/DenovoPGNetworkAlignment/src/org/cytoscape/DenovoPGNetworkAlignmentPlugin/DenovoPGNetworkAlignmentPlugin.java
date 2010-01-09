package org.cytoscape.DenovoPGNetworkAlignmentPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

import org.cytoscape.DenovoPGNetworkAlignmentPlugin.ui.SearchPropertyPanel;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

public class DenovoPGNetworkAlignmentPlugin extends CytoscapePlugin {
	
	private SearchPropertyPanel searchPanel;

	public DenovoPGNetworkAlignmentPlugin() {
		final JMenuItem menuItem = new JMenuItem("DenovoPGNetworkAlignment...");
		menuItem.addActionListener(new PluginAction());
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins.Module Finders...")
				.add(menuItem);
	}

	class PluginAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (searchPanel == null) {
				searchPanel = new SearchPropertyPanel();
			}

			CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(
					SwingConstants.WEST);
			int index = cytoPanel.indexOfComponent(searchPanel);
			if (index < 0) {
				searchPanel.setVisible(true);
				cytoPanel.add("DenovoPGNetworkAlignment", searchPanel);
				index = cytoPanel.indexOfComponent(searchPanel);
			}
			cytoPanel.setSelectedIndex(index);
			cytoPanel.setState(CytoPanelState.DOCK);
		}
	}

	class SearchAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.displayCancelButton(true);
			jTaskConfig.displayCloseButton(false);
			jTaskConfig.displayStatus(true);
			jTaskConfig.displayTimeElapsed(true);
			jTaskConfig.displayTimeRemaining(false);
			jTaskConfig.setAutoDispose(true);
			jTaskConfig.setModal(true);
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			TaskManager.executeTask(new SearchTask(searchPanel.getParameters()), jTaskConfig);
		}
	}
}
