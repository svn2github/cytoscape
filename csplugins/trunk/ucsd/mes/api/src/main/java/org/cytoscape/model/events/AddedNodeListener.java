
package org.cytoscape.model.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for AddedEgeEvents.
 */
public interface AddedNodeListener extends CyEventListener<AddedNodeEvent> {
	public void handleEvent(AddedNodeEvent e);
}
