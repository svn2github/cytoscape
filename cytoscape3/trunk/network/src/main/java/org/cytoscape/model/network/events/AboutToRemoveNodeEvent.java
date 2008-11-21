
package org.cytoscape.model.events;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.event.CyEvent;

/**
 * Fired before a node is actually removed so that listeners
 * have a chance to clean up before the node object disappaears.
 */
public interface AboutToRemoveNodeEvent extends CyEvent<CyNetwork> {
	public CyNode getNode();
}
