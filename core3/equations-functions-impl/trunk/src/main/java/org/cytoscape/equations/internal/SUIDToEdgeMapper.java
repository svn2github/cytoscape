package org.cytoscape.equations.internal;


import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.events.AddedEdgeEvent;
import org.cytoscape.model.events.AddedEdgeListener;
import org.cytoscape.model.events.AboutToRemoveEdgeEvent;
import org.cytoscape.model.events.AboutToRemoveEdgeListener;


public class SUIDToEdgeMapper implements AddedEdgeListener, AboutToRemoveEdgeListener {
	private final Map<Long, CyEdge> suidToEdgeMap = new HashMap<Long, CyEdge>();

	public void handleEvent(final AddedEdgeEvent event) {
		final CyEdge edge = event.getEdge();
		suidToEdgeMap.put(edge.getSUID(), edge);
	}

	public void handleEvent(final AboutToRemoveEdgeEvent event) {
		final CyEdge edge = event.getEdge();
		suidToEdgeMap.remove(edge.getSUID());
	}

	public CyEdge getEdge(final Long id) {
		return suidToEdgeMap.get(id);
	}
}