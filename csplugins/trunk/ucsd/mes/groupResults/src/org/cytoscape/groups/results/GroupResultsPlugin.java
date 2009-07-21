package org.cytoscape.groups.results;

import javax.swing.SwingConstants;

import org.cytoscape.groups.results.internal.ui.GroupPanel;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.plugin.CytoscapePlugin;

public class GroupResultsPlugin extends CytoscapePlugin 
{ 

	//private GroupPanel groupPanel; 
	//private CytoPanel cytoPanel; 

	public GroupResultsPlugin() {

		// Under normal circumstances, we might create a menu and perhaps
		// setting up node context menu listeners, etc.

		// Add our interface to results CytoPanel 
		//groupPanel = new GroupPanel();
		//cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		//cytoPanel.add("Module Finding Results", groupPanel);
		
		new GroupHandler();
		
	}

}

