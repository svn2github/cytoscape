package org.cytoscape.view.ui.networkpanel.internal;

import cytoscape.CyNetwork;
import cytoscape.groups.CyGroup;


public class GroupTreeNode extends NetworkTreeNode {
	
	private CyNetwork parent;

	public GroupTreeNode(CyNetwork parent, CyGroup group, String id, int columnSize) {
		super(group, id, columnSize);
		this.parent = parent;
	}
	
	@Override
	public TreeObjectType getObjectType() {
		return TreeObjectType.GROUP;
	}
	
	public CyNetwork getParentNetwork() {
		return parent;
	}

}
