
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public interface NetworkAddedEvent extends CyEvent<CyNetworkManager> {
	CyNetwork getNetwork();
}
