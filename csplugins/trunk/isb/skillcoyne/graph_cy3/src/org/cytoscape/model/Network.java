/**
 * 
 */
package org.cytoscape.model;

import java.util.*;

/**
 * A Network *could* be typed by the edges expected, but it might make more sense
 * to allow the Edge interface to define that and allow a network to create an edge
 * to any two "objects" such as nodes, node groups, node lists, etc
 */
public interface Network {
	// basics
	public int getIdentifier();
	public String getName();
	
	// adding/removing nodes or edges 
	public Node addNode(Attribute nodeAtts);
	public void removeNode(Node node);
	
	public Edge addEdge(Node source, Node target, Attribute edgeAtts);
	public void removeEdge(Edge edge);
	
	// does the network have these objects
	public boolean contains(Node node);
	public boolean contains(Edge edge);
	public boolean containsEdges( Node nodeFrom, Node nodeTo );

	
	// get lists of whatever this graph contains
	public Node[] getNodes();
	public Edge[] getEdges();
	public Edge[] getConnectingEdges( Node source, Node target, byte edgeType );

	// Makes most sense to me on a node, but could be in both interfaces for now
	public Node[] getNeighbors(Node node);
	public Edge[] getAdjacentEdges(Node node);
	
	// Network attributes
	public AttributeHolder getAttributeHolder();

	// Do these belong on the Edge object instead?
    public static final byte NO_EDGE        	= 0;
	public static final byte UNDIRECTED_EDGE    = (1 << 0);
	public static final byte INCOMING_EDGE      = (1 << 1);
	public static final byte OUTGOING_EDGE      = (1 << 2);
	public static final byte DIRECTED_EDGE      = INCOMING_EDGE | OUTGOING_EDGE;
	public static final byte ANY_EDGE       	= UNDIRECTED_EDGE | DIRECTED_EDGE;
}
