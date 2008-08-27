
package org.cytoscape.model.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for RemovedEgeEvents.
 */
public interface RemovedEdgeListener extends CyEventListener {
	public void handleEvent(RemovedEdgeEvent e);
}
