package org.idekerlab.PanGIAPlugin;

import java.awt.Component;
import javax.swing.Icon;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.idekerlab.PanGIAPlugin.ui.SearchPropertyPanel;

public class PanGIACytoPanelComponent implements CytoPanelComponent {

	SearchPropertyPanel panel;
	
	public PanGIACytoPanelComponent(SearchPropertyPanel panel) {
		this.panel = panel; 
	}
	
	@Override
	public Component getComponent() {
		return panel;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	@Override
	public String getTitle() {
		return "PanGIA";
	}

	@Override
	public Icon getIcon() {
		return null;
	}

}
