
package org.cytoscape.model.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for AddedEgeEvents.
 */
public interface AddedNodeListener extends CyEventListener<AddedNodeEvent> {
	public void handleEvent(AddedNodeEvent e);
}
