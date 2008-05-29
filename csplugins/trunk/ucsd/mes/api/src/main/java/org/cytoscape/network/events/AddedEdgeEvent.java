
package org.cytoscape.network.events;

import org.cytoscape.network.CyEdge;
import org.cytoscape.network.CyNetwork;
import org.cytoscape.event.CyEvent;

public interface AddedEdgeEvent extends CyEvent<CyNetwork> {
	public CyEdge getEdge();
}
