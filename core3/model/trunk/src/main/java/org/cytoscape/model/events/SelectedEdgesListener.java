
package org.cytoscape.model.events;

import org.cytoscape.event.CyListener;

/**
 * Listener for Selected Nodes.
 */
public interface SelectedEdgesListener extends CyListener {
	public void handleEvent(SelectedEdgesEvent e);
}
