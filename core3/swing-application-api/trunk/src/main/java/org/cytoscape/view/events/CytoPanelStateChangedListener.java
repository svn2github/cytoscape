
package org.cytoscape.view.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface CytoPanelStateChangedListener extends CyListener {
	public void handleEvent(CytoPanelStateChangedEvent e);
}
