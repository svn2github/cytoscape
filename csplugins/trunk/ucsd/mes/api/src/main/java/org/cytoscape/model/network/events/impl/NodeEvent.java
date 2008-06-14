

package org.cytoscape.model.network.events.impl;

import org.cytoscape.model.network.CyNode;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.model.network.events.AboutToRemoveNodeEvent;
import org.cytoscape.model.network.events.AddedNodeEvent;

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
