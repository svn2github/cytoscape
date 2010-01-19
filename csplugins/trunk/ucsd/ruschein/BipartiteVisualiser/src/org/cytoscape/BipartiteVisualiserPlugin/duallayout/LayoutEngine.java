package org.cytoscape.BipartiteVisualiserPlugin.duallayout;

import giny.model.Edge;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayouts;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;

public class LayoutEngine {

	static String MM_EDGE_ATTR_PREFIX = "MM-";

	private final EdgeView edgeView;

	private String visualStyleName;

	private final CyNetwork parentNetwork;
	private final CyNetwork network1;
	private final CyNetwork network2;

	private static final int LEFT   = 0;
	private static final int RIGHT  = 1;
	private static final int CENTRE = 2;

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

		visualStyleName = network1.getTitle() + " <--> " + network2.getTitle();
		final String edgeAttrName = visualStyleName;
		// Create network
		CyNetwork result = Cytoscape.createNetwork(network1
				.getNodeIndicesArray(), network1.getEdgeIndicesArray(), visualStyleName, parentNetwork, false);
		int[] nodes = network2.getNodeIndicesArray();
		int[] edges = network2.getEdgeIndicesArray();

		for (int i = 0; i < nodes.length; i++)
			result.addNode(nodes[i]);
		for (int i = 0; i < edges.length; i++)
			result.addEdge(edges[i]);

		addEdges(result, edgeAttrName);
		performLayout(result);

	}

	private void addEdges(final CyNetwork result, final String edgeAttrName) {
		@SuppressWarnings("unchecked")
		final Set<CyNode> nodesInResult = new HashSet<CyNode>(
				(List<CyNode>) result.nodesList());
		@SuppressWarnings("unchecked")
		final Set<CyEdge> edgesInResult = new HashSet<CyEdge>(
				(List<CyEdge>) result.edgesList());
		final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();

		for (CyNode node : nodesInResult) {
			// Get the list of edges connected to this node
			int[] edgeIndices = parentNetwork.getAdjacentEdgeIndicesArray(node
					.getRootGraphIndex(), true, true, true);
			// For each node, select the appropriate edges
			if (edgeIndices == null)
				continue;

			for (int i = 0; i < edgeIndices.length; i++) {
				final Edge edge = parentNetwork.getEdge(edgeIndices[i]);
				if (nodesInResult.contains(edge.getSource())
						&& nodesInResult.contains(edge.getTarget())) {
					result.addEdge(edge);
					if (!edgesInResult.contains(edge))
						edgeAttr.setAttribute(edge.getIdentifier(),
								edgeAttrName, "module-module");
				}
			}
		}
	}


	private void performLayout(final CyNetwork result) {
		final Set<CyNode> leftSet   = new HashSet<CyNode>();
		final Set<CyNode> rightSet  = new HashSet<CyNode>();
		categoriseNodes(result, leftSet, rightSet);

		final CyNetworkView networkView = Cytoscape.createNetworkView(result);
		CyLayouts.getDefaultLayout().doLayout(networkView);

		final double[] xMin = new double[3];
		final double[] xMax = new double[3];
		final double[] yMin = new double[3];
		final double[] yMax = new double[3];
		findExtents(networkView, leftSet, rightSet, xMin, xMax, yMin, yMax);
		
		final VisualStyle style = VisualStyleBuilder.getVisualStyle(visualStyleName, network1.getTitle(), network2.getTitle());
		networkView.setVisualStyle(style.getName());
		Cytoscape.getVisualMappingManager().setVisualStyle(style);
		networkView.redrawGraph(false, true);
	}

	/**
	 * Sorts the nodes in "result" into 2 different sets: leftSet and rightSet.  If the
	 * original node was in both "network1" and "network2" it will be added to neither set if it
	 * was only in "network1", it will be added to leftSet, and if it was only in "network2" it
	 * will be put into rightSet.
	 */
	private void categoriseNodes(final CyNetwork result, final Set<CyNode> leftSet,
				     final Set<CyNode> rightSet)
	{
		@SuppressWarnings("unchecked")
		final Set<CyNode> orig1 = new HashSet<CyNode>(network1.nodesList());
		@SuppressWarnings("unchecked")
		final Set<CyNode> orig2 = new HashSet<CyNode>(network2.nodesList());

		@SuppressWarnings("unchecked")
		final List<CyNode> resultNodes = (List<CyNode>)result.nodesList();
		
		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		
		for (final CyNode node : resultNodes) {
			final boolean inNetwork1 = orig1.contains(node);
			final boolean inNetwork2 = orig2.contains(node);

			if (inNetwork1 && inNetwork2)
				/* Do Nothing. */;
			else if (inNetwork1 && !inNetwork2) {
				leftSet.add(node);
				nodeAttr.setAttribute(node.getIdentifier(), visualStyleName, network1.getTitle());
			} else if (!inNetwork1 && inNetwork2) {
				rightSet.add(node);
				nodeAttr.setAttribute(node.getIdentifier(), visualStyleName, network1.getTitle());
			} else // This should never happen!
				throw new IllegalStateException("Do not know how to categorise a node!");
		}
	}
	private void findExtents(final CyNetworkView networkView, final Set<CyNode> leftSet,
				 final Set<CyNode> rightSet, final double xMin[],
				 final double xMax[], final double yMin[], final double yMax[])
	{
		xMin[LEFT] = xMin[RIGHT] = xMin[CENTRE] = Double.POSITIVE_INFINITY;
		xMax[LEFT] = xMax[RIGHT] = xMax[CENTRE] = Double.NEGATIVE_INFINITY;

		final java.util.Iterator nodeViewsIterator = networkView.getNodeViewsIterator();
		while (nodeViewsIterator.hasNext()) {
			final NodeView nodeView = (NodeView)(nodeViewsIterator.next());
			final CyNode node = (CyNode)nodeView.getNode();
			if (leftSet.contains(node)) {
				xMin[LEFT] = Math.min(xMin[LEFT], nodeView.getXPosition());
				xMax[LEFT] = Math.max(xMax[LEFT], nodeView.getXPosition());
				yMin[LEFT] = Math.min(yMin[LEFT], nodeView.getYPosition());
				yMax[LEFT] = Math.max(yMax[LEFT], nodeView.getYPosition());
			}
			else if (rightSet.contains(node)) {
				xMin[RIGHT] = Math.min(xMin[RIGHT], nodeView.getXPosition());
				xMax[RIGHT] = Math.max(xMax[RIGHT], nodeView.getXPosition());
				yMin[RIGHT] = Math.min(yMin[RIGHT], nodeView.getYPosition());
				yMax[RIGHT] = Math.max(yMax[RIGHT], nodeView.getYPosition());
			}
			else { // Assume the node is in the centre set.
				xMin[CENTRE] = Math.min(xMin[CENTRE], nodeView.getXPosition());
				xMax[CENTRE] = Math.max(xMax[CENTRE], nodeView.getXPosition());
				yMin[CENTRE] = Math.min(yMin[CENTRE], nodeView.getYPosition());
				yMax[CENTRE] = Math.max(yMax[CENTRE], nodeView.getYPosition());
			}
		}
	}
}
