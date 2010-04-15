

package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
class AbstractNetworkEvent extends AbstractCyEvent<CyNetworkManager> {
	private final CyNetwork net;
	AbstractNetworkEvent(final CyNetworkManager source, final Class listenerClass, final CyNetwork net) {
		super(source, listenerClass);
		this.net = net;
	}

	public CyNetwork getNetwork() {
		return net;
	}
}
