
package org.cytoscape.model.network.events;

import org.cytoscape.model.network.CyEdge;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.event.CyEvent;

/**
 * Fired before an edge is actually removed so that listeners
 * have a chance to clean up before the edge object disappaears.
 */
public interface AboutToRemoveEdgeEvent extends CyEvent<CyNetwork> {
	public CyEdge getEdge();
}
