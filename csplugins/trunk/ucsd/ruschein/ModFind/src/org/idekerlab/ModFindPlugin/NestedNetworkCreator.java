package org.idekerlab.ModFindPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.idekerlab.ModFindPlugin.ModFinder.BFEdge;
import org.idekerlab.ModFindPlugin.networks.linkedNetworks.TypedLinkEdge;
import org.idekerlab.ModFindPlugin.networks.linkedNetworks.TypedLinkNetwork;
import org.idekerlab.ModFindPlugin.networks.linkedNetworks.TypedLinkNodeModule;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.layout.CyLayouts;
import cytoscape.task.TaskMonitor;
import cytoscape.util.PropUtil;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;


/**
 * The sole purpose of this class is to sort networks according to decreasing score.
 */
class NetworkAndScore implements Comparable<NetworkAndScore> {
	private final String nodeName;
	private final Set<String> genes;
	private final double score;
	private final int index;
	private static int nextIndex;

	NetworkAndScore(final String nodeName, final Set<String> genes, final double score) {
		this.nodeName = nodeName;
		this.genes    = genes;
		this.score    = score;
		this.index    = nextIndex++;
	}

	String getNodeName() { return nodeName; }
	Set<String> getGenes() { return genes; }
	double getScore() { return score; }

	public boolean equals(final Object o) {
		if (!(o instanceof NetworkAndScore))
			return false;

		final NetworkAndScore other = (NetworkAndScore)o;
		return other.score == score && other.index == index;
	}

	public int compareTo(final NetworkAndScore other) {
		if (other == null)
			throw new NullPointerException("can't compare this against null!");

		if (other.score < score)
			return -1;
		else if (other.score > score)
			return +1;
		return other.index - index;
	}
}


/**
 * @author ruschein
 * Creates an overview network for the detected complexes and nested networks
 * for each complex.
 */
@SuppressWarnings("unchecked") public class NestedNetworkCreator {
	// Package private constants.
	static final String REFERENCE_NETWORK_NAME_ATTRIB =
		"BipartiteVisualiserReferenceNetworkName"; // Also exists in BipartiteVisualiserPlugin!
	static final String GENE_COUNT = "gene count";
	static final String SCORE = "score";
	static final String EDGE_SCORE = "edge score";
	static final String NODE_SIZE = "complex node size";

	private CyNetwork overviewNetwork = null;
	private Map<TypedLinkNodeModule<String, BFEdge>, CyNode> moduleToCyNodeMap;
	private int maxSize = 0;
	private final int MAX_NETWORK_VIEWS = PropUtil.getInt(CytoscapeInit.getProperties(), "moduleNetworkViewCreationThreshold", 0);
	private final PriorityQueue<NetworkAndScore> networksOrderedByScores = new PriorityQueue(100);

