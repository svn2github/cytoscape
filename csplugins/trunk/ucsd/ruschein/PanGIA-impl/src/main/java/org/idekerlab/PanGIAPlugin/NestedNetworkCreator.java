package org.idekerlab.PanGIAPlugin;


import java.util.*;

import org.idekerlab.PanGIAPlugin.ModFinder.BFEdge;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkEdge;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNetwork;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.TypedLinkNodeModule;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
//import cytoscape.data.Semantics;
//import cytoscape.util.PropUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.subnetwork.CyRootNetwork;


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
	private int MAX_NETWORK_VIEWS = 5 ; //PropUtil.getInt(CytoscapeInit
			//.getProperties(), "moduleNetworkViewCreationThreshold", 0);
	private final PriorityQueue<NetworkAndScore> networksOrderedByScores = new PriorityQueue(
			100);

//	VisualStyle moduleVS = Cytoscape.getVisualMappingManager().getCalculatorCatalog().
//								getVisualStyle(VisualStyleObserver.VS_MODULE_NAME);

//	private final CyTable nodeAttribs = Cytoscape.getNodeAttributes();
//	private final CyTable edgeAttribs = Cytoscape.getEdgeAttributes();
	
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
		try {			
			MAX_NETWORK_VIEWS = new Integer(ServicesUtil.cytoscapePropertiesServiceRef.getProperties().getProperty("moduleNetworkViewCreationThreshold")).intValue();			
		}
		catch (Exception e){
			MAX_NETWORK_VIEWS = 5;
		}

		// Network attributes created here is required for managing Visual Styles.
		//final CyTable networkAttr = Cytoscape.getNetworkAttributes();
		
		moduleToCyNodeMap = new HashMap<TypedLinkNodeModule<String, BFEdge>, CyNode>();

		//overviewNetwork = Cytoscape.createNetwork( findNextAvailableNetworkName(networkName),	/* create_view = */false);
		overviewNetwork = ServicesUtil.cyNetworkFactoryServiceRef.getInstance();
		overviewNetwork.getCyRow().set("name",  findNextAvailableNetworkName(networkName));
		
		//networkAttr.setAttribute(overviewNetwork.getIdentifier(), VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.OVERVIEW.name());
		
		overviewNetwork.getDefaultNetworkTable().createColumn(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, String.class, false);
		overviewNetwork.getCyRow().set(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.OVERVIEW.name());
		
		
		//networkAttr.setUserVisible(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		//networkAttr.setUserEditable(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		
		taskMonitor.setStatusMessage("5. Generating Cytoscape networks");
		int nodeIndex = 1;
		double maxScore = Double.NEGATIVE_INFINITY;

		maxSize = 0;
		for (final TypedLinkEdge<TypedLinkNodeModule<String, BFEdge>, BFEdge> edge : networkOfModules.edgeIterator())
		{
			final TypedLinkNodeModule<String, BFEdge> sourceModule = edge.source().value();
			CyNode sourceNode = moduleToCyNodeMap.get(sourceModule);
			if (sourceNode == null) {
				final String nodeName = getNodeName(origPhysNetwork, sourceModule,nodeIndex,module_name,nodeAttrName);
				sourceNode = makeOverviewNode(nodeName, sourceModule,physicalNetwork,geneticNetwork);
				//moduleToCyNodeMap.put(sourceModule, sourceNode);
				++nodeIndex;
			}

			final TypedLinkNodeModule<String, BFEdge> targetModule = edge.target().value();
			CyNode targetNode = moduleToCyNodeMap.get(targetModule);
			if (targetNode == null) {
				final String nodeName = getNodeName(origPhysNetwork, targetModule,nodeIndex,module_name,nodeAttrName);
				targetNode = makeOverviewNode(nodeName, targetModule,physicalNetwork,geneticNetwork);
				//moduleToCyNodeMap.put(targetModule, targetNode);
				++nodeIndex;
			}
			
			
			//final CyEdge newEdge = Cytoscape.getCyEdge(sourceNode, targetNode, Semantics.INTERACTION, COMPLEX_INTERACTION_TYPE, /* create = */true);
			CyEdge newEdge = overviewNetwork.addEdge(sourceNode, targetNode, false);
			newEdge.getCyRow().set("interaction", COMPLEX_INTERACTION_TYPE);

			//edgeAttribs.setAttribute(newEdge.getIdentifier(), REFERENCE_NETWORK_NAME_ATTRIB, origPhysNetwork.getTitle()	+ "/" + origGenNetwork.getTitle());
			
			if (overviewNetwork.getDefaultEdgeTable().getColumn(REFERENCE_NETWORK_NAME_ATTRIB)== null){
				overviewNetwork.getDefaultEdgeTable().createColumn(REFERENCE_NETWORK_NAME_ATTRIB, String.class, false);
			}

			newEdge.getCyRow().set(REFERENCE_NETWORK_NAME_ATTRIB, origPhysNetwork.getCyRow().get("name", String.class) + "/" + origGenNetwork.getCyRow().get("name", String.class));
			//overviewNetwork.addEdge(newEdge);

			
			// Add various edge attributes.
			final double edgeScore = edge.value().link();
			//edgeAttribs.setAttribute(newEdge.getIdentifier(), EDGE_SCORE,Double.valueOf(edgeScore));
			if (overviewNetwork.getDefaultEdgeTable().getColumn(EDGE_SCORE)== null){
				overviewNetwork.getDefaultEdgeTable().createColumn(EDGE_SCORE, Double.class, false);
			}

			newEdge.getCyRow().set(EDGE_SCORE, Double.valueOf(edgeScore));
			if (edgeScore > maxScore)
				maxScore = edgeScore;

			final double pValue = edge.value().linkMerge();
			//edgeAttribs.setAttribute(newEdge.getIdentifier(), EDGE_PVALUE, Double.valueOf(pValue));
			
			if (overviewNetwork.getDefaultEdgeTable().getColumn(EDGE_PVALUE)== null){
				overviewNetwork.getDefaultEdgeTable().createColumn(EDGE_PVALUE, Double.class, false);
			}
			
			newEdge.getCyRow().set(EDGE_PVALUE, Double.valueOf(pValue));
			
			final int gConnectedness = geneticNetwork.getConnectedness(sourceModule.asStringSet(), targetModule.asStringSet());
			//edgeAttribs.setAttribute(newEdge.getIdentifier(),EDGE_GEN_EDGE_COUNT, Integer.valueOf(gConnectedness));
			
			
			if (overviewNetwork.getDefaultEdgeTable().getColumn(EDGE_GEN_EDGE_COUNT)== null){
				overviewNetwork.getDefaultEdgeTable().createColumn(EDGE_GEN_EDGE_COUNT, Integer.class, false);
			}
			
			newEdge.getCyRow().set(EDGE_GEN_EDGE_COUNT, Integer.valueOf(gConnectedness));
			
			final int pConnectedness = physicalNetwork.getConnectedness(sourceModule.asStringSet(), targetModule.asStringSet());
			//edgeAttribs.setAttribute(newEdge.getIdentifier(),EDGE_PHYS_EDGE_COUNT, Integer.valueOf(pConnectedness));
			
			
			if (overviewNetwork.getDefaultEdgeTable().getColumn(EDGE_PHYS_EDGE_COUNT)== null){
				overviewNetwork.getDefaultEdgeTable().createColumn(EDGE_PHYS_EDGE_COUNT, Integer.class, false);
			}
			
			newEdge.getCyRow().set(EDGE_PHYS_EDGE_COUNT,  Integer.valueOf(pConnectedness));
			
			//edgeAttribs.setAttribute(newEdge.getIdentifier(), EDGE_SOURCE_SIZE,	Integer.valueOf(sourceModule.size()));
			
			
			if (overviewNetwork.getDefaultEdgeTable().getColumn(EDGE_SOURCE_SIZE)== null){
				overviewNetwork.getDefaultEdgeTable().createColumn(EDGE_SOURCE_SIZE, Integer.class, false);
			}
			
			newEdge.getCyRow().set(EDGE_SOURCE_SIZE, Integer.valueOf(sourceModule.size()));
			
			//edgeAttribs.setAttribute(newEdge.getIdentifier(), EDGE_TARGET_SIZE,	Integer.valueOf(targetModule.size()));
			
			
			if (overviewNetwork.getDefaultEdgeTable().getColumn(EDGE_TARGET_SIZE)== null){
				overviewNetwork.getDefaultEdgeTable().createColumn(EDGE_TARGET_SIZE, Integer.class, false);
			}
			
			newEdge.getCyRow().set(EDGE_TARGET_SIZE, Integer.valueOf(targetModule.size()));
			
			final double density = edgeScore / (sourceModule.size() * targetModule.size());
			//edgeAttribs.setAttribute(newEdge.getIdentifier(), EDGE_GEN_DENSITY, Double.valueOf(density));
			
			
			if (overviewNetwork.getDefaultEdgeTable().getColumn(EDGE_GEN_DENSITY)== null){
				overviewNetwork.getDefaultEdgeTable().createColumn(EDGE_GEN_DENSITY, Double.class, false);
			}

			newEdge.getCyRow().set(EDGE_GEN_DENSITY, Double.valueOf(density));
		}

		//edgeAttribs.setUserVisible(REFERENCE_NETWORK_NAME_ATTRIB, false);

		taskMonitor.setStatusMessage("5. Generating network views");
		int networkViewCount = 0;
		NetworkAndScore network;
		final float percentIncrement = remainingPercentage / networksOrderedByScores.size();
		float percentCompleted = 100.0f - remainingPercentage;
		
		while ((network = networksOrderedByScores.poll()) != null) {
			final boolean createView = networkViewCount++ < MAX_NETWORK_VIEWS;
			
			System.out.println("\tBBBBBBBBBBBBBBBBBBB: 1");

			final CyNetwork nestedNetwork = generateNestedNetwork(network.getNodeName(), network.getGenes(), origPhysNetwork, origGenNetwork, physicalNetwork,geneticNetwork, createView, isGNetSigned, geneticEdgeAttrName);
			
			System.out.println("\tBBBBBBBBBBBBBBBB: 2");
			
			//Collection<CyRow> rows = overviewNetwork.getDefaultNodeTable().getMatchingRows("name", network.getNodeName());
			
			Iterator<CyNode> nodeIt= overviewNetwork.getNodeList().iterator();
			
			while (nodeIt.hasNext()){
				CyNode aNode = nodeIt.next();
				String nodeName = aNode.getCyRow().get("name", String.class);
				if (nodeName.equalsIgnoreCase(network.getNodeName())){
					aNode.setNetwork(nestedNetwork);	
					break;
				}
			}
			System.out.println("DDDDDDDDDDDDDD: 1");

			//final CyNode node = Cytoscape.getCyNode(network.getNodeName(), false);
			//node.setNetwork(nestedNetwork);				

			percentCompleted += percentIncrement;
			taskMonitor.setProgress(Math.round(percentCompleted));
		}
		System.out.println("EEEEEEEEEEEEEEE: 1");

		//Cytoscape.createNetworkView(overviewNetwork);
		ServicesUtil.cyNetworkViewFactoryServiceRef.getNetworkView(overviewNetwork);
		applyNetworkLayout(overviewNetwork, cutoff, maxScore);
		
		
	}

	private String getNodeName(CyNetwork network, TypedLinkNodeModule<String, BFEdge> module, int nodeIndex, Map<TypedLinkNodeModule<String, BFEdge>,String> module_name, String nodeAttrName)
	{
		
		
		//Auto label small complexes with the gene names
		if (module.size()<=2) 
		{
			Iterator<String> genes = module.getMemberValues().iterator();

			//String newName = "["+String.valueOf(nodeAttribs.getAttribute(genes.next(),nodeAttrName));
			Collection<CyRow> rowsCollection = network.getDefaultNodeTable().getMatchingRows(nodeAttrName, genes.next());
			
			CyRow[] rows= rowsCollection.toArray(new CyRow[0]);

			String newName = "["+String.valueOf(rows[0]);
			
			while (genes.hasNext()) 
			{
				//newName+=", "+String.valueOf(nodeAttribs.getAttribute(genes.next(),nodeAttrName));
				
				Collection<CyRow> rowsCollection1= network.getDefaultNodeTable().getMatchingRows(nodeAttrName, genes.next());
				rowsCollection1.toArray(new CyRow[0]);
				CyRow[] rows1= rowsCollection1.toArray(new CyRow[0]); ;//(CyRow[]) network.getDefaultNodeTable().getMatchingRows(nodeAttrName, genes.next()).toArray();
				String newName1 = "["+String.valueOf(rows1[0]);
				
				newName+=", "+String.valueOf(newName1);
			}
			return findNextAvailableNodeName(network, newName+"]");
		}
		
		//Annotate large complexes
		if (module_name!=null)
		{
			String name = module_name.get(module);
			if (name!=null) return findNextAvailableNodeName(network, name);
		}
		
		return findNextAvailableNodeName(network, "Module" + nodeIndex);
	}
	
	CyNetwork getOverviewNetwork() {
		return overviewNetwork;
	}

	/**
	 * @returns a new node in the overview (module/complex) network.
	 */
	private CyNode makeOverviewNode(final String nodeName,
			final TypedLinkNodeModule<String, BFEdge> module,
			TypedLinkNetwork<String, Float> physicalNetwork, TypedLinkNetwork<String, Float> geneticNetwork) {
		
		
		//final CyNode newNode = Cytoscape.getCyNode(nodeName, true); // create=true
		
		CyNode newNode = overviewNetwork.addNode();
		newNode.getCyRow().set("name", nodeName);
		
		
		moduleToCyNodeMap.put(module, newNode);
		
		//overviewNetwork.addNode(newNode);
		
		//Add attributes
		final Set<String> genes = module.getMemberValues();
		final Integer geneCount = Integer.valueOf(genes.size());
		//nodeAttribs.setAttribute(newNode.getIdentifier(), GENE_COUNT, geneCount);
		
		
		if (overviewNetwork.getDefaultNodeTable().getColumn(GENE_COUNT)== null){
			overviewNetwork.getDefaultNodeTable().createColumn(GENE_COUNT, Integer.class, false);
		}
		
		newNode.getCyRow().set(GENE_COUNT, geneCount);
		//nodeAttribs.setAttribute(newNode.getIdentifier(), GENE_COUNT_SQRT, Math.sqrt(geneCount));
		
		if (overviewNetwork.getDefaultNodeTable().getColumn(GENE_COUNT_SQRT)== null){
			overviewNetwork.getDefaultNodeTable().createColumn(GENE_COUNT_SQRT, Double.class, false);
		}
		
		newNode.getCyRow().set(GENE_COUNT_SQRT, Math.sqrt(geneCount));
		if (genes.size() > maxSize)
			maxSize = genes.size();

		final double score = Double.valueOf(module.score());
		//nodeAttribs.setAttribute(newNode.getIdentifier(), SCORE, score);
		if (overviewNetwork.getDefaultNodeTable().getColumn(SCORE)== null){
			overviewNetwork.getDefaultNodeTable().createColumn(SCORE, Double.class, false);
		}

		newNode.getCyRow().set( SCORE, score);

		StringBuilder members = new StringBuilder();
		for (String gene : genes)
		{
			if (members.length()!=0) members.append("|");
			members.append(gene);
		}
		//nodeAttribs.setAttribute(newNode.getIdentifier(), MEMBERS, members.toString());
		
		if (overviewNetwork.getDefaultNodeTable().getColumn(MEMBERS)== null){
			overviewNetwork.getDefaultNodeTable().createColumn(MEMBERS, String.class, false);
		}
		
		newNode.getCyRow().set(MEMBERS,  members.toString());
		
		//nodeAttribs.setAttribute(newNode.getIdentifier(), PHYS_EDGE_COUNT, physicalNetwork.subNetwork(module.asStringSet()).numEdges());
		
		if (overviewNetwork.getDefaultNodeTable().getColumn(PHYS_EDGE_COUNT)== null){
			overviewNetwork.getDefaultNodeTable().createColumn(PHYS_EDGE_COUNT, Integer.class, false);
		}
		
		newNode.getCyRow().set(PHYS_EDGE_COUNT, physicalNetwork.subNetwork(module.asStringSet()).numEdges());
		//nodeAttribs.setAttribute(newNode.getIdentifier(), GEN_EDGE_COUNT, geneticNetwork.subNetwork(module.asStringSet()).numEdges());
		
		
		if (overviewNetwork.getDefaultNodeTable().getColumn(GEN_EDGE_COUNT)== null){
			overviewNetwork.getDefaultNodeTable().createColumn(GEN_EDGE_COUNT, Integer.class, false);
		}
		
		newNode.getCyRow().set(GEN_EDGE_COUNT,  geneticNetwork.subNetwork(module.asStringSet()).numEdges());
		
		//Add to network
		networksOrderedByScores.add(new NetworkAndScore(nodeName, genes, score));

		return newNode;
	}

	private CyNetwork generateNestedNetwork(final String networkName,
			final Set<String> nodeNames, final CyNetwork origPhysNetwork,
			final CyNetwork origGenNetwork, TypedLinkNetwork<String, Float> physicalNetwork, TypedLinkNetwork<String, Float> geneticNetwork, final boolean createNetworkView,
			boolean isGNetSigned, String geneticEdgeAttrName)
	{
		System.out.println("Entering generateNestedNetwork()...");
		
		if (nodeNames.isEmpty())
			return null;

		// First, create network without view.
		//final CyNetwork nestedNetwork = Cytoscape.createNetwork(networkName, overviewNetwork, false);
		//final CyNetwork nestedNetwork = ServicesUtil.cyNetworkFactoryServiceRef.getInstance();

				
		// Add the nodes to our new nested network.
//		final List<CyNode> nodes = new ArrayList<CyNode>();
//		for (final String nodeName : nodeNames) {
//			final CyNode node = nestedNetwork.addNode(); //Cytoscape.getCyNode(nodeName, /* create = */false);
//			node.getCyRow().set("name", nodeName);
//			if (node == null) {
//				System.err.println("in NestedNetworkCreator.generateNestedNetwork() (in the PanGIA plug-in): unknown node: \"" + nodeName + "\"!");
//				throw new IllegalStateException("unknown node: \"" + nodeName + "\"!");
//			}
//			//nestedNetwork.addNode(node);
//			nodes.add(node);
//			//nodeAttributes.setAttribute(node.getIdentifier(), VisualStyleObserver.PARENT_MODULE_ATTRIBUTE_NAME, networkName);
//			node.getCyRow().set(VisualStyleObserver.PARENT_MODULE_ATTRIBUTE_NAME, networkName);
//		}

		// Find the list of nodes
		
		HashMap<String,CyNode> name_node_map = new HashMap<String,CyNode>();
		Iterator<CyNode> node_it = origPhysNetwork.getNodeList().iterator();
		while(node_it.hasNext()){
			CyNode node = node_it.next();
			String name = node.getCyRow().get("name", String.class);
			name_node_map.put(name, node);
		}
		final List<CyNode> nodes = new ArrayList<CyNode>();
		for (final String nodeName : nodeNames) {
			CyNode node = name_node_map.get(nodeName);
			nodes.add(node);	
		}
		
		List<CyNode> nodeList = getIntersectingNodes(origPhysNetwork, nodes);
		Iterator<CyNode> it = nodeList.iterator();
		while(it.hasNext()){

			CyNode node = it.next(); 
			if (origPhysNetwork.getDefaultNodeTable().getColumn(VisualStyleObserver.PARENT_MODULE_ATTRIBUTE_NAME) == null){
				origPhysNetwork.getDefaultNodeTable().createColumn(VisualStyleObserver.PARENT_MODULE_ATTRIBUTE_NAME, String.class, false);
			}
			node.getCyRow().set(VisualStyleObserver.PARENT_MODULE_ATTRIBUTE_NAME, networkName);
		}
		
		System.out.println("\tRRRRRRRRRRRRRRRRRRRRRRR..nodeList.size() = "+ nodeList.size());
				
		// Add the edges induced by "origPhysNetwork" to our new nested network.
		//List<CyEdge> edges = (List<CyEdge>) origPhysNetwork..getConnectingEdgeList(nodeList);
		List<CyEdge> edges = getConnectingEdges(origPhysNetwork,nodeList);

		System.out.println("\tTTTTTTTTTTTTTTTTTTT");

		for (final CyEdge edge : edges)
		{
			if (physicalNetwork.containsEdge(edge.getSource().getCyRow().get("name", String.class),edge.getTarget().getCyRow().get("name", String.class)))
			{
				//nestedNetwork.addEdge(edge);
				//cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical");
				
				if (origPhysNetwork.getDefaultEdgeTable().getColumn("PanGIA.Interaction Type") == null){
					origPhysNetwork.getDefaultEdgeTable().createColumn("PanGIA.Interaction Type", String.class, false);
				}
				
				edge.getCyRow().set("PanGIA.Interaction Type", "Physical");
			}
		}

		// Add the edges induced by "origGenNetwork" to our new nested network.
		//edges = (List<CyEdge>) origGenNetwork.getConnectingEdges(getIntersectingNodes(origGenNetwork, nodes));
		
		List<CyNode> nodeList2 = getIntersectingNodes(origGenNetwork, nodes);
				
		List<CyEdge >edges2 =  getConnectingEdges(origGenNetwork, nodeList2);
		
		System.out.println("\tUUUUUUUUUUUUUUUUUUU...edges2.size()="+ edges2.size());
		
		for (final CyEdge edge : edges2)
		{
			System.out.println("\tIIIIIIIIIIIIIIIIIIIIIIII...");

			if (geneticNetwork.containsEdge(edge.getSource().getCyRow().get("name", String.class),edge.getTarget().getCyRow().get("name", String.class)))
			{
				System.out.println("\t\tOOOOOOOOOOOOOOOOOOOOOOOOOOO...");

				//nestedNetwork.addEdge(edge);
				//Object existingAttribute = cyEdgeAttrs.getAttribute(edge.getIdentifier(), "PanGIA.Interaction Type");
				Object existingAttribute = edge.getCyRow().getRaw("PanGIA.Interaction Type");
				if (existingAttribute==null || !existingAttribute.equals("Physical"))  
				{
					if (isGNetSigned)
					{
						//double genscore = (Double)cyEdgeAttrs.getAttribute(edge.getIdentifier(), geneticEdgeAttrName);
						double genscore = edge.getCyRow().get(geneticEdgeAttrName, Double.class);
						if (genscore<0) {
							//cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic(negative)");
							edge.getCyRow().set("PanGIA.Interaction Type", "Genetic(negative)");
						}
						else {
							//cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic(positive)");
							edge.getCyRow().set("PanGIA.Interaction Type", "Genetic(positive)");
						}
					}else {
						//cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic");
						edge.getCyRow().set( "PanGIA.Interaction Type", "Genetic");
					}
				}
				else 
					if (isGNetSigned)
					{
						//double genscore = (Double)cyEdgeAttrs.getAttribute(edge.getIdentifier(), geneticEdgeAttrName);
						double genscore = edge.getCyRow().get(geneticEdgeAttrName, Double.class);
						if (genscore<0) {
							//cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic(negative)");
							edge.getCyRow().set("PanGIA.Interaction Type", "Physical&Genetic(negative)");
						}
						else {
							//cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic(positive)");
							edge.getCyRow().set( "PanGIA.Interaction Type", "Physical&Genetic(positive)");
						}
					}else {
						//cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic");
						edge.getCyRow().set("PanGIA.Interaction Type", "Physical&Genetic");
					}
			}
		}

		//Merge two edge list
		edges.addAll(edges2);
		
		System.out.println("\tLLLLLLLLLLLLL...........edges.size() = "+edges.size());
		
		//
		CyRootNetwork rootNetwork = ServicesUtil.cyRootNetworkFactory.convert(origPhysNetwork);		

		System.out.println("\tLLLLLLLLLLLLL...........1 ");

		CyNetwork nestedNetwork = rootNetwork.addSubNetwork(getIntersectingNodes(origPhysNetwork, nodes), edges); x
		
		System.out.println("\tLLLLLLLLLLLLL...........2 ");

		if (nestedNetwork.getDefaultNetworkTable().getColumn(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME)== null){
			nestedNetwork.getDefaultNetworkTable().createColumn(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, String.class, false);
		}
		nestedNetwork.getCyRow().set(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.MODULE.name());

		System.out.println("\tLLLLLLLLLLLLL...........3 ");

		if (createNetworkView) {
			CyNetworkView theView = ServicesUtil.cyNetworkViewFactoryServiceRef.getNetworkView(nestedNetwork);
			
//			theView.setVisualStyle(VisualStyleObserver.VS_MODULE_NAME);
//			Cytoscape.getVisualMappingManager().setVisualStyle(moduleVS);
//			theView.redrawGraph(false, true);
			
			CyLayoutAlgorithm alg = ServicesUtil.cyLayoutsServiceRef.getLayout("force-directed");
			alg.setNetworkView(theView);
			ServicesUtil.taskManagerServiceRef.execute(alg);			
		}

		System.out.println("\tExiting generateNestedNetwork ... ");

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
	private String findNextAvailableNodeName(CyNetwork network, final String initialPreference) {
		// Try the preferred choice first:
		//CyNode node = Cytoscape
		//		.getCyNode(initialPreference, /* create = */false);
		
		if (network.getDefaultNodeTable().getMatchingRows("name", initialPreference).isEmpty())
		//if (node == null)
			return initialPreference;

		for (int suffix = 1; true; ++suffix) {
			final String titleCandidate = initialPreference + "-" + suffix;
			
			if (network.getDefaultNodeTable().getMatchingRows("name", titleCandidate).isEmpty())			
			//node = Cytoscape.getCyNode(titleCandidate, /* create = */false);
			//if (node == null)
				return titleCandidate;
		}
	}

	/**
	 * Returns the first network with title "networkTitle" or null, if there is
	 * no network w/ this title.
	 */
	private CyNetwork getNetworkByTitle(final String networkTitle) {
		for (final CyNetwork network : ServicesUtil.cyNetworkManagerServiceRef.getNetworkSet()) {
			if (network.getCyRow().get("name", String.class).equals(networkTitle))
				return network;
		}

		return null;
	}

	private void applyNetworkLayout(final CyNetwork network, Double cutoff, Double maxScore) {
		//final CyNetworkView targetView = Cytoscape.getNetworkView(network
		//		.getIdentifier());
				
		//targetView.applyLayout(tuning());
		//targetView.redrawGraph(false, true);

		final CyNetworkView targetView = ServicesUtil.cyNetworkViewManagerServiceRef.getNetworkView(network.getSUID());
		
		CyLayoutAlgorithm alg = tuning();
		alg.setNetworkView(targetView);
		ServicesUtil.taskManagerServiceRef.execute(alg);

		targetView.updateView();
	}
	
	private CyLayoutAlgorithm tuning() {
		final CyLayoutAlgorithm fd = ServicesUtil.cyLayoutsServiceRef.getLayout(LAYOUT_ALGORITHM);
	
//		fd.getSettings().get("defaultSpringLength").setValue("90");
//		fd.getSettings().get("defaultNodeMass").setValue("8");
//		fd.getSettings().updateValues();
//		fd.updateSettings();
		
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
	
	private List<CyEdge> getConnectingEdges(CyNetwork network,List<CyNode> nodeList)
	{
		HashSet<CyEdge> edgeSet = new HashSet<CyEdge>();
		Iterator<CyNode> it = nodeList.iterator();
		while(it.hasNext()){
			CyNode node = it.next();
			List<CyEdge> edgeList = network.getAdjacentEdgeList(node, CyEdge.Type.ANY);
			edgeSet.addAll(edgeList);
		}
		
		return new ArrayList<CyEdge>(edgeSet);
	}
	
	
}
