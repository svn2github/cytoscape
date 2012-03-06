package org.cytoscape.task;

import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public abstract class SimpleEdgeViewTaskFactory implements EdgeViewTaskFactory<Object> {
	@Override
	public final TaskIterator createTaskIterator(Object tunableContext, View<CyEdge> edgeView, CyNetworkView networkView) {
		return createTaskIterator(edgeView, networkView);
	}

	@Override
	public final boolean isReady(Object tunableContext, View<CyEdge> edgeView, CyNetworkView networkView) {
		return isReady(edgeView, networkView);
	}
	
	protected boolean isReady(View<CyEdge> EdgeView, CyNetworkView networkView) {
		return true;
	}

	@Override
	public Object createTunableContext() {
		return null;
	}
	
	protected abstract TaskIterator createTaskIterator(View<CyEdge> edgeView, CyNetworkView networkView);
}
