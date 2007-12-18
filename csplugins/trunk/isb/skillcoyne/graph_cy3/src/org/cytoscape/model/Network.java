/**
 * 
 */
package org.cytoscape.model;

import java.util.*;

/**
 * A Network *could* be typed by the edges expected, but it might make more sense
 * to allow the Edge interface to define that and allow a network to create an edge
 * to any two "objects" such as nodes, node groups, node lists, etc
 *
 */
public interface Network {
	// basics
	public int getIdentifier();
	public String getName();
	
	// adding/removing nodes or edges 
	public Node addNode(Attribute nodeAtts);
	public void removeNode(Node node);
	
	public Edge addEdge(Object source, Object targets, Attribute edgeAtts);
	public void removeEdge(Edge edge);
	
	// does the network have these objects
	public boolean contains(Node node);
	public boolean contains(Edge edge);
	
	// get lists of whatever this graph contains
	public Node[] getNodes();
	public Edge[] getEdges();

	// Makes most sense to me on a node, but could be in both interfaces for now
	public Node[] getNeighbors(Node node);
	public Edge[] getAdjacentEdges(Node node);
	
	// attributes
	public AttributeHolder getAttributeHolder();
	
	// for subgraphs? these I'm not so sure about
	public Object clone();
	public Network getParentNetwork();
	public NodeGroup createGroup(Collection<Node> nodes);
}
