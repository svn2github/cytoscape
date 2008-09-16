
package org.cytoscape.model.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for RemovedNodeEvents.
 */
public interface RemovedNodeListener extends CyEventListener {
	public void handleEvent(RemovedNodeEvent e);
}
