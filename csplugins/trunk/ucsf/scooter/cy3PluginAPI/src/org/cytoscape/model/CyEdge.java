package org.cytoscape.model;

import java.util.Set;

/**
 * CyEdge is the object that represents the connection
 * between two (or more) nodes
 */
public interface CyEdge extends CyModelObject {
	public static enum EdgeDirection {EDGE_UNDIRECTED, EDGE_DIRECTED};

	/**
	 * Return the source of this edge
	 *
	 * @return edge source
	 */
	public CyNode getSource();

	/**
	 * Return the target of this edge
	 *
	 * @return edge target
	 */
	public Set<CyNode> getTargets();

	/**
	 * Return the edge type.  This is usually indicative of the
	 * type of interaction represented by the edge.
	 *
	 * @return edge type
	 */
	public String getEdgeType();

	/**
	 * Return whether this edge is directed or not
	 *
	 * @return true if this edge is directed
	 */
	public boolean isDirected();

	/**
	 * Set this edge to be directed or not
	 *
	 * @param direction EDGE_DIRECTED if this is a directed edge
	 */
	public void setDirected(EdgeDirection directed);
}
