package org.cytoscape.task;

import java.util.Collection;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;

public abstract class SimpleNetworkViewCollectionTaskFactory implements NetworkViewCollectionTaskFactory<Object> {
	@Override
	public final TaskIterator createTaskIterator(Object tunableContext, Collection<CyNetworkView> networkViews) {
		return createTaskIterator(networkViews);
	}
	
	@Override
	public final boolean isReady(Object tunableContext, Collection<CyNetworkView> networkViews) {
		return isReady(networkViews);
	}
	
	protected boolean isReady(Collection<CyNetworkView> networkViews) {
		return true;
	}

	@Override
	public Object createTunableContext() {
		return null;
	}
	
	protected abstract TaskIterator createTaskIterator(Collection<CyNetworkView> networkViews);
}
