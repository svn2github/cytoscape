
package org.cytoscape.model.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for Selected Nodes.
 */
public interface SelectedNodesListener extends CyEventListener {
	public void handleEvent(SelectedNodesEvent e);
}
