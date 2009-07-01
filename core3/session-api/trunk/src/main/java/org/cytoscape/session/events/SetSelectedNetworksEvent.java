
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.model.CyNetwork;

import java.util.List;

/**
 * 
 */
public interface SetSelectedNetworksEvent extends CyEvent<CyNetworkManager> {
	List<CyNetwork> getNetworks();
}
