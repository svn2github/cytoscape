package org.cytoscape.DenovoPGNetworkAlignmentPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import networks.denovoPGNetworkAlignment.BFEdge;
import networks.linkedNetworks.TypedLinkEdge;
import networks.linkedNetworks.TypedLinkNetwork;
import networks.linkedNetworks.TypedLinkNode;
import networks.linkedNetworks.TypedLinkNodeModule;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;


/**
 * Creates an overview network for the detected complexes and nested networks
 * for each complex.
 */
class NestedNetworkCreator {
	private CyNetwork overviewNetwork = null;
	private Map<TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge>, CyNode> moduleToCyNodeMap;

	/**
	 * Instantiates an overview network of complexes (modules) and one nested
	 * network for each node in the overview network.
	 */
	NestedNetworkCreator(final TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> networkOfModules) {
		moduleToCyNodeMap = new HashMap<TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge>, CyNode>();
		createOverviewNetworkNodes(networkOfModules.nodes());
		createOverviewNetworkEdges(networkOfModules.edges());
	}

	CyNetwork getOverviewNetwork() {
		return overviewNetwork;
	}

	private void createOverviewNetworkNodes(final Set<TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge>> overviewNodes) {
		overviewNetwork = Cytoscape.createNetwork(findNextAvailableNetworkName("Complex Search Results: " + new java.util.Date()),
							  /* create_view = */true);

		final CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();

		int nodeIndex = 1;
		for (final TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> module : overviewNodes) {
			final String nodeName = findNextAvailableNodeName("Complex" + nodeIndex);
			final CyNode newNode = Cytoscape.getCyNode(nodeName, /* create = */ true);
			moduleToCyNodeMap.put(module, newNode);
			overviewNetwork.addNode(newNode);
			final Set<String> genes = module.value().getMemberValues();
			nodeAttribs.setAttribute(newNode.getIdentifier(), "gene count", Integer.valueOf(genes.size()));
			nodeAttribs.setAttribute(newNode.getIdentifier(), "score", Double.valueOf(module.value().score()));

			++nodeIndex;
		}
	}

	private void createOverviewNetworkEdges(final Set<TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge>> overviewEdges) {
		final CyAttributes edgeAttribs = Cytoscape.getEdgeAttributes();

		for (final TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> edge : overviewEdges) {
			final TypedLinkNodeModule<String, BFEdge> sourceModule = edge.source().value();
			final CyNode sourceNode = moduleToCyNodeMap.get(sourceModule);
			if (sourceNode == null)
				throw new IllegalStateException("this should be impossible: can't find source node!");

			final TypedLinkNodeModule<String, BFEdge> targetModule = edge.target().value();
			final CyNode targetNode = moduleToCyNodeMap.get(targetModule);
			if (targetNode == null)
				throw new IllegalStateException("this should be impossible: can't find target node name!");

			final CyEdge newEdge = Cytoscape.getCyEdge(sourceNode, targetNode, Semantics.INTERACTION, "complex-complex",
								   /* create = */true);

			edgeAttribs.setAttribute(newEdge.getIdentifier(), "edge score", Double.valueOf(edge.value().link()));
		}
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
}
