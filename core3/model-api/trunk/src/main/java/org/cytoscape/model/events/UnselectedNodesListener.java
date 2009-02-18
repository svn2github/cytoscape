
package org.cytoscape.model.events;

import org.cytoscape.event.CyListener;

/**
 * Listener for Selected Nodes.
 */
public interface UnselectedNodesListener extends CyListener {
	public void handleEvent(UnselectedNodesEvent e);
}
