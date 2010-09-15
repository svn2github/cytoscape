
package org.cytoscape.view.model.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface AddedNodeViewListener extends CyListener {
	public void handleEvent(AddedNodeViewEvent e);
}
