
package org.cytoscape.model.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for Selected Nodes.
 */
public interface UnselectedEdgesListener extends CyEventListener {
	public void handleEvent(UnselectedEdgesEvent e);
}
