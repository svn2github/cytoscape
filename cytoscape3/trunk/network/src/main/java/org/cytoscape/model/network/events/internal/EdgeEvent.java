

package org.cytoscape.model.events.internal;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.AboutToRemoveEdgeEvent;
import org.cytoscape.model.events.AddedEdgeEvent;
import org.cytoscape.model.events.RemovedEdgeEvent;

/**
 * 
 */
public class EdgeEvent extends NetEvent<CyEdge> 
	implements AboutToRemoveEdgeEvent, AddedEdgeEvent, RemovedEdgeEvent {

	public EdgeEvent(CyEdge e, CyNetwork n) {
		super(e,n);
	}

	public CyEdge getEdge() {
		return get();
	}
}
