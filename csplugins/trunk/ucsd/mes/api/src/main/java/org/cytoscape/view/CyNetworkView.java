package org.cytoscape.view;

import org.cytoscape.model.network.CyNetwork;
import java.util.List; 

/**
 * Contains the visual representation of a Network.
 */
public interface CyNetworkView extends View {
	public CyNetwork getNetwork();

	public List<CyNodeView> getNodeViewList();
	public List<CyEdgeView> getEdgeViewList();

	public  CyNodeView addNodeView(CyNodeView nodeView);
	public  CyEdgeView addEdgeView(CyEdgeView edgeView);

	public CyNodeView removeNodeView (CyNodeView node_view);
	public CyEdgeView removeEdgeView (CyEdgeView edge_view);

	/**
	 * I wonder if this should be here. Shouldn't updating be
	 * a function of the renderer?  Doesn't the view just contain
	 * the state of the view?  I think a better approach is for
	 * the view to fire an event when something is modified and
	 * the renderer will listen for that event and redraw/update
	 * accordingly.
	 */
	public void updateView();
}
