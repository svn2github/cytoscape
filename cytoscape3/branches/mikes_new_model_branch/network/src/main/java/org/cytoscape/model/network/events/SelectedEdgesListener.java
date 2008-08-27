
package org.cytoscape.model.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for Selected Nodes.
 */
public interface SelectedEdgesListener extends CyEventListener {
	public void handleEvent(SelectedEdgesEvent e);
}
