
package org.cytoscape.model.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for AddedEgeEvents.
 */
public interface AddedNodeListener extends CyEventListener {
	public void handleEvent(AddedNodeEvent e);
}
