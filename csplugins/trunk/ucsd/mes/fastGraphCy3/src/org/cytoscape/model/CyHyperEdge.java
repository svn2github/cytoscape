
package org.cytoscape.model;

import java.util.List;

public interface CyHyperEdge extends CyBaseEdge {
	public List<CyNode> getSourceList();
	public List<CyNode> getTargetList();
	public boolean removeNode(CyNode n);
}
