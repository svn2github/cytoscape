
package org.cytoscape.model.events;

import org.cytoscape.event.CyEventListener;

/**
 * Listener for AboutToRemoveNodeEvents. 
 */
public interface AboutToRemoveNodeListener extends CyEventListener {
	public void handleEvent(AboutToRemoveNodeEvent e);
}
