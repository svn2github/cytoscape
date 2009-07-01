
package org.cytoscape.session.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface SetCurrentNetworkListener extends CyListener {
	public void handleEvent(SetCurrentNetworkEvent e);
}
