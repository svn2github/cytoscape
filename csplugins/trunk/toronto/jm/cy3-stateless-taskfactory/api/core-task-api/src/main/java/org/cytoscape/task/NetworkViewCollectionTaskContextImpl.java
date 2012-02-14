package org.cytoscape.task;

import java.util.Collection;

import org.cytoscape.view.model.CyNetworkView;

public class NetworkViewCollectionTaskContextImpl implements NetworkViewCollectionTaskContext {
	/** The collection of network views to be passed into any tasks constructed by descendants of this class. */
	protected Collection<CyNetworkView> networkViews;

	@Override
	public void setNetworkViewCollection(final Collection<CyNetworkView> networkViews) {
		if (networkViews == null)
			throw new NullPointerException("CyNetworkView Colleciton is null");
		this.networkViews = networkViews;
	}

	@Override
	public Collection<CyNetworkView> getNetworkViewCollection() {
		return networkViews;
	}
}
