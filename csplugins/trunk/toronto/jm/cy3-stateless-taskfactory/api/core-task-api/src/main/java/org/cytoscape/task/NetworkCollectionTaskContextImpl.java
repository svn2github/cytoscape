package org.cytoscape.task;

import java.util.Collection;

import org.cytoscape.model.CyNetwork;

public class NetworkCollectionTaskContextImpl implements NetworkCollectionTaskContext {
	/** The collection of networks that will be passed to any task constructed by descendants of this factory. */
	protected Collection<CyNetwork> networks;

	@Override
	public void setNetworkCollection(final Collection<CyNetwork> networks) {
		if (networks == null)
			throw new NullPointerException("CyNetwork Colleciton is null");
		this.networks = networks;
	}

	@Override
	public Collection<CyNetwork> getNetworkCollection() {
		return networks;
	}
}
