package org.cytoscape.view.model2;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.GraphObject;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import java.util.List; 

/**
 * Contains the visual representation of a Network.
 */
public interface CyNetworkView {

	/**
	 * Returns the network this view was created for.  The
	 * network is immutable for this view, so there is no way
	 * to set it.
	 */
	public CyNetwork getNetwork();

	/**
	 * Returns a View for a specified Node.
	 */
	public View<CyNode> getCyNodeView( CyNode n );

	/**
	 * Returns a list of Views for all CyNodes in the network. 
	 */
	public List<View<CyNode>> getCyNodeViews();

	/**
	 * Returns a View for a specified Edge.
	 */
	public View<CyEdge> getCyEdgeView( CyEdge n );

	/**
	 * Returns a list of Views for all CyEdges in the network. 
	 */
	public List<View<CyEdge>> getCyEdgeViews();

	/**
	 * Returns the view for this Network. 
	 */
	public View<CyNetwork> getNetworkView();

	/**
	 * Returns a list of all View including those for Nodes, Edges, and Network.
	 */
	public List<View<? extends GraphObject>> getAllViews();
}
