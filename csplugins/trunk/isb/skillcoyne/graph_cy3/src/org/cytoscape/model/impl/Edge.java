/**
 * 
 */
package org.cytoscape.model.impl;

import org.cytoscape.model.*;

/**
 * @author skillcoy
 *
 */
public class Edge implements CyEdge {

	private static int edgeIdIncrement = 0;
	
	private int id;
	
	//private Node source;
	private CyNode target;
	private boolean directed = false;
	
	Edge(CyNode t) {
		id = edgeIdIncrement;
		target = t;
		edgeIdIncrement++;
	}
	
//	Edge(Node s, Node t) {
//		id = edgeIdIncrement;
//		target = t;
//		edgeIdIncrement++;
//	}
	
	public int getIndex() {
		return id;
	}

	public CyNode getConnectedNode() {
		return target;
	}
	
	public boolean isDirected() {
		return directed;
	}
	
	public void setAttributes(Attribute edgeAtt) {
		
	}
	
	public Attribute getAttributes() {
		return null;
	}
}
