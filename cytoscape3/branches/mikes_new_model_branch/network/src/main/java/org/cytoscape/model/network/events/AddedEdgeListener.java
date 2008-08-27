
package org.cytoscape.model.network.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for AddedEgeEvents.
 */
public interface AddedEdgeListener extends CyEventListener {
	public void handleEvent(AddedEdgeEvent e);
}
