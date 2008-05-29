
package org.cytoscape.network.events;

import org.cytoscape.network.CyNode;
import org.cytoscape.network.CyNetwork;
import org.cytoscape.event.CyEvent;

public interface AddedNodeEvent extends CyEvent<CyNetwork> {
	public CyNode getNode();
}
