package org.cytoscape.model.internal.builder;

import java.util.List;
import java.util.ArrayList;
import org.cytoscape.model.builder.CyEdgeBuilder;
import org.cytoscape.model.builder.CyNodeBuilder;
import org.cytoscape.model.builder.CyNetworkBuilder;
import org.cytoscape.model.builder.CyTableBuilder;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyEdge;

public final class CyNetworkBuilderImpl implements CyNetworkBuilder {

	private final List<CyNodeBuilder> nodeList;
	private final List<CyEdgeBuilder> edgeList;
	private final CyTableBuilder networkTable;
	private final CyTableBuilder nodeTable;
	private final CyTableBuilder edgeTable;

	public CyNetworkBuilderImpl() {
		nodeList = new ArrayList<CyNodeBuilder>();
		edgeList = new ArrayList<CyEdgeBuilder>();

		networkTable = new CyTableBuilderImpl();
		networkTable.createColumn(CyTableEntry.NAME, String.class, true);

		nodeTable = new CyTableBuilderImpl();
        nodeTable.createColumn(CyTableEntry.NAME, String.class, true);
		nodeTable.createColumn(CyNetwork.SELECTED, Boolean.class, true);

		edgeTable = new CyTableBuilderImpl();
        edgeTable.createColumn(CyTableEntry.NAME, String.class, true);
		edgeTable.createColumn(CyNetwork.SELECTED, Boolean.class, true);
		edgeTable.createColumn(CyEdge.INTERACTION, String.class, true);
	}

	public CyNodeBuilder addNode() {
		CyNodeBuilder node = new CyNodeBuilderImpl(nodeTable);
		nodeList.add(node);
		return node;
	}

	public CyEdgeBuilder addEdge(CyNodeBuilder source, CyNodeBuilder target, boolean directed) {
		CyEdgeBuilder edge = new CyEdgeBuilderImpl(source, target, directed, edgeTable);
		edgeList.add(edge);
		return edge;
	}

	public List<CyEdgeBuilder> getEdges() {
		return edgeList;
	}

	public List<CyNodeBuilder> getNodes() {
		return nodeList;
	}

	public CyTableBuilder getNodeTable() {
		return nodeTable;
	}

	public CyTableBuilder getEdgeTable() {
		return edgeTable;
	}

	public CyTableBuilder getNetworkTable() {
		return networkTable;
	}
}
