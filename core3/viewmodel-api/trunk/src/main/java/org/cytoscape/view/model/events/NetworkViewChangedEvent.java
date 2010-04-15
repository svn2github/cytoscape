
package org.cytoscape.view.model.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;

/**
 * 
 */
public final class NetworkViewChangedEvent extends AbstractCyEvent<CyNetworkView> {
	public NetworkViewChangedEvent(final CyNetworkView source) {
		super(source,NetworkViewChangedListener.class);
	}
}
