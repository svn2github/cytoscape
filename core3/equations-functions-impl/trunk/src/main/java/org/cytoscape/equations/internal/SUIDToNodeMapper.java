package org.cytoscape.equations.internal;


import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.events.AddedNodeEvent;
import org.cytoscape.model.events.AddedNodeListener;
import org.cytoscape.model.events.AboutToRemoveNodeEvent;
import org.cytoscape.model.events.AboutToRemoveNodeListener;


public class SUIDToNodeMapper implements AddedNodeListener, AboutToRemoveNodeListener {
	private final Map<Long, CyNode> suidToNodeMap = new HashMap<Long, CyNode>();

	public void handleEvent(final AddedNodeEvent event) {
		final CyNode edge = event.getNode();
		suidToNodeMap.put(edge.getSUID(), edge);
	}

	public void handleEvent(final AboutToRemoveNodeEvent event) {
		final CyNode edge = event.getNode();
		suidToNodeMap.remove(edge.getSUID());
	}

	public CyNode getNode(final Long id) {
		return suidToNodeMap.get(id);
	}
}