package org.cytoscape.model.network;

import org.cytoscape.groups.CyGroup;

import java.util.List;

public interface CyNode extends GraphObject {
	public void addToGroup(CyGroup group); 
	public void removeFromGroup(CyGroup group); 
	public List<CyGroup> getGroups(); 
	public boolean inGroup(CyGroup group); 
	public String toString(); 
} // interface Node
