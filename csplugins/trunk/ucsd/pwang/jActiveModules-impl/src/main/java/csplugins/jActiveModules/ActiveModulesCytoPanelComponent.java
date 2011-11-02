package csplugins.jActiveModules;

import java.awt.Component;
import javax.swing.Icon;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import csplugins.jActiveModules.dialogs.ActivePathsParameterPanel;

public class ActiveModulesCytoPanelComponent implements CytoPanelComponent {

	ActivePathsParameterPanel panel;
	
	public ActiveModulesCytoPanelComponent(ActivePathsParameterPanel panel) {
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
		return "jActiveMNodules";
	}

	@Override
	public Icon getIcon() {
		return null;
	}

}
