package org.cytoscape.task;

import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public abstract class SimpleEdgeViewTaskFactory implements EdgeViewTaskFactory {
	@Override
	public boolean isReady(View<CyEdge> EdgeView, CyNetworkView networkView) {
		return true;
	}
}
