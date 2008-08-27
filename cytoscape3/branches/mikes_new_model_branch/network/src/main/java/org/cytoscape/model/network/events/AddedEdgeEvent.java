
package org.cytoscape.model.network.events;

import org.cytoscape.model.network.CyEdge;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.event.CyEvent;

public interface AddedEdgeEvent extends CyEvent<CyNetwork> {
	public CyEdge getEdge();
}
