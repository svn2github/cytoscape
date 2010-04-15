
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.event.AbstractCyEvent;

/**
 * 
 */
public final class NetworkDestroyedEvent extends AbstractCyEvent<CyNetworkManager> {
	public NetworkDestroyedEvent(final CyNetworkManager source) {
		super(source, NetworkDestroyedListener.class);
	}
}
