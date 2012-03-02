package org.cytoscape.task;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public abstract class SimpleNodeViewTaskFactory implements NodeViewTaskFactory<Object> {
	@Override
	public final TaskIterator createTaskIterator(Object tunableContext, View<CyNode> nodeView, CyNetworkView networkView) {
		return createTaskIterator(nodeView, networkView);
	}

	@Override
	public final boolean isReady(Object tunableContext, View<CyNode> nodeView, CyNetworkView networkView) {
		return isReady(nodeView, networkView);
	}
	
	protected boolean isReady(View<CyNode> nodeView, CyNetworkView networkView) {
		return true;
	}

	@Override
	public Object createTunableContext() {
		return null;
	}
	
	protected abstract TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView networkView);
}
