
package org.cytoscape.model.events;

import org.cytoscape.event.CyListener;

/**
 * Listener for Selected Nodes.
 */
public interface SelectedNodesListener extends CyListener {
	public void handleEvent(SelectedNodesEvent e);
}
