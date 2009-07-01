
package org.cytoscape.session.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface SetSelectedNetworksListener extends CyListener {
	public void handleEvent(SetSelectedNetworksEvent e);
}
