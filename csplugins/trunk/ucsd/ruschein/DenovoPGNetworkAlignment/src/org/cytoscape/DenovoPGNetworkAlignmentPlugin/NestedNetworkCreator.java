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
	private Map<TypedLinkNodeModule<String, BFEdge>, CyNode> moduleToCyNodeMap;

	/**
	 * Instantiates an overview network of complexes (modules) and one nested
	 * network for each node in the overview network.
	 */
	NestedNetworkCreator(final TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> networkOfModules) {
		moduleToCyNodeMap = new HashMap<TypedLinkNodeModule<String, BFEdge>, CyNode>();

		overviewNetwork = Cytoscape.createNetwork(findNextAvailableNetworkName("Complex Search Results: " + new java.util.Date()),
							  /* create_view = */true);

		final CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
		final CyAttributes edgeAttribs = Cytoscape.getEdgeAttributes();

		int nodeIndex = 1;
		for (final TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> edge : networkOfModules.edges()) {
			final TypedLinkNodeModule<String, BFEdge> sourceModule = edge.source().value();
			CyNode sourceNode = moduleToCyNodeMap.get(sourceModule);
			if (sourceNode == null) {
				final String nodeName = findNextAvailableNodeName("Complex" + nodeIndex);
				sourceNode = Cytoscape.getCyNode(nodeName, /* create = */ true);
				moduleToCyNodeMap.put(sourceModule, sourceNode);
				overviewNetwork.addNode(sourceNode);
				final Set<String> genes = sourceModule.getMemberValues();
				nodeAttribs.setAttribute(sourceNode.getIdentifier(), "gene count", Integer.valueOf(genes.size()));
				nodeAttribs.setAttribute(sourceNode.getIdentifier(), "score", Double.valueOf(sourceModule.score()));

				++nodeIndex;
			}

			final TypedLinkNodeModule<String, BFEdge> targetModule = edge.target().value();
			CyNode targetNode = moduleToCyNodeMap.get(targetModule);
			if (targetNode == null) {
				final String nodeName = findNextAvailableNodeName("Complex" + nodeIndex);
				targetNode = Cytoscape.getCyNode(nodeName, /* create = */ true);
				moduleToCyNodeMap.put(targetModule, targetNode);
				overviewNetwork.addNode(targetNode);
				final Set<String> genes = targetModule.getMemberValues();
				nodeAttribs.setAttribute(targetNode.getIdentifier(), "gene count", Integer.valueOf(genes.size()));
				nodeAttribs.setAttribute(targetNode.getIdentifier(), "score", Double.valueOf(targetModule.score()));

				++nodeIndex;
			}

			final CyEdge newEdge = Cytoscape.getCyEdge(sourceNode, targetNode, Semantics.INTERACTION, "complex-complex",
								   /* create = */true);

			edgeAttribs.setAttribute(newEdge.getIdentifier(), "edge score", Double.valueOf(edge.value().link()));
		}
	}

	CyNetwork getOverviewNetwork() {
		return overviewNetwork;
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
