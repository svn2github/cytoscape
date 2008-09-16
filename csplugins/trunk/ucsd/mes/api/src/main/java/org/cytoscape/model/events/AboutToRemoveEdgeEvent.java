
package org.cytoscape.model.events;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;

/**
 * Fired before an edge is actually removed so that listeners
 * have a chance to clean up before the edge object disappaears.
 */
public interface AboutToRemoveEdgeEvent extends CyEvent<CyNetwork> {
	public CyEdge getEdge();
}
