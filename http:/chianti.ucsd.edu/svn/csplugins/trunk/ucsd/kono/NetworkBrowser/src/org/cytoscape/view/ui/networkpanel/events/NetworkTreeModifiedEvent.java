package org.cytoscape.view.ui.networkpanel.events;

import org.jdesktop.swingx.treetable.TreeTableNode;

public interface NetworkTreeModifiedEvent {
	
	public  TreeTableNode[] getTreePath();

}
