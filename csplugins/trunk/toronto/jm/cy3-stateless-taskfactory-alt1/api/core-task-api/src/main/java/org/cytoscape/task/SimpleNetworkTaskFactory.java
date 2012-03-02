package org.cytoscape.task;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskIterator;

public abstract class SimpleNetworkTaskFactory implements NetworkTaskFactory<Object> {
	@Override
	public final TaskIterator createTaskIterator(Object tunableContext, CyNetwork network) {
		return createTaskIterator(network);
	}
	
	@Override
	public final boolean isReady(Object tunableContext, CyNetwork network) {
		return isReady(network);
	}
	
	protected boolean isReady(CyNetwork network) {
		return true;
	}

	@Override
	public Object createTunableContext() {
		return null;
	}

	protected abstract TaskIterator createTaskIterator(CyNetwork network);
}
