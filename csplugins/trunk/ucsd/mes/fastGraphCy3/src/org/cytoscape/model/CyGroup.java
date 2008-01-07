
package org.cytoscape.model;

import java.util.List;

public interface CyGroup {
	public void addInnerEdge(CyEdge edge);
	public void addNode(CyNode node);
	public void addOuterEdge(CyEdge edge);
	public boolean contains(CyNode node);
	public String getGroupName();
	public List<CyEdge> getInnerEdges();
	public List<CyNode> getNodes();
	public List<CyEdge> getOuterEdges();
	public void removeNode(CyNode node);
	public void removeInnerEdge(CyEdge edge);
	public void removeOuterEdge(CyEdge edge);
	public GroupState getState();
	public void setState(GroupState state);
}
