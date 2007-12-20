package org.cytoscape.view;

import org.cytoscape.model.CyNetwork;
import java.util.List; 

public interface CyNetworkView extends View {
	public CyNetwork getNetwork();
	public String getIdentifier();

	public List<CyNodeView> getNodeViewList();
	public List<CyEdgeView> getEdgeViewList();

	public  CyNodeView addNodeView(CyNodeView nodeView);
	public  CyEdgeView addEdgeView(CyEdgeView edgeView);

	public CyNodeView removeNodeView (CyNodeView node_view);
	public CyEdgeView removeEdgeView (CyEdgeView edge_view);

	public VisualStyle getVisualStyle();
	public void setVisualStyle(VisualStyle vs);

	public void updateView();
}
