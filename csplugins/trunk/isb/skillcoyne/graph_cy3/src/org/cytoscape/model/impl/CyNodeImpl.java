/**
 * 
 */
package org.cytoscape.model.impl;

import org.cytoscape.model.*;
import java.util.List;
import java.util.Vector;

import org.cytoscape.model.Attribute;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * @author skillcoy
 *
 */
public class CyNodeImpl implements CyNode {
	
	private int index;
	private Vector<CyEdge> edges;
	private boolean visited = false;
	private CyNetwork parent;
	private Attribute nodeAtts;
	
	CyNodeImpl(int nodeIndex) {
		index = nodeIndex;
		edges = new Vector<CyEdge>();
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyNode#connectTo(org.cytoscape.model.CyNode, boolean)
	 */
	public CyEdge connectTo(CyNode target, boolean directed) {
		CyEdge e = new CyEdgeImpl(this, target, directed);
		return e;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyNode#getAttributes()
	 */
	public Attribute getAttributes() {
		return nodeAtts;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyNode#getEdges()
	 */
	public List<CyEdge> getEdges() {
		return edges;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyNode#getIndex()
	 */
	public int getIndex() {
		return index;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyNode#hasBeenVisited()
	 */
	public boolean hasBeenVisited() {
		return visited;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyNode#removeEdge(org.cytoscape.model.CyEdge)
	 */
	public void removeEdge(CyEdge edge) {
		edges.remove(edge);
	}

	public List<CyEdge> getAdjacentEdges() {
		return edges;
	}
	
	public List<CyNode> getNeighbors() {
		List<CyNode> neighbors = new java.util.ArrayList<CyNode>();
		for (CyEdge e: edges) {
			neighbors.add(e.getTarget());
		}
		return neighbors;
	}
	
	
	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyNode#setAttributes(org.cytoscape.model.Attribute)
	 */
	public void setAttributes(Attribute nodeAtt) {
		nodeAtts = nodeAtt;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.CyNode#visited(boolean)
	 */
	public void visited(boolean visit) {
		visited = visit;
	}

}
