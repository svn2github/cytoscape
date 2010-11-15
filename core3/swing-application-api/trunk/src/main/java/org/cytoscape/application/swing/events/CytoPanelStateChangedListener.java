
package org.cytoscape.application.swing.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface CytoPanelStateChangedListener extends CyListener {
	public void handleEvent(CytoPanelStateChangedEvent e);
}
