package org.cytoscape.task;

import java.util.Collection;

import org.cytoscape.model.CyNetwork;

public interface NetworkCollectionTaskContext {

	/** Provisions descendants of this factory with a collection of networks to be passed into any constructed tasks.
	 *  @param networks a collection of networks
	 */
	void setNetworkCollection(Collection<CyNetwork> networks);

	Collection<CyNetwork> getNetworkCollection();
}
