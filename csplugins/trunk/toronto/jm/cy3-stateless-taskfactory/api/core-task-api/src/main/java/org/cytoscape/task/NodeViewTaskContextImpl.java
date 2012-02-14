package org.cytoscape.task;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class NodeViewTaskContextImpl implements NodeViewTaskContext {
	/** The network view that will be used to provision tasks that are being created by descendants of this class. */
	protected CyNetworkView netView; // TODO: should be renamed to networkView

	/** The node view that will be used to provision tasks that are being created by descendants of this class. */
	protected View<CyNode> nodeView;

	@Override
	public void setNodeView(final View<CyNode> nodeView, final CyNetworkView netView) {
		if (nodeView == null)
			throw new NullPointerException("NodeView is null");
		if (netView == null)
			throw new NullPointerException("CyNetworkView is null");

		this.nodeView = nodeView;
		this.netView = netView;
	}

	@Override
	public View<CyNode> getNodeView() {
		return nodeView;
	}

	@Override
	public CyNetworkView getNetworkView() {
		return netView;
	}
}
