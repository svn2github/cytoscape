/**
 * 
 */
package org.cytoscape.model.impl;

import java.util.*;

import org.cytoscape.model.Attribute;
import org.cytoscape.model.AttributeHolder;
import org.cytoscape.model.Edge;
import org.cytoscape.model.Network;
import org.cytoscape.model.Node;
import org.cytoscape.model.NodeGroup;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;

/**
 * @author skillcoy
 *
 */
public class NetworkImpl implements Network {
	private static int netIdInc = 0;
	
	private int netId;
	private String name;
	
	private Network parent;
	
	private AttributeHolder netAtts;
	private List<Node> nodes;
	private List<Edge<Node, Node>> edges;
	
	private DynamicGraph dg;

	
	public NetworkImpl(String arg) {
		this.netId = netIdInc;
		netIdInc++;
		this.name = arg;
		dg = DynamicGraphFactory.instantiateDynamicGraph();
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge<Node, Node>>();
		netAtts = new NetworkAttributeHolderImpl();
	}
	
	
	public Object clone() {
		return null;
	}
	

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#contains(org.cytoscape.model.Node)
	 */
	public boolean contains(Node node) {
		return nodes.contains(node);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#contains(org.cytoscape.model.Edge)
	 */
	public boolean contains(Edge edge) {
		return edges.contains(edge);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#createGroup(java.util.Collection)
	 */
	public NodeGroup createGroup(Collection<Node> nodes) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#getAdjacentEdges(org.cytoscape.model.Node)
	 */
	public Edge[] getAdjacentEdges(Node node) {
		return node.getAdjacentEdges();
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#getAttributeHolder()
	 */
	public AttributeHolder getAttributeHolder() {
		return this.netAtts;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#getEdges()
	 */
	public Edge[] getEdges() {
		Edge[] NetEdges = new Edge[edges.size()];
		return edges.toArray(NetEdges);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#getIdentifier()
	 */
	public int getIdentifier() {
		return this.netId;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#getName()
	 */
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#getNeighbors(org.cytoscape.model.Node)
	 */
	public Node[] getNeighbors(Node node) {
		return node.getNeighbors();
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#getNodes()
	 */
	public Node[] getNodes() {
		Node[] NetNodes = new Node[nodes.size()];
		return nodes.toArray(NetNodes);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#getParentNetwork()
	 */
	public Network getParentNetwork() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#addEdge(java.lang.Object, java.lang.Object, org.cytoscape.model.Attribute)
	 */
	public Edge addEdge(Object source, Object target, Attribute edgeDirection) {

		if (!contains( (Node)source) || !contains( (Node)target)) {
			throw new RuntimeException("Input nodes are not part of network " + getIdentifier());
		}
		
		// inelegant but not the end of the world I suppose
		NodeImpl nodeSource = (NodeImpl) source;
		NodeImpl nodeTarget = (NodeImpl) target;
		
		int edgeIndex = dg.edgeCreate( nodeSource.getIndex(), nodeTarget.getIndex(), true); // XXX get edge direction from the attribute
		Edge<Node, Node> edge = new EdgeImpl<Node, Node>( (Node)nodeSource, (Node)nodeTarget, edgeIndex);
		
		if ( edgeIndex == edges.size() )
			edges.add( edge );
		else if ( edgeIndex < edges.size() && edgeIndex > 0 )
			edges.set( edgeIndex, edge );
		else
			throw new RuntimeException("bad new edge index: " + edgeIndex + " max size: " + edges.size());

		nodeSource.addEdge(edge);
		// if ( Attribute directed == false)
		nodeTarget.addEdge(edge);
		
		return edge;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#addNode()
	 */
	public Node addNode(Attribute nodeAtts) {
		
		int nodeIndex = dg.nodeCreate();
		Node node = new NodeImpl(nodeIndex); 

		if (nodeIndex == nodes.size())
			nodes.add(node);
		else if (nodeIndex < nodes.size() && nodeIndex >= 0)
			nodes.set(nodeIndex, node);
		else
			throw new RuntimeException("bad new node index: " + nodeIndex + " max size: " + nodes.size());

		//node.getAttributeHolder().addAttribute(nodeAtts);
		return node;
	}

	
	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#removeEdge(org.cytoscape.model.Edge)
	 */
	public void removeEdge(Edge edge) {
		if (contains(edge)) {
			dg.edgeRemove( ((EdgeImpl)edge).getIndex() );
			edges.remove(edge);
		}

	}

	/* (non-Javadoc)
	 * @see org.cytoscape.model.Network#removeNode(org.cytoscape.model.Node)
	 */
	public void removeNode(Node node) {
		if (contains(node)) {
			for (Edge e: node.getAdjacentEdges())
				this.removeEdge(e);
			dg.nodeRemove( ((NodeImpl) node).getIndex());
			nodes.remove(node);
		}
	}

}
