

package org.cytoscape.model.events;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.AboutToRemoveEdgeEvent;
import org.cytoscape.model.events.AddedEdgeEvent;

/**
 * 
 */
public class EdgeEvent extends NetEvent<CyEdge> implements AboutToRemoveEdgeEvent, AddedEdgeEvent {

	public EdgeEvent(CyEdge e, CyNetwork n) {
		super(e,n);
	}

	public CyEdge getEdge() {
		return get();
	}
}
