
package org.cytoscape.view.model.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public interface AddedNodeViewEvent extends CyEvent<CyNetworkView> {
	View<CyNode> getNodeView();
}
