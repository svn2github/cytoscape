package org.cytoscape.task;

import org.cytoscape.view.model.CyNetworkView;

public abstract class AbstractNetworkViewTaskFactory implements NetworkViewTaskFactory {
	@Override
	public boolean isReady(CyNetworkView networkView) {
		return true;
	}
}
