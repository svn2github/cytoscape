package org.idekerlab.PanGIAPlugin;


import java.util.*;

import org.idekerlab.PanGIAPlugin.ModFinder.BFEdge;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkEdge;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNetwork;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNodeModule;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.task.TaskMonitor;
import cytoscape.util.PropUtil;
import cytoscape.view.CyNetworkView;


/**
 * The sole purpose of this class is to sort networks according to decreasing
 * score.
 */
class NetworkAndScore implements Comparable<NetworkAndScore> {
	private final String nodeName;
	private final Set<String> genes;
	private final double score;
	private final int index;
	private static int nextIndex;

	NetworkAndScore(final String nodeName, final Set<String> genes,
			final double score)
	{
		this.nodeName = nodeName;
		this.genes = genes;
		this.score = score;
		this.index = nextIndex++;
	}

	String getNodeName() {
		return nodeName;
	}

	Set<String> getGenes() {
		return genes;
	}

	double getScore() {
		return score;
	}

	public boolean equals(final Object o) {
		if (!(o instanceof NetworkAndScore))
			return false;

		final NetworkAndScore other = (NetworkAndScore) o;
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
 *
 * Creates an overview network for the detected complexes and
 * nested networks for each complex.
 */
@SuppressWarnings("unchecked")
public class NestedNetworkCreator {
	
	private static final String LAYOUT_ALGORITHM = "force-directed";
	
	// Also exists in BipartiteVisualiserPlugin!
	static final String REFERENCE_NETWORK_NAME_ATTRIB = "BipartiteVisualiserReferenceNetworkName"; 

	
	/////////////// Node Attribute Names /////////////
	// This is common prefix for all finders.
	private static final String MODULE_FINDER_PREFIX = "Module Finder.";
	
	// Number of nodes in a module
	private static final String GENE_COUNT = MODULE_FINDER_PREFIX + "member count";
	// And its SQRT value for visual mapping
	private static final String GENE_COUNT_SQRT = MODULE_FINDER_PREFIX + "SQRT of member count";
	
	private static final String SCORE = MODULE_FINDER_PREFIX + "score";
	
	/////////////// Edge Attribute Names /////////////
	private static final String EDGE_SCORE = MODULE_FINDER_PREFIX + "edge score";
	private static final String EDGE_PVALUE = MODULE_FINDER_PREFIX + "p-value";
	
	private static final String COMPLEX_INTERACTION_TYPE = "module-module";
	
	private CyNetwork overviewNetwork = null;
	private Map<TypedLinkNodeModule<String, BFEdge>, CyNode> moduleToCyNodeMap;
	private int maxSize = 0;
	private final int MAX_NETWORK_VIEWS = PropUtil.getInt(CytoscapeInit
			.getProperties(), "moduleNetworkViewCreationThreshold", 0);
	private final PriorityQueue<NetworkAndScore> networksOrderedByScores = new PriorityQueue(
			100);

	/**
	 * Instantiates an overview network of complexes (modules) and one nested
	 * network for each node in the overview network.
	 * 
	 * @param networkOfModules
	 *            a representation of the "overview" network
	 * @param originalNetwork
	 *            the network that the overview network was generated from
	 * @param taskMonitor
	 *            progress indicator floating dialog
	 * @param remainingPercentage
	 *            100 - this is where to start with the percent-completed
	 *            progress bar
	 */
	NestedNetworkCreator(
			final TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> networkOfModules,
			final CyNetwork origPhysNetwork, final CyNetwork origGenNetwork,
			final TypedLinkNetwork<String, Float> physicalNetwork,
			final TypedLinkNetwork<String, Float> geneticNetwork,
			final double cutoff, final TaskMonitor taskMonitor,
			final float remainingPercentage,
			Map<TypedLinkNodeModule<String, BFEdge>,String> module_name)
	{
		// Network attributes created here is required for managing Visual Styles.
		final CyAttributes networkAttr = Cytoscape.getNetworkAttributes();
		
		moduleToCyNodeMap = new HashMap<TypedLinkNodeModule<String, BFEdge>, CyNode>();

		final Set<CyEdge> selectedEdges = new HashSet<CyEdge>();
		final Set<CyNode> selectedNodes = new HashSet<CyNode>();

		overviewNetwork = Cytoscape.createNetwork(
				findNextAvailableNetworkName("Module Search Results: "
						+ new java.util.Date()),
				/* create_view = */false);
		networkAttr.setAttribute(overviewNetwork.getIdentifier(), 
				VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.OVERVIEW.name());
		networkAttr.setUserVisible(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		networkAttr.setUserEditable(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		
		final CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
		final CyAttributes edgeAttribs = Cytoscape.getEdgeAttributes();

		taskMonitor.setStatus("5. Generating Cytoscape networks");
		int nodeIndex = 1;
		double maxScore = Double.NEGATIVE_INFINITY;
		maxSize = 0;
		for (final TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> edge : networkOfModules
				.edges())
		{
			final TypedLinkNodeModule<String, BFEdge> sourceModule = edge.source().value();
			CyNode sourceNode = moduleToCyNodeMap.get(sourceModule);
			if (sourceNode == null) {
				final String nodeName = findNextAvailableNodeName("Module" + nodeIndex);
				sourceNode = makeOverviewNode(nodeName, sourceModule,nodeAttribs);
				++nodeIndex;
			}

			final TypedLinkNodeModule<String, BFEdge> targetModule = edge.target().value();
			CyNode targetNode = moduleToCyNodeMap.get(targetModule);
			if (targetNode == null) {
				final String nodeName = findNextAvailableNodeName("Module"	+ nodeIndex);
				targetNode = makeOverviewNode(nodeName, targetModule,nodeAttribs);
				++nodeIndex;
			}
			
			
			//Auto label small complexes with the gene names
			if (sourceModule.size()<=2) 
			{
				Iterator<String> genes = sourceModule.getMemberValues().iterator();
				String newName = genes.next();
				while (genes.hasNext()) newName+=", "+genes.next();
				sourceNode.setIdentifier(newName);
			}

			if (targetModule.size()<=2) 
			{
				Iterator<String> genes = targetModule.getMemberValues().iterator();
				String newName = genes.next();
				while (genes.hasNext()) newName+=", "+genes.next();
				targetNode.setIdentifier(newName);
			}
			
			//Annotate large complexes
			if (module_name!=null)
			{
				String name1 = module_name.get(sourceModule);
				if (name1!=null) sourceNode.setIdentifier(name1);
								
				String name2 = module_name.get(sourceModule);
				if (name2!=null) sourceNode.setIdentifier(name1);
			}
			

			final CyEdge newEdge = Cytoscape.getCyEdge(sourceNode, targetNode,
					Semantics.INTERACTION, COMPLEX_INTERACTION_TYPE,
					/* create = */true);
			edgeAttribs.setAttribute(newEdge.getIdentifier(),
					REFERENCE_NETWORK_NAME_ATTRIB, origPhysNetwork.getTitle()
							+ "/" + origGenNetwork.getTitle());
			overviewNetwork.addEdge(newEdge);

			// Add various edge attributes.
			final double edgeScore = edge.value().link();
			edgeAttribs.setAttribute(newEdge.getIdentifier(), EDGE_SCORE,
					Double.valueOf(edgeScore));
			if (edgeScore > maxScore)
				maxScore = edgeScore;

			final double pValue = edge.value().linkMerge();
			edgeAttribs.setAttribute(newEdge.getIdentifier(), EDGE_PVALUE, Double
					.valueOf(pValue));
			if (pValue < cutoff) {
				selectedEdges.add(newEdge);
				selectedNodes.add((CyNode) newEdge.getSource());
				selectedNodes.add((CyNode) newEdge.getTarget());
			}

			final int gConnectedness = geneticNetwork.getConnectedness(
					sourceModule.asStringSet(), targetModule.asStringSet());
			edgeAttribs.setAttribute(newEdge.getIdentifier(),
					"genetic link count", Integer.valueOf(gConnectedness));
			final int pConnectedness = physicalNetwork.getConnectedness(
					sourceModule.asStringSet(), targetModule.asStringSet());
			edgeAttribs.setAttribute(newEdge.getIdentifier(),
					"physical link count", Integer.valueOf(pConnectedness));
			edgeAttribs.setAttribute(newEdge.getIdentifier(), "source size",
					Integer.valueOf(sourceModule.size()));
			edgeAttribs.setAttribute(newEdge.getIdentifier(), "target size",
					Integer.valueOf(targetModule.size()));
			final double density = edgeScore
					/ (sourceModule.size() * targetModule.size());
			edgeAttribs.setAttribute(newEdge.getIdentifier(), "density", Double
					.valueOf(density));
		}

		edgeAttribs.setUserVisible(REFERENCE_NETWORK_NAME_ATTRIB, false);

		Cytoscape.createNetworkView(overviewNetwork);
		applyNetworkLayout(overviewNetwork, cutoff, maxScore);

		// Visually mark selected edges and nodes:
		overviewNetwork.setSelectedEdgeState(selectedEdges, true);
		overviewNetwork.setSelectedNodeState(selectedNodes, true);

		taskMonitor.setStatus("5. Generating network views");
		int networkViewCount = 0;
		NetworkAndScore network;
		final float percentIncrement = remainingPercentage
				/ networksOrderedByScores.size();
		float percentCompleted = 100.0f - remainingPercentage;
		while ((network = networksOrderedByScores.poll()) != null) {
			final boolean createView = networkViewCount++ < MAX_NETWORK_VIEWS;
			final CyNetwork nestedNetwork = generateNestedNetwork(
					network.getNodeName(), network.getGenes(), origPhysNetwork,
					origGenNetwork, createView, networkAttr);
			final CyNode node = Cytoscape.getCyNode(network.getNodeName(), false);
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
	private CyNode makeOverviewNode(final String nodeName,
			final TypedLinkNodeModule<String, BFEdge> module,
			final CyAttributes nodeAttribs) {
		
		
		final CyNode newNode = Cytoscape.getCyNode(nodeName, true); // create=true
		moduleToCyNodeMap.put(module, newNode);
		
		
		/* How to make newNode a subnode of overviewNetwork??
		CyNode newNode = Cytoscape.getCyNode(nodeName);
		if (newNode==null)
		{
			Cytoscape.createNetwork(nodeName, overviewNetwork, false);
			newNode = Cytoscape.getCyNode(nodeName,true);
		}*/
		
		overviewNetwork.addNode(newNode);
		
		final Set<String> genes = module.getMemberValues();
		final Integer geneCount = Integer.valueOf(genes.size());
		nodeAttribs.setAttribute(newNode.getIdentifier(), GENE_COUNT, geneCount);
		nodeAttribs.setAttribute(newNode.getIdentifier(), GENE_COUNT_SQRT, Math.sqrt(geneCount));
		if (genes.size() > maxSize)
			maxSize = genes.size();

		final double score = Double.valueOf(module.score());
		nodeAttribs.setAttribute(newNode.getIdentifier(), SCORE, score);

		networksOrderedByScores
				.add(new NetworkAndScore(nodeName, genes, score));

		return newNode;
	}

	private CyNetwork generateNestedNetwork(final String networkName,
			final Set<String> nodeNames, final CyNetwork origPhysNetwork,
			final CyNetwork origGenNetwork, final boolean createNetworkView,
			final CyAttributes networkAttr)
	{
		if (nodeNames.isEmpty())
			return null;

		// First, create network without view.
		final CyNetwork nestedNetwork = Cytoscape.createNetwork(networkName, false);
		
		networkAttr.setAttribute(nestedNetwork.getIdentifier(), 
				VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.MODULE.name());

		// Add the nodes to our new nested network.
		final List<CyNode> nodes = new ArrayList<CyNode>();
		for (final String nodeName : nodeNames) {
			final CyNode node = Cytoscape.getCyNode(nodeName, /* create = */false);
			if (node == null) {
				System.err.println("in NestedNetworkCreator.generateNestedNetwork() (in the PanGIA plug-in): unknown node: \"" + nodeName + "\"!");
				throw new IllegalStateException("unknown node: \"" + nodeName + "\"!");
			}
			nestedNetwork.addNode(node);
			nodes.add(node);
		}

		// Add the edges induced by "origPhysNetwork" to our new nested network.
		List<CyEdge> edges = (List<CyEdge>) origPhysNetwork
			.getConnectingEdges(getIntersectingNodes(origPhysNetwork, nodes));
		for (final CyEdge edge : edges)
			nestedNetwork.addEdge(edge);

		// Add the edges induced by "origGenNetwork" to our new nested network.
		edges = (List<CyEdge>) origGenNetwork
			.getConnectingEdges(getIntersectingNodes(origGenNetwork, nodes));
		for (final CyEdge edge : edges)
			nestedNetwork.addEdge(edge);

		if (createNetworkView) {
			Cytoscape.createNetworkView(nestedNetwork);
			applyNetworkLayout(nestedNetwork, null, null);
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

	private void applyNetworkLayout(final CyNetwork network, Double cutoff, Double maxScore) {
		final CyNetworkView targetView = Cytoscape.getNetworkView(network
				.getIdentifier());
		
		targetView.applyLayout(tuning());
		//targetView.redrawGraph(false, true);
	}
	
	private CyLayoutAlgorithm tuning() {
		final CyLayoutAlgorithm fd = CyLayouts.getLayout(LAYOUT_ALGORITHM);
	
		fd.getSettings().get("defaultSpringLength").setValue("90");
		fd.getSettings().get("defaultNodeMass").setValue("8");
		fd.getSettings().updateValues();
		fd.updateSettings();
		
		return fd;
	}

	/**
	 *  @returns the list of nodes that are both, in "network", and in "nodes"
	 */
	private List<CyNode> getIntersectingNodes(final CyNetwork network, final List<CyNode> nodes) {
		final List<CyNode> commonNodes = new ArrayList<CyNode>();
		for (final CyNode node : nodes) {
			if (network.containsNode(node))
				commonNodes.add(node);
		}

		return commonNodes;
	}
}
