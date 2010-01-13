package org.cytoscape.DenovoPGNetworkAlignmentPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import networks.denovoPGNetworkAlignment.BFEdge;
import networks.linkedNetworks.TypedLinkEdge;
import networks.linkedNetworks.TypedLinkNetwork;
import networks.linkedNetworks.TypedLinkNodeModule;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;

/**
 * Creates an overview network for the detected complexes and nested networks
 * for each complex.
 */
public class NestedNetworkCreator {

	// Package private constants.
	static final String GENE_COUNT = "gene count";
	static final String SCORE = "score";
	static final String EDGE_SCORE = "edge score";
	static final String NODE_SIZE = "complex node size";

	private CyNetwork overviewNetwork = null;
	private Map<TypedLinkNodeModule<String, BFEdge>, CyNode> moduleToCyNodeMap;

	/**
	 * Instantiates an overview network of complexes (modules) and one nested
	 * network for each node in the overview network.
	 * 
	 * @param networkOfModules
	 *            a representation of the "overview" network
	 * @param originalNetwork
	 *            the network that the overview network was generated from
	 */
	NestedNetworkCreator(
			final TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> networkOfModules,
			final CyNetwork originalNetwork) {
		moduleToCyNodeMap = new HashMap<TypedLinkNodeModule<String, BFEdge>, CyNode>();

		overviewNetwork = Cytoscape.createNetwork(
				findNextAvailableNetworkName("Complex Search Results: "
						+ new java.util.Date()),
				/* create_view = */true);

		final CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
		final CyAttributes edgeAttribs = Cytoscape.getEdgeAttributes();

		int nodeIndex = 1;
		for (final TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> edge : networkOfModules
				.edges()) {
			final TypedLinkNodeModule<String, BFEdge> sourceModule = edge
					.source().value();
			CyNode sourceNode = moduleToCyNodeMap.get(sourceModule);
			if (sourceNode == null) {
				final String nodeName = findNextAvailableNodeName("Complex"
						+ nodeIndex);
				sourceNode = makeOverviewNode(nodeName, sourceModule,
						nodeAttribs, originalNetwork);
				++nodeIndex;
			}

			final TypedLinkNodeModule<String, BFEdge> targetModule = edge
					.target().value();
			CyNode targetNode = moduleToCyNodeMap.get(targetModule);
			if (targetNode == null) {
				final String nodeName = findNextAvailableNodeName("Complex"
						+ nodeIndex);
				targetNode = makeOverviewNode(nodeName, targetModule,
						nodeAttribs, originalNetwork);
				++nodeIndex;
			}

			final CyEdge newEdge = Cytoscape.getCyEdge(sourceNode, targetNode,
					Semantics.INTERACTION, "complex-complex",
					/* create = */true);

			edgeAttribs.setAttribute(newEdge.getIdentifier(), EDGE_SCORE,
					Double.valueOf(edge.value().link()));
			overviewNetwork.addEdge(newEdge);

		}
		applyNetworkLayout(overviewNetwork, VisualStyleBuilder.getVisualStyle()
				.getName());
	}

	CyNetwork getOverviewNetwork() {
		return overviewNetwork;
	}

	/**
	 * @returns a new node in the overview (module/complex) network.
	 */
	private CyNode makeOverviewNode(final String nodeName,
			final TypedLinkNodeModule<String, BFEdge> module,
			final CyAttributes nodeAttribs, final CyNetwork originalNetwork) {
		final CyNode newNode = Cytoscape.getCyNode(nodeName, /* create = */true);
		moduleToCyNodeMap.put(module, newNode);
		overviewNetwork.addNode(newNode);
		final Set<String> genes = module.getMemberValues();
		nodeAttribs.setAttribute(newNode.getIdentifier(), GENE_COUNT, Integer
				.valueOf(genes.size()));
		nodeAttribs.setAttribute(newNode.getIdentifier(), SCORE, Double
				.valueOf(module.score()));
		nodeAttribs.setAttribute(newNode.getIdentifier(), NODE_SIZE, Math
				.sqrt(genes.size() / Math.PI));
		newNode.setNestedNetwork(generateNestedNetwork(nodeName, genes,
				originalNetwork));

		return newNode;
	}

	private CyNetwork generateNestedNetwork(final String networkName,
			final Set<String> nodeNames, final CyNetwork originalNetwork) {
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
		final List<CyEdge> edges = (List<CyEdge>) originalNetwork
				.getConnectingEdges(nodes);
		for (final CyEdge edge : edges)
			nestedNetwork.addEdge(edge);

		applyNetworkLayout(nestedNetwork, "default");

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

	private void applyNetworkLayout(final CyNetwork network, final String style) {
		final CyNetworkView targetView = Cytoscape.getNetworkView(network
				.getIdentifier());
		targetView.setVisualStyle(style);
		Cytoscape.getVisualMappingManager().setVisualStyle(style);
		targetView.applyLayout(CyLayouts.getDefaultLayout());
		targetView.redrawGraph(false, true);
	}
}
