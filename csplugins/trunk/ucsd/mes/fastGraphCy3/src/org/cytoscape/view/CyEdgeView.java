package org.cytoscape.view;

import org.cytoscape.model.CyEdge;

public interface CyEdgeView extends View {
	public CyNetworkView getNetworkView();
	public CyEdge getEdge();

	// we need to support edge bends
//	public Bend getBend();
//	public void clearBends();
}
