/**
 * 
 */
package org.cytoscape.model;

import java.util.*;

/**
 * @author skillcoy
 *
 */
public interface Node {

	public Edge[] getAdjacentEdges();
	public Node[] getNeighbors();
	
	// attributes
	public AttributeHolder getAttributeHolder();
}
