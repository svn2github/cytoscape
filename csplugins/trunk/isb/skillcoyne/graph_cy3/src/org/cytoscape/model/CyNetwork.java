/**
 * 
 */
package org.cytoscape.model;

import java.util.*;
/**
 * @author skillcoy
 *
 */
public interface CyNetwork {

	public int getIdentifier();
	public String getName();
	
	public CyNode addNode();
	public void removeNode(CyNode node);
	
	public CyEdge addEdge(CyNode source, CyNode target, boolean directed);
	public void removeEdge(CyEdge edge);
	
	public boolean contains(CyNode node);
	public boolean contains(CyEdge edge);
	
	public List<CyNode> getNodes();
	public List<CyEdge> getEdges();

	public List<CyNode> getNeighbors(CyNode node);
	public List<CyEdge> getAdjacentEdges(CyNode node);
	
	public void setAttributes(Attribute att);
	public Attribute getAttributes();
	
}