	/**
	 * Instantiates an overview network of complexes (modules) and one nested
	 * network for each node in the overview network.
	 * 
	 * @param networkOfModules
	 *            a representation of the "overview" network
	 * @param originalNetwork
	 *            the network that the overview network was generated from
	 * @param taskMonitor progress indicator floating dialog
	 * @param remainingPercentage 100 - this is where to start with the percent-completed progress bar
	 */
	NestedNetworkCreator(
		final TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> networkOfModules,
		final CyNetwork originalNetwork,
		final TypedLinkNetwork<String, Float> physicalNetwork,
		final TypedLinkNetwork<String, Float> geneticNetwork, final double cutoff,
		final TaskMonitor taskMonitor, final float remainingPercentage)
	{
		moduleToCyNodeMap = new HashMap<TypedLinkNodeModule<String, BFEdge>, CyNode>();

		final Set<CyEdge> selectedEdges = new HashSet<CyEdge>();
		final Set<CyNode> selectedNodes = new HashSet<CyNode>();

		overviewNetwork = Cytoscape.createNetwork(
				findNextAvailableNetworkName("Complex Search Results: "
						+ new java.util.Date()),
				/* create_view = */ false);

		final CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
		final CyAttributes edgeAttribs = Cytoscape.getEdgeAttributes();

		taskMonitor.setStatus("4. Generating networks");
		int nodeIndex = 1;
		double maxScore = Double.NEGATIVE_INFINITY;
		maxSize = 0;
		for (final TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> edge :
			     networkOfModules.edges())
		{
			final TypedLinkNodeModule<String, BFEdge> sourceModule =
				edge.source().value();
			CyNode sourceNode = moduleToCyNodeMap.get(sourceModule);
			if (sourceNode == null) {
				final String nodeName = findNextAvailableNodeName("Complex"
				                                                  + nodeIndex);

				sourceNode = makeOverviewNode(
					nodeName, sourceModule, nodeAttribs, originalNetwork);
				++nodeIndex;
			}

			final TypedLinkNodeModule<String, BFEdge> targetModule =
				edge.target().value();
			CyNode targetNode = moduleToCyNodeMap.get(targetModule);
			if (targetNode == null) {
				final String nodeName =
					findNextAvailableNodeName("Complex" + nodeIndex);
				targetNode = makeOverviewNode(
					nodeName, targetModule, nodeAttribs, originalNetwork);
				++nodeIndex;
			}

			final CyEdge newEdge = Cytoscape.getCyEdge(sourceNode, targetNode,
					Semantics.INTERACTION, "complex-complex",
					/* create = */true);
			edgeAttribs.setAttribute(newEdge.getIdentifier(),
						 REFERENCE_NETWORK_NAME_ATTRIB,
						 originalNetwork.getTitle());
			overviewNetwork.addEdge(newEdge);

			// Add various edge attributes.
			final double edgeScore = edge.value().link();
			edgeAttribs.setAttribute(newEdge.getIdentifier(), "edge score",
			                         Double.valueOf(edgeScore));
			if (edgeScore > maxScore)
				maxScore = edgeScore;
			// Selected if above cutoff
			if (edgeScore >= cutoff) {
				selectedEdges.add(newEdge);
				selectedNodes.add((CyNode) newEdge.getSource());
				selectedNodes.add((CyNode) newEdge.getTarget());
			}
			
			final int gConnectedness =
				geneticNetwork.getConnectedness(sourceModule.asStringSet(),
				                                targetModule.asStringSet());
			edgeAttribs.setAttribute(newEdge.getIdentifier(), "genetic link count",
			                         Integer.valueOf(gConnectedness));
			final int pConnectedness =
				physicalNetwork.getConnectedness(sourceModule.asStringSet(),
				                                 targetModule.asStringSet());
			edgeAttribs.setAttribute(newEdge.getIdentifier(), "physical link count",
			                         Integer.valueOf(pConnectedness));
			edgeAttribs.setAttribute(newEdge.getIdentifier(), "source size",
                                                 Integer.valueOf(sourceModule.size()));
			edgeAttribs.setAttribute(newEdge.getIdentifier(), "target size",
                                                 Integer.valueOf(targetModule.size()));
			final double density = edgeScore / (sourceModule.size() * targetModule.size());
			edgeAttribs.setAttribute(newEdge.getIdentifier(), "density",
						 Double.valueOf(density));
		}

		edgeAttribs.setUserVisible(REFERENCE_NETWORK_NAME_ATTRIB, false);
		
		Cytoscape.createNetworkView(overviewNetwork);
		applyNetworkLayout(overviewNetwork, VisualStyleBuilder.getVisualStyle(), cutoff, maxScore);

		// Visually mark selected edges and nodes:
		overviewNetwork.setSelectedEdgeState(selectedEdges, true);
		overviewNetwork.setSelectedNodeState(selectedNodes, true);

		taskMonitor.setStatus("5. Generating network views");
		int networkViewCount = 0;
		NetworkAndScore network;
		final float percentIncrement =
			remainingPercentage / networksOrderedByScores.size();
		float percentCompleted = 100.0f - remainingPercentage;
		while ((network = networksOrderedByScores.poll()) != null) {
			final boolean createView = networkViewCount++ < MAX_NETWORK_VIEWS;
			final CyNetwork nestedNetwork =
				generateNestedNetwork(network.getNodeName(), network.getGenes(),
				                      originalNetwork, createView);
			final CyNode node =
				Cytoscape.getCyNode(network.getNodeName(), /* create = */false);
			node.setNestedNetwork(nestedNetwork);

			percentCompleted += percentIncrement;
			taskMonitor.setPercentCompleted(Math.round(percentCompleted));
		}
	}

	CyNetwork getOverviewNetwork() {
		return overviewNetwork;
	}

