package org.cytoscape.model;

import java.util.List;

/**
 * CyHyperEdge is the object that represents the connection
 * between two (or more) nodes
 */
public interface CyHyperEdge extends CyEdge {

	/**
	 * Return the source(s) of this edge
	 *
	 * @return edge sources
	 */
	public List<CyNode> getSources();

	/**
	 * Return the target(s) of this edge
	 *
	 * @return edge targets
	 */
	public List<CyNode> getTargets();

	/**
 	 * Return the connecting node for this Hyperedge
 	 *
 	 * @return connecting node
 	 */
	public CyNode getConnectingNode();
}
