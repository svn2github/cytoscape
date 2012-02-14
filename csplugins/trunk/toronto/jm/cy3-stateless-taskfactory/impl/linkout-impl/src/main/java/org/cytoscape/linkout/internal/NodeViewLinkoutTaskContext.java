package org.cytoscape.linkout.internal;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.NodeViewTaskContext;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class NodeViewLinkoutTaskContext extends LinkoutTaskContext implements NodeViewTaskContext {

	private View<CyNode> nodeView;
	private CyNetworkView networkView;

	@Override
	public void setNodeView(View<CyNode> nodeView, CyNetworkView netView) {
		this.nodeView = nodeView;
		this.networkView = netView;
		setURLs(netView.getModel(),nodeView.getModel());
	}

	@Override
	public View<CyNode> getNodeView() {
		return nodeView;
	}

	@Override
	public CyNetworkView getNetworkView() {
		return networkView;
	}
	
}
