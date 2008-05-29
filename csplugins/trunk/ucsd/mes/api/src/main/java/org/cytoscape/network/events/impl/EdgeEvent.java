

package org.cytoscape.network.events.impl;

import org.cytoscape.network.CyEdge;
import org.cytoscape.network.CyNetwork;
import org.cytoscape.network.events.AboutToRemoveEdgeEvent;
import org.cytoscape.network.events.AddedEdgeEvent;

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
