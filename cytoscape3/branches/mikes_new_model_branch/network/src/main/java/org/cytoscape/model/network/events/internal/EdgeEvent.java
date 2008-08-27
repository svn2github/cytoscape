

package org.cytoscape.model.network.events.internal;

import org.cytoscape.model.network.CyEdge;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.model.network.events.AboutToRemoveEdgeEvent;
import org.cytoscape.model.network.events.AddedEdgeEvent;
import org.cytoscape.model.network.events.RemovedEdgeEvent;

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
