package csplugins.mcode.internal;

import java.util.List;
import java.util.ArrayList;
import cytoscape.view.CyNetworkView;
import cytoscape.util.CyNetworkNaming;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.data.CyAttributes;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

public class MCODEClustersToNestedNetworks {

	private static MCODEVisualStyle vs; 

	static {
		vs = new MCODEVisualStyle("MCODE");
		Cytoscape.getVisualMappingManager().getCalculatorCatalog().addVisualStyle(vs);
	}

	public static void convert(MCODECluster[] clusters) {

		if ( clusters.length <= 0 )
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

			final CyNetwork nested = createClusterNetwork(clust,overview);

			oNode.setNestedNetwork( nested );

			nets.add( nested );
			final double score = clust.getClusterScore();

			maxScore = Math.max(maxScore,score);
			Cytoscape.getNodeAttributes().setAttribute(oNode.getIdentifier(), "MCODE_Score", score);
		}

		// prepare the visual style
		vs.setMaxValue(maxScore);
		vs.initCalculators();

		// update the network views
		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");

		// create network views
		// NOTE:  We have to apply the visual style after creation because otherwise
		// an event gets fired and the visual style gets applied to the current network
		// (i.e. the one we're analyzing) incorrectly. Grrrrr.
		Cytoscape.createNetworkView(overview,overview.getIdentifier(),layout,null);
		applyVisualStyle(overview,vs);

		for ( CyNetwork net : nets ) { 
			Cytoscape.createNetworkView(net,net.getIdentifier(),layout,null);
			applyVisualStyle(net,vs);
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
}
