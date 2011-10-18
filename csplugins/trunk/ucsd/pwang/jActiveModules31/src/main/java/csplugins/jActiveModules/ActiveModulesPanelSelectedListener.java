package csplugins.jActiveModules;

import org.cytoscape.application.swing.events.CytoPanelComponentSelectedEvent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;

import csplugins.jActiveModules.dialogs.ActivePathsParameterPanel;

public class ActiveModulesPanelSelectedListener implements CytoPanelComponentSelectedListener {
	private ActivePathsParameterPanel mainPanel;

	public ActiveModulesPanelSelectedListener(ActivePathsParameterPanel mainPanel) {
		this.mainPanel = mainPanel;
	}
	
	@Override
	public void handleEvent(CytoPanelComponentSelectedEvent e) {
		if (e.getCytoPanel().getSelectedComponent() == mainPanel) {
			mainPanel.handlePanelSelected();
		}
	}
}
