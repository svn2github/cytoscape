
package org.cytoscape.model.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for AboutToRemoveEdgeEvents.
 */
public interface AboutToRemoveEdgeListener extends CyEventListener {
	public void handleEvent(AboutToRemoveEdgeEvent e);
}
