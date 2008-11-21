
package org.cytoscape.model.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for Selected Nodes.
 */
public interface SelectedEdgesListener extends CyEventListener {
	public void handleEvent(SelectedEdgesEvent e);
}
