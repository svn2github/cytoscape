
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.event.AbstractCyEvent;

/**
 * 
 */
public final class NetworkViewDestroyedEvent extends AbstractCyEvent<CyNetworkManager> {
	public NetworkViewDestroyedEvent(final CyNetworkManager source) {
		super(source, NetworkViewDestroyedListener.class);
	}
}
