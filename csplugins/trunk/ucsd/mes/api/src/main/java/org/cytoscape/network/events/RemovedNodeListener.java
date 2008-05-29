
package org.cytoscape.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for RemovedNodeEvents.
 */
public interface RemovedNodeListener extends CyEventListener<RemovedNodeEvent> {
	public void handleEvent(RemovedNodeEvent e);
}
