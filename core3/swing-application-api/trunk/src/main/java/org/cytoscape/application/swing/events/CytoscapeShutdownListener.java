
package org.cytoscape.application.swing.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface CytoscapeShutdownListener extends CyListener {
	public void handleEvent(CytoscapeShutdownEvent e);
}
