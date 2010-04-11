
package org.cytoscape.view.model.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyEdge;

/**
 * 
 */
public interface AddedEdgeViewEvent extends CyEvent<CyNetworkView> {
	View<CyEdge> getEdgeView();
}
