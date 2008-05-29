
package org.cytoscape.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for AboutToRemoveEdgeEvents.
 */
public interface AboutToRemoveEdgeListener extends CyEventListener<AboutToRemoveEdgeEvent> {
	public void handleEvent(AboutToRemoveEdgeEvent e);
}
