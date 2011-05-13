package org.cytoscape.view.model.events;


import org.cytoscape.event.CyMicroListener;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;


/** Micro Listener for edge view destruction. */
public interface AboutToRemoveEdgeViewMicroListener extends CyMicroListener {
	void edgeViewAboutToBeRemoved(final View<CyEdge> edgeView, final CyNetworkView networkView);
}
