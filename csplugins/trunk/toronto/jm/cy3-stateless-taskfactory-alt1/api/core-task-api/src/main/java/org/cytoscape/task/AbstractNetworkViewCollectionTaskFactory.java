package org.cytoscape.task;

import java.util.Collection;

import org.cytoscape.view.model.CyNetworkView;

public abstract class AbstractNetworkViewCollectionTaskFactory implements NetworkViewCollectionTaskFactory {
	@Override
	public boolean isReady(Collection<CyNetworkView> networkViews) {
		return true;
	}
}