	/**
	 * @returns a new node in the overview (module/complex) network.
	 */
	private CyNode makeOverviewNode(
			final String nodeName, final TypedLinkNodeModule<String, BFEdge> module,
			final CyAttributes nodeAttribs, final CyNetwork originalNetwork)
	{
		final CyNode newNode = Cytoscape.getCyNode(nodeName, /* create = */true);
		moduleToCyNodeMap.put(module, newNode);
		overviewNetwork.addNode(newNode);
		final Set<String> genes = module.getMemberValues();
		nodeAttribs.setAttribute(newNode.getIdentifier(), GENE_COUNT,
		                         Integer.valueOf(genes.size()));
		if (genes.size() > maxSize)
			maxSize = genes.size();
		
		final double score = Double.valueOf(module.score());
		nodeAttribs.setAttribute(newNode.getIdentifier(), SCORE, score);
		nodeAttribs.setAttribute(newNode.getIdentifier(), NODE_SIZE,
		                         Math.sqrt(genes.size() / Math.PI));
		networksOrderedByScores.add(new NetworkAndScore(nodeName, genes, score));

		return newNode;
	}

	private CyNetwork generateNestedNetwork(
		final String networkName, final Set<String> nodeNames,
		final CyNetwork originalNetwork, final boolean createNetworkView)
	{
		if (nodeNames.isEmpty())
			return null;

		final CyNetwork nestedNetwork = Cytoscape.createNetwork(networkName,
		/* create_view = */false);

		// Add the nodes to our new nested network.
		final List<CyNode> nodes = new ArrayList<CyNode>();
		for (final String nodeName : nodeNames) {
			final CyNode node = Cytoscape.getCyNode(nodeName, /* create = */true);
			nestedNetwork.addNode(node);
			nodes.add(node);
		}

		// Add the edges induced by "originalNetwork" to our new nested network.
		final List<CyEdge> edges =
			(List<CyEdge>) originalNetwork.getConnectingEdges(nodes);
		for (final CyEdge edge : edges)
			nestedNetwork.addEdge(edge);

		if (createNetworkView) {
			Cytoscape.createNetworkView(nestedNetwork);
			applyNetworkLayout(nestedNetwork,
			                   Cytoscape.getVisualMappingManager().getVisualStyle(),
			                   null, null);
		}

		return nestedNetwork;
	}

	/**
	 * Finds an unused network name starting with a first choice. If the first
	 * choice is not available, we will successively try to append -1 -2, -3 and
	 * so on, until we indentify an unused name.
	 * 
	 * @param initialPreference
	 *            The network name we'd like to use, if it is available. If not
	 *            we use it as a prefix instead.
	 */
	private String findNextAvailableNetworkName(final String initialPreference) {
		// Try the preferred choice first:
		CyNetwork network = getNetworkByTitle(initialPreference);
		if (network == null)
			return initialPreference;

		for (int suffix = 1; true; ++suffix) {
			final String titleCandidate = initialPreference + "-" + suffix;
			network = getNetworkByTitle(titleCandidate);
			if (network == null)
				return titleCandidate;
		}
	}

	/**
	 * Finds an unused node name starting with a first choice. If the first
	 * choice is not available, we will successively try to append -1 -2, -3 and
	 * so on, until we indentify an unused name.
	 * 
	 * @param initialPreference
	 *            The node name we'd like to use, if it is available. If not we
	 *            use it as a prefix instead.
	 */
	private String findNextAvailableNodeName(final String initialPreference) {
		// Try the preferred choice first:
		CyNode node = Cytoscape
				.getCyNode(initialPreference, /* create = */false);
		if (node == null)
			return initialPreference;

		for (int suffix = 1; true; ++suffix) {
			final String titleCandidate = initialPreference + "-" + suffix;
			node = Cytoscape.getCyNode(titleCandidate, /* create = */false);
			if (node == null)
				return titleCandidate;
		}
	}

	/**
	 * Returns the first network with title "networkTitle" or null, if there is
	 * no network w/ this title.
	 */
	private CyNetwork getNetworkByTitle(final String networkTitle) {
		for (final CyNetwork network : Cytoscape.getNetworkSet()) {
			if (network.getTitle().equals(networkTitle))
				return network;
		}

		return null;
	}

	private void applyNetworkLayout(final CyNetwork network, final VisualStyle style, Double cutoff, Double maxScore) {
		final CyNetworkView targetView = Cytoscape.getNetworkView(network
				.getIdentifier());
		if (cutoff != null)
			VisualStyleBuilder.updateStyle(cutoff, maxScore, maxSize);
		targetView.setVisualStyle(style.getName());
		Cytoscape.getVisualMappingManager().setVisualStyle(style);
		targetView.applyLayout(CyLayouts.getLayout("circular"));
		targetView.redrawGraph(false, true);
	}
}
