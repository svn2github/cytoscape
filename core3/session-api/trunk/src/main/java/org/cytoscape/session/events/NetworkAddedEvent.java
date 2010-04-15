
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public final class NetworkAddedEvent extends AbstractNetworkEvent {
	public NetworkAddedEvent(final CyNetworkManager source, final CyNetwork net) {
		super(source, NetworkAddedListener.class, net);
	}
}
