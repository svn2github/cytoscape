


package org.cytoscape.model.network.events.internal;

import org.cytoscape.model.network.CyNode;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.model.network.events.SelectedNodesEvent;
import org.cytoscape.model.network.events.UnselectedNodesEvent;

import java.util.List;

/**
 * 
 */
public class MultiNodeEvent extends NetEvent<List<CyNode>> 
	implements SelectedNodesEvent, UnselectedNodesEvent {

	public MultiNodeEvent(List<CyNode> e, CyNetwork n) {
		super(e,n);
	}

	public List<CyNode> getNodeList() {
		return get();
	}
}
