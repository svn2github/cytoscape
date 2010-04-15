
package org.cytoscape.view.model.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyEdge;

/**
 * 
 */
public final class AddedEdgeViewEvent extends AbstractCyEvent<CyNetworkView> {
	private View<CyEdge> edgeView;
	public AddedEdgeViewEvent(final CyNetworkView source, final View<CyEdge> edgeView) {
		super(source, AddedEdgeViewListener.class);
		this.edgeView = edgeView;
	}

	public View<CyEdge> getEdgeView() {
		return edgeView;
	}
}
