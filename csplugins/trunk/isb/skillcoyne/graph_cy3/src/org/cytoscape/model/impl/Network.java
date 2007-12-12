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
public class Network implements CyNetwork {
	
	private static int networkIdIncrement = 0;
	
	private int id;
	private TreeMap<Integer, CyNode> nodes;
	
	public Network() {
		id = networkIdIncrement;
		nodes = new TreeMap<Integer, CyNode>();
		networkIdIncrement++;
	}

	public int getIdentifier() {
		return id;
	}
	

	public void setAttributes(Attribute att) {
		
	}
	
	public Attribute getAttributes() {
		return null;
	}
	
	public CyNode addNode() {
		CyNode n = new Node();
		nodes.put(n.getIndex(), n);
		return n;
	}
	
	public void remove(CyNode n) {
		nodes.remove(n.getIndex());
	}
	
	/* --- bulk operations... --- */
	/**
 	 * Get the nodes in the listed range (by their creation order...ids)
 	 * @param min
 	 * @param max
 	 * @return
	 */
	public List<CyNode> getNodes(int min, int max) {
		return new LinkedList<CyNode>(nodes.subMap(min, max).values()); 
	}

	/**
	 * Get all nodes
	 * @return
	 */
	public List<CyNode> getNodes() {
		return new ArrayList<CyNode>(nodes.values());
	}
	
}
