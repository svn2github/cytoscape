


package org.cytoscape.model.events.internal;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.SelectedNodesEvent;
import org.cytoscape.model.events.UnselectedNodesEvent;

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
