package org.cytoscape.linkout.internal;

import org.cytoscape.model.CyEdge;
import org.cytoscape.task.EdgeViewTaskContext;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class EdgeViewLinkoutTaskContext extends LinkoutTaskContext implements EdgeViewTaskContext {

	private View<CyEdge> edgeView;
	private CyNetworkView networkView;

	@Override
	public void setEdgeView(View<CyEdge> edgeView, CyNetworkView netView) {
		this.edgeView = edgeView;
		this.networkView = netView;
		setURLs(netView.getModel(),edgeView.getModel().getSource(), edgeView.getModel().getTarget());
	}

	@Override
	public View<CyEdge> getEdgeView() {
		return edgeView;
	}

	@Override
	public CyNetworkView getNetworkView() {
		return networkView;
	}

}
