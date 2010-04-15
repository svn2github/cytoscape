
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public final class  NetworkAboutToBeDestroyedEvent extends AbstractNetworkEvent {
	public NetworkAboutToBeDestroyedEvent(final CyNetworkManager source, final CyNetwork net) {
		super(source, NetworkAboutToBeDestroyedListener.class, net);
	}
}
