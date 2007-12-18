/**
 * 
 */
package org.cytoscape.model.impl;

import org.cytoscape.model.AttributeHolder;
import org.cytoscape.model.Edge;
import org.cytoscape.model.Node;

import java.util.*;

/**
 * @author skillcoy
 *
 */
public class NodeImpl implements Node {

	private List<Edge<Node, Node>> adjEdges;
	private Node[] neighborNodes;
	private AttributeHolder attHold;
	private int graphIndex;
	
	
	protected NodeImpl(int index) {
		this.graphIndex = index;
		attHold = new NodeAttributeHolderImpl();
		adjEdges = new ArrayList<Edge<Node, Node>>();
	}
	
	protected int getIndex() {
		return this.graphIndex;
	}
	
	protected void addEdge(Edge<Node, Node> edge) {
		this.adjEdges.add(edge);
	}
	
	protected void removeEdge(Edge<Node, Node> edge) {
		this.adjEdges.remove(edge);
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.model.Node#getAdjacentEdges()
	 */
	public Edge<Node, Node>[] getAdjacentEdges() {
		Edge<Node, Node>[] edges = new Edge[adjEdges.size()];
		return adjEdges.toArray(edges);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Node#getAttributeHolder()
	 */
	public AttributeHolder getAttributeHolder() {
		return attHold;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Node#getNeighbors()
	 */
	public Node[] getNeighbors() {
		// obviously not entirely the right way

		List<Node> neighbors = new ArrayList<Node>();
		
		for (Edge e: adjEdges) {
			if (e.isDirected() && !e.getTarget().equals(this) ) {
				neighbors.add( (Node)e.getTarget() );
			} else if (e.getSource().equals(this) ) {
				neighbors.add( (Node)e.getTarget() );
			} else {
				neighbors.add( (Node)e.getSource() );
			}
		}
		
		Node[] nodes = new Node[neighbors.size()];
		return neighbors.toArray(nodes);
	}

}
