

package org.cytoscape.model.events.internal;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.AboutToRemoveNodeEvent;
import org.cytoscape.model.events.AddedNodeEvent;
import org.cytoscape.model.events.RemovedNodeEvent;

/**
 * 
 */
public class NodeEvent extends NetEvent<CyNode> 
	implements AboutToRemoveNodeEvent, AddedNodeEvent, RemovedNodeEvent {

	public NodeEvent(CyNode e, CyNetwork n) {
		super(e,n);
	}

	public CyNode getNode() {
		return get();
	}
}
