
package org.cytoscape.view.model.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface AddedEdgeViewListener extends CyListener {
	public void handleEvent(AddedEdgeViewEvent e);
}
