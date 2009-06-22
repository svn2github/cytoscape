package org.cytoscape.groups.results;

import javax.swing.SwingConstants;

import org.cytoscape.groups.results.internal.GroupPanel;

import cytoscape.Cytoscape;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.plugin.CytoscapePlugin;

// Cytoscape group system imports
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupChangeListener;

public class GroupResultsPlugin extends CytoscapePlugin 
                               implements CyGroupChangeListener { 

	private GroupPanel groupPanel; 
	private CytoPanel cytoPanel; 

	public GroupResultsPlugin() {

		// Under normal circumstances, we might create a menu and perhaps
		// setting up node context menu listeners, etc.

		// Add our interface to results CytoPanel 
		groupPanel = new GroupPanel();
		cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		cytoPanel.add("Module Finding Results", groupPanel);

		CyGroupManager.addGroupChangeListener(this);
	}

	public void groupChanged(CyGroup group, CyGroupChangeListener.ChangeType change) { 
		if ( change == CyGroupChangeListener.ChangeType.GROUP_CREATED ) {
			groupPanel.groupCreated(group);

			// set visible
			if ( cytoPanel.getState() == CytoPanelState.HIDE )
				cytoPanel.setState( CytoPanelState.DOCK );
		} else if ( change == CyGroupChangeListener.ChangeType.GROUP_DELETED ) {
			groupPanel.groupRemoved(group);
		} else if ( change == CyGroupChangeListener.ChangeType.GROUP_MODIFIED ) {
			groupPanel.groupChanged(group);
		} else {
			System.err.println("unsupported change type: " + change);
		}
	}
}

