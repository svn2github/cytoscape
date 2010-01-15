package org.cytoscape.BipartiteVisualiserPlugin.duallayout;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayouts;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;

public class LayoutEngine {

	private final EdgeView edgeView;

	private final CyNetwork parentNetwork;
	private final CyNetwork network1;
	private final CyNetwork network2;

	public LayoutEngine(final EdgeView edgeView, final CyNetwork parentNetwork,
			final CyNetwork network1, final CyNetwork network2) {

		this.edgeView = edgeView;
		this.parentNetwork = parentNetwork;
		this.network1 = network1;
		this.network2 = network2;

	}

	public void doLayout(TaskMonitor taskMonitor) {
		if (taskMonitor != null)
			taskMonitor.setPercentCompleted(0);

		// Create network
		CyNetwork result = Cytoscape.createNetwork(network1
				.getNodeIndicesArray(), network1.getEdgeIndicesArray(),
				network1.getTitle() + " <--> " + network2.getTitle(), parentNetwork, false);
		int[] nodes = network2.getNodeIndicesArray();
		int[] edges = network2.getEdgeIndicesArray();

		for (int i = 0; i < nodes.length; i++)
			result.addNode(nodes[i]);
		for (int i = 0; i < edges.length; i++)
			result.addEdge(edges[i]);
		
		addEdges(result);
		performLayout(result);

	}

	
	private void addEdges(final CyNetwork result) {
		@SuppressWarnings("unchecked")
		final Set<CyNode> nodesInResult = new HashSet<CyNode>((List<CyNode>)result.nodesList());
		
		for (CyNode node: nodesInResult) {
			// Get the list of edges connected to this node
			int[] edgeIndices = parentNetwork.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), true, true, true);
			// For each node, select the appropriate edges
			if (edgeIndices == null)
				continue;

			for (int i = 0; i < edgeIndices.length; i++)  {
				final Edge edge = parentNetwork.getEdge(edgeIndices[i]);
				if (nodesInResult.contains(edge.getSource()) && nodesInResult.contains(edge.getTarget()))
					result.addEdge(edge);
				
			}
		}
	}

	private void performLayout(CyNetwork result) {
		final CyNetworkView view = Cytoscape.createNetworkView(result);
		CyLayouts.getDefaultLayout().doLayout(view);

	}

}
