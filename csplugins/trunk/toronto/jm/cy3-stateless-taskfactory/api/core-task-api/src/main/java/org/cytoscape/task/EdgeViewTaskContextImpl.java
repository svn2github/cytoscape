package org.cytoscape.task;

import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public class EdgeViewTaskContextImpl implements EdgeViewTaskContext {
	protected View<CyEdge> edgeView;
	protected CyNetworkView networkView;

	@Override
	public void setEdgeView(View<CyEdge> edgeView, CyNetworkView netView) {
		if ( edgeView == null )
			throw new NullPointerException("EdgeView is null");
		if ( netView == null )
			throw new NullPointerException("CyNetworkView is null");
		this.edgeView = edgeView;
		this.networkView = netView;
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
