package org.cytoscape.model;

import java.util.List;

public interface CyNetwork extends CyGraph<CyEdge> {
	public CyEdge addEdge(CyNode source, CyNode target, boolean isDirected);
}
