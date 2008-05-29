

package org.cytoscape.network.events.impl;

import org.cytoscape.network.CyNode;
import org.cytoscape.network.CyNetwork;
import org.cytoscape.network.events.AboutToRemoveNodeEvent;
import org.cytoscape.network.events.AddedNodeEvent;

/**
 * 
 */
public class NodeEvent extends NetEvent<CyNode> implements AboutToRemoveNodeEvent, AddedNodeEvent {

	public NodeEvent(CyNode e, CyNetwork n) {
		super(e,n);
	}

	public CyNode getNode() {
		return get();
	}
}
