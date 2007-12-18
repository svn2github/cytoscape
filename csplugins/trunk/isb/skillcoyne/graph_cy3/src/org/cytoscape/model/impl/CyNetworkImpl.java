/**
 * 
 */
package org.cytoscape.model.impl;

import java.util.*;

import org.cytoscape.model.AttributeHolder;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;

/**
 * @author skillcoy
 * 
 */
public class CyNetworkImpl implements CyNetwork {
	private static int netIdInc = 0;

	private List<CyNode> nodes;

	private List<CyEdge> edges;

	private String name;

	private int id;

	private DynamicGraph dg;

	public CyNetworkImpl(String arg) {
		name = arg;
		id = netIdInc;
		netIdInc++;
		nodes = new ArrayList<CyNode>();
		edges = new ArrayList<CyEdge>();
		dg = DynamicGraphFactory.instantiateDynamicGraph();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#getIdentifier()
	 */
	public int getIdentifier() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#addEdge(org.cytoscape.model.CyNode,
	 *      org.cytoscape.model.CyNode, boolean)
	 */
	// is there any way to allow the entire creation to occur on the node itself?
	public CyEdge addEdge(CyNode source, CyNode target, boolean directed) {
		if (!contains(source) || !contains(target)) {
			throw new RuntimeException("Input nodes are not part of network " + getIdentifier());
		}
		int edgeIndex = dg.edgeCreate(source.getIndex(), target.getIndex(), directed);
		CyEdge edge = source.connectTo(target, directed); // how important is the actual index to an edge...?
		edge.setIndex(edgeIndex);
		
		if ( edgeIndex == edges.size() )
			edges.add( edge );
		else if ( edgeIndex < edges.size() && edgeIndex > 0 )
			edges.set( edgeIndex, edge );
		else
			throw new RuntimeException("bad new int index: " + edgeIndex + " max size: " + edges.size());

		return edge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#addNode()
	 */
	public CyNode addNode() {
		int nodeIndex = dg.nodeCreate();
		CyNode node = new CyNodeImpl(nodeIndex);

		if (nodeIndex == nodes.size())
			nodes.add(node);
		else if (nodeIndex < nodes.size() && nodeIndex >= 0)
			nodes.set(nodeIndex, node);
		else
			throw new RuntimeException("bad new int index: " + nodeIndex
					+ " max size: " + nodes.size());

		return node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#getAdjacentEdges(org.cytoscape.model.CyNode)
	 */
	public List<CyEdge> getAdjacentEdges(CyNode node) {
		return node.getAdjacentEdges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#getEdges()
	 */
	public List<CyEdge> getEdges() {
		return edges;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#getNeighbors(org.cytoscape.model.CyNode)
	 */
	public List<CyNode> getNeighbors(CyNode node) {
		return node.getNeighbors();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#getNodes()
	 */
	public List<CyNode> getNodes() {
		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#removeEdge(org.cytoscape.model.CyEdge)
	 */
	public void removeEdge(CyEdge edge) {
		if (contains(edge)) {
			edge.getSource().removeEdge(edge);
			edges.remove(edge);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#removeNode(org.cytoscape.model.CyNode)
	 */
	public void removeNode(CyNode node) {
		if (contains(node)) {
			for (CyEdge e : node.getAdjacentEdges()) {
				dg.edgeRemove(e.getIndex());
				edges.remove(e);
			}
			dg.nodeRemove(node.getIndex());
			nodes.remove(node);
		}
	}

	public boolean contains(CyNode node) {
		return nodes.contains(node);
	}

	public boolean contains(CyEdge edge) {
		return edges.contains(edge);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#setAttributes(org.cytoscape.model.Attribute)
	 */
	public void setAttributes(AttributeHolder att) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cytoscape.model.CyNetwork#getAttributes()
	 */
	public AttributeHolder getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

}
