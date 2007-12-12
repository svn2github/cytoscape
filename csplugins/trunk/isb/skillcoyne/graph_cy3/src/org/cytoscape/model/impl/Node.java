/**
 * 
 */
package org.cytoscape.model.impl;

import org.cytoscape.model.*;
import java.util.*;
/**
 * @author skillcoy
 *
 */
public class Node implements CyNode {

	private static int nodeIdIncrement = 0;
	
	private int id;
	private Vector<CyEdge> edges;
	private boolean visited;
	
	Node() {
		id = nodeIdIncrement;
		edges = new Vector<CyEdge>();
		nodeIdIncrement++;
	}
	
	public int getIndex() {
		return id;
	}

	public void visited(boolean v) {
		visited = v;
	}
	
	public boolean hasBeenVisited() {
		return visited;
	}
	
	
	protected CyEdge connectNode(CyNode target) {
		CyEdge e = new Edge(target);
		edges.add(e);
		return e;
	}
	
	public CyEdge connectTo(CyNode target, boolean directed) {
		CyEdge e = this.connectNode(target);
		if (!directed)
			((Node)target).connectNode((CyNode)this);
		return e;
	}
	
	public void removeEdge(CyEdge e) {
		e.getConnectedNode().removeEdge(e);
		edges.remove(e);
	}

	public List<CyEdge> getEdges() {
		return edges;
	}
	
	public void setAttributes(Attribute nodeAtt) {
		
	}
	
	public Attribute getAttributes() {
		return null;
	}
	
}
