
package org.cytoscape.model.events;

import org.cytoscape.event.CyListener;

/**
 * Listener for Selected Nodes.
 */
public interface UnselectedEdgesListener extends CyListener {
	public void handleEvent(UnselectedEdgesEvent e);
}
