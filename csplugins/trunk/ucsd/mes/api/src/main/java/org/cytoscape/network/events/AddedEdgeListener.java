
package org.cytoscape.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for AddedEgeEvents.
 */
public interface AddedEdgeListener extends CyEventListener<AddedEdgeEvent> {
	public void handleEvent(AddedEdgeEvent e);
}
