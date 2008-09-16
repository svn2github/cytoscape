
package org.cytoscape.model.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for AddedEgeEvents.
 */
public interface AddedNodeListener extends CyEventListener {
	public void handleEvent(AddedNodeEvent e);
}
