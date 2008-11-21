
package org.cytoscape.model.events;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;

public interface AddedNodeEvent extends CyEvent<CyNetwork> {
	public CyNode getNode();
}
