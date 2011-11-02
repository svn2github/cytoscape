package csplugins.jActiveModules;

import org.cytoscape.application.swing.events.CytoPanelComponentSelectedEvent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;

import csplugins.jActiveModules.dialogs.ActivePathsParameterPanel;


// Note: THis class does nothing, should be removed.
public class ActiveModulesPanelSelectedListener implements CytoPanelComponentSelectedListener {
	private ActivePathsParameterPanel mainPanel;

	public ActiveModulesPanelSelectedListener(ActivePathsParameterPanel mainPanel, int c) {
		this.mainPanel = mainPanel;
	}
	
	@Override
	public void handleEvent(CytoPanelComponentSelectedEvent e) {
		if (e.getCytoPanel().getSelectedComponent() == mainPanel) {
			//mainPanel.handlePanelSelected();
		}
	}
}
