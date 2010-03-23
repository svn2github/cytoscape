package csplugins.mcode.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import csplugins.mcode.MCODEPlugin;
import cytoscape.view.CyNetworkView;
import cytoscape.util.CyNetworkNaming;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.data.Semantics;
import cytoscape.util.PropUtil;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;



public class MCODEClustersToNestedNetworks {

	public static URL url1 = MCODEPlugin.class.getResource("/csplugins/mcode/resources/MCODE_OVERVIEW_VS.props");
	public static URL url2 = MCODEPlugin.class.getResource("/csplugins/mcode/resources/MCODE_MODULE_VS.props");
	private static String VS_OVERVIEW_NAME = "MCODE";
	private static String VS_MODULE_NAME = "MCODE_MODULE";
	private static VisualStyle vs_overview = null;
	private static VisualStyle vs_module = null;
	
	//private static MCODEVisualStyle vs1;
	private static int MAX_NETWORK_VIEWS = PropUtil.getInt(CytoscapeInit.getProperties(), "moduleNetworkViewCreationThreshold", 0);

	static {
		
		// Create visualStyles based on the definition in property files
		Set<String> names = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyleNames();
		if (!names.contains(VS_OVERVIEW_NAME)){
			Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null,url1);
		}
		if (!names.contains(VS_MODULE_NAME)){
			Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null,url2);
		}
	}

	public static void convert(MCODECluster[] clusters) {
		MCODEUtil.sortClusters(clusters);

		if ( clusters == null || clusters.length <= 0 )
			return;

		// create overview network and nested networks
		final CyNetwork overview = Cytoscape.createNetwork(
		         CyNetworkNaming.getSuggestedNetworkTitle("MCode Result Overview"), false);

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


		Object[] styles = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyles().toArray();
		for (int i=0; i< styles.length; i++){
			VisualStyle vs = (VisualStyle) styles[i];
			if (vs.getName().equalsIgnoreCase(VS_OVERVIEW_NAME)){
				vs_overview = vs;
			}
			else if (vs.getName().equalsIgnoreCase(VS_MODULE_NAME)){
				vs_module = vs;
			}
		}
		
		// prepare the visual style
		//vs.setMaxValue(maxScore);
		//vs.initCalculators();

		// update the network views
		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");

		// create network views
		// NOTE:  We have to apply the visual style after creation because otherwise
		// an event gets fired and the visual style gets applied to the current network
		// (i.e. the one we're analyzing) incorrectly. Grrrrr.
		Cytoscape.createNetworkView(overview, overview.getIdentifier(), layout, null);
		applyVisualStyle(overview,vs_overview);

		int viewCount = 0;
		for ( CyNetwork net : nets ) {
			if (++viewCount > MAX_NETWORK_VIEWS)
				break;
			Cytoscape.createNetworkView(net, net.getIdentifier(), layout, null);
			applyVisualStyle(net,vs_module);
		}

		Cytoscape.getDesktop().setFocus(overview.getIdentifier());
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
}
