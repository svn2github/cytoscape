
package org.cytoscape.model.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for Selected Nodes.
 */
public interface SelectedNodesListener extends CyEventListener {
	public void handleEvent(SelectedNodesEvent e);
}
