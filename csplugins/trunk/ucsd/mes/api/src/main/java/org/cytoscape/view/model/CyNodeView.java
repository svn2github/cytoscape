package org.cytoscape.view.model;

import org.cytoscape.model.CyNode;

/**
 * Contains the visual representation of a node.
 */
public interface CyNodeView extends View {
	public CyNetworkView getNetworkView();
	public CyNode getNode();
}
