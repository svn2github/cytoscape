
package org.cytoscape.application.swing.view.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface CytoPanelStateChangedListener extends CyListener {
	public void handleEvent(CytoPanelStateChangedEvent e);
}
