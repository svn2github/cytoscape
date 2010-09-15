
package org.cytoscape.view.model.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public final class AddedNodeViewEvent extends AbstractCyEvent<CyNetworkView> {
	private final View<CyNode> nodeView;
	public AddedNodeViewEvent(final CyNetworkView source, final View<CyNode> nodeView) {
		super(source, AddedNodeViewListener.class);
		this.nodeView = nodeView;
	}

	public View<CyNode> getNodeView() {
		return nodeView;
	}
}
