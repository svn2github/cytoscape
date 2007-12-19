/**
 * 
 */
package org.cytoscape.model;

import java.util.*;

public interface Node {

	public Node[] getNeighbors();
	public Edge[] getAdjacentEdges();
	public Edge[] getConnectingEdges(Node connectedNode);
	
	// attributes
	public AttributeHolder getAttributeHolder();
}
