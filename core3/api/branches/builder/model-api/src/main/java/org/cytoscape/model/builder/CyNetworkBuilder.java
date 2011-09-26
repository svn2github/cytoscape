package org.cytoscape.model.builder;

import java.util.List;
import java.util.ArrayList;

public interface CyNetworkBuilder {

	CyNodeBuilder addNode();

	CyEdgeBuilder addEdge(CyNodeBuilder source, CyNodeBuilder target, boolean directed);

	List<CyEdgeBuilder> getEdges();

	List<CyNodeBuilder> getNodes();

	CyTableBuilder getNodeTable();

	CyTableBuilder getEdgeTable();

	CyTableBuilder getNetworkTable();
}
