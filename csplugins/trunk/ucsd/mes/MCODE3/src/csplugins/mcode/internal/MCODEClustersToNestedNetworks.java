package csplugins.mcode.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import csplugins.mcode.MCODEPlugin;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.PropUtil;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;



public class MCODEClustersToNestedNetworks {

	private static final URL vsLocation = MCODEPlugin.class.getResource("/csplugins/mcode/resources/MCODE_VS.props");
	private static final String VS_OVERVIEW_NAME = "MCODE Overview Style";
	private static final String VS_MODULE_NAME = "MCODE Module Style";
	private static final VisualStyle vs_overview;
	private static final VisualStyle vs_module;
	
	private static final CyLayoutAlgorithm forceDirected = CyLayouts.getLayout("force-directed");
	
	private static int MAX_NETWORK_VIEWS = PropUtil.getInt(CytoscapeInit.getProperties(), "moduleNetworkViewCreationThreshold", 10);

	static {
		Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null,vsLocation);
		vs_overview = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(VS_OVERVIEW_NAME);
		vs_module = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(VS_MODULE_NAME);	
	}

	public static void convert(MCODECluster[] clusters) {
		MCODEUtil.sortClusters(clusters);

		if ( clusters == null || clusters.length <= 0 )
			return;

		// create overview network and nested networks
		final CyNetwork overview = Cytoscape.createNetwork(
		         CyNetworkNaming.getSuggestedNetworkTitle("MCODE Result Overview"), false);

		double maxScore = Double.MIN_VALUE;

		List<CyNetwork> nets = new ArrayList<CyNetwork>();

		// create networks
		for ( MCODECluster clust : clusters ) {
			final CyNode oNode = Cytoscape.getCyNode(clust.getClusterName(), true);
			overview.addNode(oNode);

			final CyNetwork nested = createClusterNetwork(clust, overview);

			oNode.setNestedNetwork( nested );

			nets.add( nested );
			final double score = clust.getClusterScore();

			maxScore = Math.max(maxScore, score);
			Cytoscape.getNodeAttributes().setAttribute(oNode.getIdentifier(), "MCODE_Score", score);
		}
	
		addOverlapEdges(overview);
		
		// prepare the visual style
		//vs.setMaxValue(maxScore);
		//vs.initCalculators();

		// update the network views
		

		// create network views
		// NOTE:  We have to apply the visual style after creation because otherwise
		// an event gets fired and the visual style gets applied to the current network
		// (i.e. the one we're analyzing) incorrectly. Grrrrr.
		Cytoscape.createNetworkView(overview, overview.getIdentifier(), getTunedAlgorithm(), null);
		applyVisualStyle(overview,vs_overview);

		int viewCount = 0;
		for ( CyNetwork net : nets ) {
			if (++viewCount > MAX_NETWORK_VIEWS)
				break;
			Cytoscape.createNetworkView(net, net.getIdentifier(), getTunedAlgorithm(), null);
			applyVisualStyle(net,vs_module);
		}

		Cytoscape.getDesktop().setFocus(overview.getIdentifier());
		
		
		// Create an edge attribute "overlapScore", which is defined as NumberOfSharedNodes/min(two network sizes)
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		int[] edgeIndexArray = overview.getEdgeIndicesArray();
		
		for (int i=0; i<edgeIndexArray.length; i++ ){
		
			CyEdge aEdge = (CyEdge) overview.getEdge(edgeIndexArray[i]);
			int NumberOfSharedNodes = getNumberOfSharedNodes((CyNetwork)aEdge.getSource().getNestedNetwork(), 
					(CyNetwork)aEdge.getTarget().getNestedNetwork());
			
			int minNodeCount = Math.min(aEdge.getSource().getNestedNetwork().getNodeCount(), 
								aEdge.getTarget().getNestedNetwork().getNodeCount());
			
			double overlapScore = (double)NumberOfSharedNodes/minNodeCount;
			cyEdgeAttrs.setAttribute(aEdge.getIdentifier(), "overlapScore", overlapScore);			
		}
	}

	
	private static int getNumberOfSharedNodes(CyNetwork networkA, CyNetwork networkB){
		
		int[] nodeIndicesA = networkA.getNodeIndicesArray();
		int[] nodeIndicesB = networkB.getNodeIndicesArray();
		
		
		HashSet<Integer> hashSet = new HashSet<Integer>();
		for (int i=0; i< nodeIndicesA.length; i++){
			hashSet.add( new Integer(nodeIndicesA[i]));
		}

		int sharedNodeCount =0;
		for (int i=0; i< nodeIndicesB.length; i++){
			if (hashSet.contains(new Integer(nodeIndicesB[i]))){
				sharedNodeCount++;
			}
		}
		
		return sharedNodeCount;
	}

	
	private static void applyVisualStyle(CyNetwork net, VisualStyle vs) {
		CyNetworkView view = Cytoscape.getNetworkView( net.getIdentifier() );
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		view.setVisualStyle(vs.getName());
		vmm.setNetworkView(view);
		vmm.setVisualStyle(vs);
	}

	private static CyNetwork createClusterNetwork(MCODECluster clust, CyNetwork overview) {
		final CyNetwork nested = Cytoscape.createNetwork(
		        CyNetworkNaming.getSuggestedNetworkTitle(clust.getClusterName()), overview, false);

		// add nodes to cluster network
		for ( Integer nodeId : clust.getALCluster() ) {
			final CyNode nNode = (CyNode)(Cytoscape.getRootGraph().getNode(nodeId));
			nested.addNode(nNode);
		}

		// add edges to cluster network
		final CyNetwork parent = clust.getParentNetwork();
		for ( Object n1 : nested.nodesList() ) {
			for ( Object n2 : nested.nodesList() ) {
				List<CyNode> nl = new ArrayList<CyNode>();
				nl.add((CyNode)n1);
				nl.add((CyNode)n2);
				List edges = parent.getConnectingEdges(nl);
				for ( Object e : edges ) 
					nested.addEdge( (CyEdge)e );
			}
		}

		return nested;
	}

	/**
	 * Will add edges to the specified network if any of the nested networks
	 * of the nodes of the specified network share nodes.
	 */
	private static void addOverlapEdges(CyNetwork net) {
		Object[] nodes = net.nodesList().toArray();
		HashSet[] hashSet = new HashSet[nodes.length];
		for (int i=0; i< nodes.length; i++)
			hashSet[i] = new HashSet<CyNode>(((CyNode)nodes[i]).getNestedNetwork().nodesList());

		for (int i=0; i< nodes.length-1; i++) {
			for (int j=i+1; j<nodes.length; j++) {
				// determine if there are overlap between nested networks
				if ( hasTwoSetOverlap(hashSet[i], hashSet[j]) ) {
					CyEdge edge = Cytoscape.getCyEdge((CyNode)nodes[i], (CyNode)nodes[j], Semantics.INTERACTION, "overlap", true);
					net.addEdge(edge);
				}
			}
		}
    }

    private static boolean hasTwoSetOverlap(HashSet<CyNode> set1, HashSet<CyNode> set2) {
		Iterator<CyNode> it = set1.iterator();
		while (it.hasNext()) {
			if (set2.contains(it.next())) 
				return true;
		}
		return false;
	}
    
	private static CyLayoutAlgorithm getTunedAlgorithm() {
		final CyLayoutAlgorithm fd = forceDirected;
	
		fd.getSettings().get("defaultSpringLength").setValue("90");
		fd.getSettings().get("defaultNodeMass").setValue("18");
		fd.getSettings().updateValues();
		fd.updateSettings();
		
		return fd;
	}
}
