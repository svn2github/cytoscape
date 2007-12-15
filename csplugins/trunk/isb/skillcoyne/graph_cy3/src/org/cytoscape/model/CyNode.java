/**
 * 
 */
package org.cytoscape.model;

import java.util.*;
/**
 * @author skillcoy
 *
 */
public interface CyNode {

	public int getIndex();
	
	public CyEdge connectTo(CyNode targetNode, boolean directed, int index);
	public void removeEdge(CyEdge edge);
	
	public List<CyEdge> getAdjacentEdges();
	public List<CyNode> getNeighbors();
	
	public void setAttributes(Attribute nodeAtt);
	public Attribute getAttributes();
	
	public boolean hasBeenVisited();
	public void visited(boolean visit);
}
