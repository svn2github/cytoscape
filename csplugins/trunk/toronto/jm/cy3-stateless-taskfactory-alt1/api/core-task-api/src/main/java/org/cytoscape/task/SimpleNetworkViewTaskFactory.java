package org.cytoscape.task;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public abstract class SimpleNetworkViewTaskFactory implements NetworkViewTaskFactory<Object> {
	@Override
	public final TaskIterator createTaskIterator(Object tunableContext, CyNetworkView networkView) {
		return createTaskIterator(networkView);
	}

	@Override
	public final boolean isReady(Object tunableContext, CyNetworkView networkView) {
		return isReady(networkView);
	}
	
	protected boolean isReady(CyNetworkView networkView) {
		return true;
	}

	@Override
	public Object createTunableContext() {
		return null;
	}
	
	protected abstract TaskIterator createTaskIterator(CyNetworkView networkView);
}
