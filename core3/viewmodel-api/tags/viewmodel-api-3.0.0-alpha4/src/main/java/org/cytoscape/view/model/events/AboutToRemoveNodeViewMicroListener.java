package org.cytoscape.view.model.events;


import org.cytoscape.event.CyMicroListener;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;


/** Micro Listener for node view destruction. */
public interface AboutToRemoveNodeViewMicroListener extends CyMicroListener {
	void nodeViewAboutToBeRemoved(final View<CyNode> nodeView, final CyNetworkView networkView);
}
