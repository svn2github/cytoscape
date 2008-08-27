
package org.cytoscape.model.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for Selected Nodes.
 */
public interface UnselectedNodesListener extends CyEventListener {
	public void handleEvent(UnselectedNodesEvent e);
}
