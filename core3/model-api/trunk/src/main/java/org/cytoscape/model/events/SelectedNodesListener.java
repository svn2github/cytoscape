
package org.cytoscape.model.events;

import org.cytoscape.event.CyListener;

/**
 * Listener for SelectedNodesEvent.
 */
public interface SelectedNodesListener extends CyListener {
	/**
	 * The method that should handle the specified event.
	 * @param e The event to be handled.
	 */
	public void handleEvent(SelectedNodesEvent e);
}
