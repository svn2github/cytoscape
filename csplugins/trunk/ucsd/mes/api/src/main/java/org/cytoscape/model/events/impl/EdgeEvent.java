

package org.cytoscape.model.network.events.impl;

import org.cytoscape.model.network.CyEdge;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.model.network.events.AboutToRemoveEdgeEvent;
import org.cytoscape.model.network.events.AddedEdgeEvent;

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
