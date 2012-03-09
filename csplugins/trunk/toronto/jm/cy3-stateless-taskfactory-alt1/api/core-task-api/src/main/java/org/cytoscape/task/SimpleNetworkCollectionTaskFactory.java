package org.cytoscape.task;

import java.util.Collection;

import org.cytoscape.model.CyNetwork;

public abstract class SimpleNetworkCollectionTaskFactory implements NetworkCollectionTaskFactory {
	@Override
	public boolean isReady(Collection<CyNetwork> networks) {
		return true;
	}
}
