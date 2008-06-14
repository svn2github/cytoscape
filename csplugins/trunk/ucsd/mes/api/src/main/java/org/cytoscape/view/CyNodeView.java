package org.cytoscape.view;

import org.cytoscape.model.network.CyNode;

/**
 * Contains the visual representation of a node.
 */
public interface CyNodeView extends View {
	public CyNetworkView getNetworkView();
	public CyNode getNode();
}
