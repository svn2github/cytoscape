package org.cytoscape.view;

import org.cytoscape.model.CyNode;

public interface CyNodeView extends View {
	public CyNetworkView getNetworkView();
	public CyNode getNode();
}
