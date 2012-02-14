package org.cytoscape.task;

import java.util.Collection;

import org.cytoscape.view.model.CyNetworkView;

public interface NetworkViewCollectionTaskContext {

	/** Provisions descendants of this class with a collection of network views to pass into constructed tasks.
	 *  @param networkViews  must be a non-null collection of network views
	 */
	void setNetworkViewCollection(Collection<CyNetworkView> networkViews);

	Collection<CyNetworkView> getNetworkViewCollection();

}
