
package org.cytoscape.model.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for AboutToRemoveEdgeEvents.
 */
public interface AboutToRemoveEdgeListener extends CyEventListener {
	public void handleEvent(AboutToRemoveEdgeEvent e);
}
