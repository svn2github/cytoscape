package org.cytoscape.DenovoPGNetworkAlignmentPlugin;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import networks.denovoPGNetworkAlignment.BFEdge;
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
	private Map<Integer, String> nodeIndexToNodeNameMap;


	/**
	 * Instantiates an overview network of complexes (modules) and one nested
	 * network for each node in the overview network.
	 */
	NestedNetworkCreator(final TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> networkOfModules,
	                     final List<EdgeProperties> edgeProperties)
	{
		nodeIndexToNodeNameMap = new HashMap<Integer, String>();
		createOverviewNetworkNodes(networkOfModules.nodes());
		createOverviewNetworkEdges(edgeProperties);
	}


	CyNetwork getOverviewNetwork() {
		return overviewNetwork;
	}


	private void createOverviewNetworkNodes(final Set<TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge>> overviewNodes) {
		overviewNetwork = Cytoscape.createNetwork(findNextAvailableNetworkName("Complex Search Result: " + new java.util.Date()),
		                                          /* create_view = */ true);

		int nodeIndex = 1;
		for (final TypedLinkNode<TypedLinkNodeModule<String, BFEdge>, BFEdge> module : overviewNodes) {
			final String nodeName = findNextAvailableNodeName("Complex" + nodeIndex);
			nodeIndexToNodeNameMap.put(nodeIndex, nodeName);
			final CyNode newNode = Cytoscape.getCyNode(nodeName, /* create = */ true);
			overviewNetwork.addNode(newNode);

			++nodeIndex;
		}
	}


	private void createOverviewNetworkEdges(final List<EdgeProperties> edgeProperties) {
		final CyAttributes edgeAttribs = Cytoscape.getEdgeAttributes();

		for (final EdgeProperties edgeProps : edgeProperties) {
			final String sourceNodeName = nodeIndexToNodeNameMap.get(Integer.valueOf(edgeProps.getSourceNodeIndex()));
			if (sourceNodeName == null)
				throw new IllegalStateException("this should be impossible: can't find source node name!");

			final String targetNodeName = nodeIndexToNodeNameMap.get(Integer.valueOf(edgeProps.getTargetNodeIndex()));
			if (targetNodeName == null)
				throw new IllegalStateException("this should be impossible: can't find target node name!");

			final CyNode sourceNode = Cytoscape.getCyNode(sourceNodeName);
			final CyNode targetNode = Cytoscape.getCyNode(targetNodeName);
			final CyEdge newEdge = Cytoscape.getCyEdge(sourceNode, targetNode, Semantics.INTERACTION, "complex-complex",
								   /* create = */ true);
			

			edgeAttribs.setAttribute(newEdge.getIdentifier(), "edge score", Double.valueOf(edgeProps.getEdgeScore()));
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
