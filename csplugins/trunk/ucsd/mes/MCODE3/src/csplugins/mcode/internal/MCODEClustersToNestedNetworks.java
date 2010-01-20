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

public class MCODEClustersToNestedNetworks {

	private static MCODEVisualStyle vs = new MCODEVisualStyle("MCode Visual Style");

	public static void convert(MCODECluster[] clusters) {

		// create overview network and nested networks
		final CyNetwork overview = Cytoscape.createNetwork(
		         CyNetworkNaming.getSuggestedNetworkTitle("MCode Result Overview"), true);

		double maxScore = Double.MIN_VALUE;

		List<CyNetworkView> views = new ArrayList<CyNetworkView>();

		// create networks
		for ( MCODECluster clust : clusters ) {
			final CyNode oNode = Cytoscape.getCyNode(clust.getClusterName(), true);
			overview.addNode(oNode);

			final CyNetwork nested = createNetwork(clust);

			oNode.setNestedNetwork( nested );

			views.add( Cytoscape.getNetworkView( nested.getIdentifier() ) );
			maxScore = Math.max(maxScore,clust.getClusterScore());
		}

		// prepare the visual style
		vs.setMaxValue(maxScore);
		vs.initCalculators();

		// update the network views
		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");

		for ( CyNetworkView view : views ) 
			updateView(view,vs,layout);

		updateView( Cytoscape.getNetworkView( overview.getIdentifier() ),vs,layout );	
	}

	private static void updateView(CyNetworkView view, MCODEVisualStyle vs, 
	                               CyLayoutAlgorithm layout) {
		view.setVisualStyle(vs.getName());
		layout.doLayout(view);
		view.redrawGraph(true,true);
	}

	private static CyNetwork createNetwork(MCODECluster clust) {
		final CyNetwork nested = Cytoscape.createNetwork(
		        CyNetworkNaming.getSuggestedNetworkTitle(clust.getClusterName()), true);

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
