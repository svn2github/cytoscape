
package org.cytoscape.model.events;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;

public interface AddedEdgeEvent extends CyEvent<CyNetwork> {
	public CyEdge getEdge();
}
