package org.cytoscape.task;

import org.cytoscape.model.CyNetwork;

public abstract class SimpleNetworkTaskFactory implements NetworkTaskFactory {
	@Override
	public boolean isReady(CyNetwork network) {
		return true;
	}
}
