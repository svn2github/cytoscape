
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.model.CyNetwork;

import java.util.List;

/**
 * 
 */
public final class SetSelectedNetworksEvent extends AbstractCyEvent<CyNetworkManager> {
	private final List<CyNetwork> networks;
	public SetSelectedNetworksEvent(final CyNetworkManager source, final List<CyNetwork> networks) {
		super(source,SetSelectedNetworksListener.class);
		this.networks = networks;
	}

	public List<CyNetwork> getNetworks() {
		return networks;
	}
}
