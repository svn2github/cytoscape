
package org.cytoscape.model.events;

import org.cytoscape.event.CyListener;

/**
 * Listener for SelectedEdgesEvents.
 */
public interface SelectedEdgesListener extends CyListener {
	/**
	 * The method that should handle the specified event.
	 * @param e The event to be handled.
	 */
	public void handleEvent(SelectedEdgesEvent e);
}
