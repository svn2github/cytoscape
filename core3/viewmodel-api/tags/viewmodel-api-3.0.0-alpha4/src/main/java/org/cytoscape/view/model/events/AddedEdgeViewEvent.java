
package org.cytoscape.view.model.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyEdge;

/**
 * When edge {@linkplain View} is added to a {@linkplain CyNetworkView}, this event will be fired.
 */
public final class AddedEdgeViewEvent extends AbstractCyEvent<CyNetworkView> {
	
	private View<CyEdge> edgeView;
	
	/**
	 * Creates the event for a new edge view.
	 * 
	 * @param source network view which includes the new edge view.
	 * @param edgeView Newly created view object for an edge.
	 * 
	 */
	public AddedEdgeViewEvent(final CyNetworkView source, final View<CyEdge> edgeView) {
		super(source, AddedEdgeViewListener.class);
		this.edgeView = edgeView;
	}

	/**
	 * Returns new edge view added to the source network view object.
	 * 
	 * @return new edge view added to the network view.
	 * 
	 */
	public View<CyEdge> getEdgeView() {
		return edgeView;
	}
}
