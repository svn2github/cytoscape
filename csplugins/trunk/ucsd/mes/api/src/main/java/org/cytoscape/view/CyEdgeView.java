package org.cytoscape.view;

import org.cytoscape.model.network.CyEdge;

/**
 * Contains the visual representation of an Edge.
 */
public interface CyEdgeView extends View {
	public CyNetworkView getNetworkView();
	public CyEdge getEdge();

	// we need to support edge bends
//	public Bend getBend();
//	public void clearBends();
}
