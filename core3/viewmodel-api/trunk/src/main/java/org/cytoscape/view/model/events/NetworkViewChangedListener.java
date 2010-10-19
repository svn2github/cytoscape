
package org.cytoscape.view.model.events;

import org.cytoscape.event.CyListener;

/**
 * Listener for {@linkplain NetworkViewChanged} event.  Usually, presentation layer objects implements this event handler.
 * 
 */
public interface NetworkViewChangedListener extends CyListener {
	
	/**
	 * In most cases, this is for presentation layer objects.  They should update the rendered graphics based on the changes in view model.
	 * 
	 * @param e Event containing target network view model.
	 */
	public void handleEvent(NetworkViewChangedEvent e);
}
