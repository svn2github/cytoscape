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
import cytoscape.visual.VisualStyle;


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
	public static final String REFERENCE_NETWORK_NAME_ATTRIB = "BipartiteVisualiserReferenceNetworkName"; 

	
	/////////////// Node Attribute Names /////////////
	// This is common prefix for all finders.
	private static final String MODULE_FINDER_PREFIX = "PanGIA.";
	
	// Number of nodes in a module
	private static final String GENE_COUNT = MODULE_FINDER_PREFIX + "module size";
	// And its SQRT value for visual mapping
	public static final String GENE_COUNT_SQRT = MODULE_FINDER_PREFIX + "SQRT of module size";
	
	private static final String SCORE = MODULE_FINDER_PREFIX + "score";
	
	private static final String MEMBERS = MODULE_FINDER_PREFIX + "members";
	
	private static final String PHYS_EDGE_COUNT = MODULE_FINDER_PREFIX + "physical interaction count";
	private static final String GEN_EDGE_COUNT = MODULE_FINDER_PREFIX + "genetic interaction count";
	
	/////////////// Edge Attribute Names /////////////
	public static final String EDGE_SCORE = MODULE_FINDER_PREFIX + "edge score";
	private static final String EDGE_PVALUE = MODULE_FINDER_PREFIX + "p-value";
	private static final String EDGE_GEN_EDGE_COUNT = MODULE_FINDER_PREFIX + "genetic interaction count";
	private static final String EDGE_PHYS_EDGE_COUNT = MODULE_FINDER_PREFIX + "physical interaction count";
	private static final String EDGE_SOURCE_SIZE = MODULE_FINDER_PREFIX + "source size";
	private static final String EDGE_TARGET_SIZE = MODULE_FINDER_PREFIX + "target size";
	private static final String EDGE_GEN_DENSITY = MODULE_FINDER_PREFIX + "genetic interaction density";
	
	
	private static final String COMPLEX_INTERACTION_TYPE = "module-module";
	
	private CyNetwork overviewNetwork = null;
	private Map<TypedLinkNodeModule<String, BFEdge>, CyNode> moduleToCyNodeMap;
	private int maxSize = 0;
	private final int MAX_NETWORK_VIEWS = PropUtil.getInt(CytoscapeInit
			.getProperties(), "moduleNetworkViewCreationThreshold", 0);
	private final PriorityQueue<NetworkAndScore> networksOrderedByScores = new PriorityQueue(
			100);

	VisualStyle moduleVS = Cytoscape.getVisualMappingManager().getCalculatorCatalog().
								getVisualStyle(VisualStyleObserver.VS_MODULE_NAME);

	private final CyAttributes nodeAttribs = Cytoscape.getNodeAttributes();
	private final CyAttributes edgeAttribs = Cytoscape.getEdgeAttributes();
	
	public static List<String> getEdgeAttributeNames()
	{
		List<String> names = new ArrayList<String>(2);
		names.add(EDGE_SCORE);
		names.add(EDGE_PVALUE);
		names.add(EDGE_GEN_EDGE_COUNT);
		names.add(EDGE_PHYS_EDGE_COUNT);
		names.add(EDGE_SOURCE_SIZE);
		names.add(EDGE_TARGET_SIZE);
		names.add(EDGE_GEN_DENSITY);
		
		return names;
	}
	
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
			Map<TypedLinkNodeModule<String, BFEdge>,String> module_name,
			String networkName,
			boolean isGNetSigned,
			String nodeAttrName,
			String geneticEdgeAttrName
			)
	{
		// Network attributes created here is required for managing Visual Styles.
		final CyAttributes networkAttr = Cytoscape.getNetworkAttributes();
		
		moduleToCyNodeMap = new HashMap<TypedLinkNodeModule<String, BFEdge>, CyNode>();

		overviewNetwork = Cytoscape.createNetwork( findNextAvailableNetworkName(networkName),	/* create_view = */false);
		networkAttr.setAttribute(overviewNetwork.getIdentifier(), VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.OVERVIEW.name());
		networkAttr.setUserVisible(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		networkAttr.setUserEditable(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		
		taskMonitor.setStatus("5. Generating Cytoscape networks");
		int nodeIndex = 1;
		double maxScore = Double.NEGATIVE_INFINITY;

		maxSize = 0;
		for (final TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> edge : networkOfModules.edgeIterator())
		{
			final TypedLinkNodeModule<String, BFEdge> sourceModule = edge.source().value();
			CyNode sourceNode = moduleToCyNodeMap.get(sourceModule);
			if (sourceNode == null) {
				final String nodeName = getNodeName(sourceModule,nodeIndex,module_name,nodeAttrName);
				sourceNode = makeOverviewNode(nodeName, sourceModule,nodeAttribs,physicalNetwork,geneticNetwork);
				//moduleToCyNodeMap.put(sourceModule, sourceNode);
				++nodeIndex;
			}

			final TypedLinkNodeModule<String, BFEdge> targetModule = edge.target().value();
			CyNode targetNode = moduleToCyNodeMap.get(targetModule);
			if (targetNode == null) {
				final String nodeName = getNodeName(targetModule,nodeIndex,module_name,nodeAttrName);
				targetNode = makeOverviewNode(nodeName, targetModule,nodeAttribs,physicalNetwork,geneticNetwork);
				//moduleToCyNodeMap.put(targetModule, targetNode);
				++nodeIndex;
			}
			
			
			final CyEdge newEdge = Cytoscape.getCyEdge(sourceNode, targetNode, Semantics.INTERACTION, COMPLEX_INTERACTION_TYPE, /* create = */true);
			edgeAttribs.setAttribute(newEdge.getIdentifier(), REFERENCE_NETWORK_NAME_ATTRIB, origPhysNetwork.getTitle()	+ "/" + origGenNetwork.getTitle());
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
			
			final int gConnectedness = geneticNetwork.getConnectedness(sourceModule.asStringSet(), targetModule.asStringSet());
			edgeAttribs.setAttribute(newEdge.getIdentifier(),EDGE_GEN_EDGE_COUNT, Integer.valueOf(gConnectedness));
			
			final int pConnectedness = physicalNetwork.getConnectedness(sourceModule.asStringSet(), targetModule.asStringSet());
			edgeAttribs.setAttribute(newEdge.getIdentifier(),EDGE_PHYS_EDGE_COUNT, Integer.valueOf(pConnectedness));
			
			edgeAttribs.setAttribute(newEdge.getIdentifier(), EDGE_SOURCE_SIZE,	Integer.valueOf(sourceModule.size()));
			edgeAttribs.setAttribute(newEdge.getIdentifier(), EDGE_TARGET_SIZE,	Integer.valueOf(targetModule.size()));
			
			final double density = edgeScore / (sourceModule.size() * targetModule.size());
			edgeAttribs.setAttribute(newEdge.getIdentifier(), EDGE_GEN_DENSITY, Double.valueOf(density));
		}

		edgeAttribs.setUserVisible(REFERENCE_NETWORK_NAME_ATTRIB, false);

		taskMonitor.setStatus("5. Generating network views");
		int networkViewCount = 0;
		NetworkAndScore network;
		final float percentIncrement = remainingPercentage / networksOrderedByScores.size();
		float percentCompleted = 100.0f - remainingPercentage;
		while ((network = networksOrderedByScores.poll()) != null) {
			final boolean createView = networkViewCount++ < MAX_NETWORK_VIEWS;
			final CyNetwork nestedNetwork = generateNestedNetwork(network.getNodeName(), network.getGenes(), origPhysNetwork, origGenNetwork, physicalNetwork,geneticNetwork, createView, networkAttr, isGNetSigned, geneticEdgeAttrName);
			final CyNode node = Cytoscape.getCyNode(network.getNodeName(), false);
			node.setNestedNetwork(nestedNetwork);

			percentCompleted += percentIncrement;
			taskMonitor.setPercentCompleted(Math.round(percentCompleted));
		}
		
		Cytoscape.createNetworkView(overviewNetwork);
		applyNetworkLayout(overviewNetwork, cutoff, maxScore);
		
		
	}

	private String getNodeName(TypedLinkNodeModule<String, BFEdge> module, int nodeIndex, Map<TypedLinkNodeModule<String, BFEdge>,String> module_name, String nodeAttrName)
	{
		
		
		//Auto label small complexes with the gene names
		if (module.size()<=2) 
		{
			Iterator<String> genes = module.getMemberValues().iterator();
			String newName = "["+String.valueOf(nodeAttribs.getAttribute(genes.next(),nodeAttrName));
			while (genes.hasNext()) newName+=", "+String.valueOf(nodeAttribs.getAttribute(genes.next(),nodeAttrName));
			return findNextAvailableNodeName(newName+"]");
		}
		
		//Annotate large complexes
		if (module_name!=null)
		{
			String name = module_name.get(module);
			if (name!=null) return findNextAvailableNodeName(name);
		}
		
		return findNextAvailableNodeName("Module" + nodeIndex);
	}
	
	CyNetwork getOverviewNetwork() {
		return overviewNetwork;
	}

	/**
	 * @returns a new node in the overview (module/complex) network.
	 */
	private CyNode makeOverviewNode(final String nodeName,
			final TypedLinkNodeModule<String, BFEdge> module,
			final CyAttributes nodeAttribs, TypedLinkNetwork<String, Float> physicalNetwork, TypedLinkNetwork<String, Float> geneticNetwork) {
		
		
		final CyNode newNode = Cytoscape.getCyNode(nodeName, true); // create=true
		
		moduleToCyNodeMap.put(module, newNode);
		
		overviewNetwork.addNode(newNode);
		
		//Add attributes
		final Set<String> genes = module.getMemberValues();
		final Integer geneCount = Integer.valueOf(genes.size());
		nodeAttribs.setAttribute(newNode.getIdentifier(), GENE_COUNT, geneCount);
		nodeAttribs.setAttribute(newNode.getIdentifier(), GENE_COUNT_SQRT, Math.sqrt(geneCount));
		if (genes.size() > maxSize)
			maxSize = genes.size();

		final double score = Double.valueOf(module.score());
		nodeAttribs.setAttribute(newNode.getIdentifier(), SCORE, score);

		StringBuilder members = new StringBuilder();
		for (String gene : genes)
		{
			if (members.length()!=0) members.append("|");
			members.append(gene);
		}
		nodeAttribs.setAttribute(newNode.getIdentifier(), MEMBERS, members.toString());

		
		nodeAttribs.setAttribute(newNode.getIdentifier(), PHYS_EDGE_COUNT, physicalNetwork.subNetwork(module.asStringSet()).numEdges());
		nodeAttribs.setAttribute(newNode.getIdentifier(), GEN_EDGE_COUNT, geneticNetwork.subNetwork(module.asStringSet()).numEdges());
		
		//Add to network
		networksOrderedByScores.add(new NetworkAndScore(nodeName, genes, score));

		return newNode;
	}

	private CyNetwork generateNestedNetwork(final String networkName,
			final Set<String> nodeNames, final CyNetwork origPhysNetwork,
			final CyNetwork origGenNetwork, TypedLinkNetwork<String, Float> physicalNetwork, TypedLinkNetwork<String, Float> geneticNetwork, final boolean createNetworkView,
			final CyAttributes networkAttr, boolean isGNetSigned, String geneticEdgeAttrName)
	{
		if (nodeNames.isEmpty())
			return null;

		// First, create network without view.
		final CyNetwork nestedNetwork = Cytoscape.createNetwork(networkName, overviewNetwork, false);
		
		networkAttr.setAttribute(nestedNetwork.getIdentifier(),	VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.MODULE.name());
		
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		
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
			nodeAttributes.setAttribute(node.getIdentifier(), VisualStyleObserver.PARENT_MODULE_ATTRIBUTE_NAME, networkName);
		}

		
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
				
		// Add the edges induced by "origPhysNetwork" to our new nested network.
		List<CyEdge> edges = (List<CyEdge>) origPhysNetwork.getConnectingEdges(getIntersectingNodes(origPhysNetwork, nodes));
		for (final CyEdge edge : edges)
		{
			if (physicalNetwork.containsEdge(edge.getSource().getIdentifier(),edge.getTarget().getIdentifier()))
			{
				nestedNetwork.addEdge(edge);
				cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical");
			}
		}

		// Add the edges induced by "origGenNetwork" to our new nested network.
		edges = (List<CyEdge>) origGenNetwork.getConnectingEdges(getIntersectingNodes(origGenNetwork, nodes));
		for (final CyEdge edge : edges)
		{
			if (geneticNetwork.containsEdge(edge.getSource().getIdentifier(),edge.getTarget().getIdentifier()))
			{
				nestedNetwork.addEdge(edge);
				Object existingAttribute = cyEdgeAttrs.getAttribute(edge.getIdentifier(), "PanGIA.Interaction Type");
				if (existingAttribute==null || !existingAttribute.equals("Physical"))  
				{
					if (isGNetSigned)
					{
						double genscore = (Double)cyEdgeAttrs.getAttribute(edge.getIdentifier(), geneticEdgeAttrName);
						if (genscore<0) cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic(negative)");
						else cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic(positive)");
					}else cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic");
				}
				else 
					if (isGNetSigned)
					{
						double genscore = (Double)cyEdgeAttrs.getAttribute(edge.getIdentifier(), geneticEdgeAttrName);
						if (genscore<0) cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic(negative)");
						else cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic(positive)");
					}else cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic");
			}
		}

		if (createNetworkView) {
			CyNetworkView theView = Cytoscape.createNetworkView(nestedNetwork);
			
			theView.setVisualStyle(VisualStyleObserver.VS_MODULE_NAME);
			Cytoscape.getVisualMappingManager().setVisualStyle(moduleVS);
			theView.redrawGraph(false, true);
			
			CyLayoutAlgorithm alg = cytoscape.layout.CyLayouts.getLayout("force-directed");
			theView.applyLayout(alg);			
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
