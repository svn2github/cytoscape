
package org.cytoscape.model.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for Selected Nodes.
 */
public interface UnselectedEdgesListener extends CyEventListener {
	public void handleEvent(UnselectedEdgesEvent e);
}
