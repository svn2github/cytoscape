
package org.cytoscape.model;

import java.util.List;

public interface CyHyperGraph extends CyGraph<CyHyperEdge> {
	public CyHyperEdge addEdge(List<CyNode> sources, List<CyNode> targets, boolean isDirected);
}
