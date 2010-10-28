package org.cytoscape.view.model.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyNode;

/**
 * When node {@linkplain View} is added to a {@linkplain CyNetworkView}, this event will be fired.
 */
public final class AddedNodeViewEvent extends AbstractCyEvent<CyNetworkView> {

	private final View<CyNode> nodeView;

	/**
	 * Creates the event for a new node view.
	 * 
	 * @param source network view which includes the new node view.
	 * @param nodeView Newly created view object for a node.
	 * 
	 */
	public AddedNodeViewEvent(final CyNetworkView source, final View<CyNode> nodeView) {
		super(source, AddedNodeViewListener.class);
		this.nodeView = nodeView;
	}

	/**
	 * Returns new node view object.
	 * 
	 * @return new node view
	 */
	public View<CyNode> getNodeView() {
		return nodeView;
	}
}
