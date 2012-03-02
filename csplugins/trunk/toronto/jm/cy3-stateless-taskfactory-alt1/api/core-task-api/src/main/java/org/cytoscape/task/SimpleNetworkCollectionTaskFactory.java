package org.cytoscape.task;

import java.util.Collection;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskIterator;

public abstract class SimpleNetworkCollectionTaskFactory implements NetworkCollectionTaskFactory<Object> {
	@Override
	public final TaskIterator createTaskIterator(Object tunableContext, Collection<CyNetwork> networks) {
		return createTaskIterator(networks);
	}
	
	@Override
	public final boolean isReady(Object tunableContext, Collection<CyNetwork> networks) {
		return isReady(networks);
	}

	protected boolean isReady(Collection<CyNetwork> networks) {
		return true;
	}

	@Override
	public Object createTunableContext() {
		return null;
	}
	
	protected abstract TaskIterator createTaskIterator(Collection<CyNetwork> networks);
}
