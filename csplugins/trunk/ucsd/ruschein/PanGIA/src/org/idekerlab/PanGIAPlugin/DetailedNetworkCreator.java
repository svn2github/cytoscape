package org.idekerlab.PanGIAPlugin;

import java.util.ArrayList;
import java.util.List;

import giny.model.GraphPerspective;
import giny.model.Node;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.view.*;
import cytoscape.*;

public class DetailedNetworkCreator
{
	@SuppressWarnings("unchecked")
	public static void createDetailedView(CyNetworkView view)
    {
		if (view.getSelectedNodeIndices().length==1)
		{
			goToNestedNetwork(Cytoscape.getRootGraph().getNode(view.getSelectedNodeIndices()[0]));
			System.out.println("Going to nested network.");
			return;
		}
		
		CyNetwork origPhysNetwork = PanGIAPlugin.output.getOrigPhysNetwork();
		CyNetwork origGenNetwork = PanGIAPlugin.output.getOrigGenNetwork();
		
		String name = findNextAvailableNetworkName("Detailed View");
		
   	 	CyNetwork detailedNetwork = Cytoscape.createNetwork(name,	/* create_view = */false);
   	 	CyAttributes networkAttr = Cytoscape.getNetworkAttributes();
		networkAttr.setAttribute(detailedNetwork.getIdentifier(), VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.DETAILED.name());
		networkAttr.setUserVisible(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		networkAttr.setUserEditable(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		
		//Populate network
		//Nodes
		for (int ni : view.getSelectedNodeIndices())
		{	
			GraphPerspective nn = Cytoscape.getRootGraph().getNode(ni).getNestedNetwork();
			if (nn!=null)
			{
				for (int ni2 : nn.getNodeIndicesArray())
					detailedNetwork.addNode(ni2);
			}
		}
		
		//Edges
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		
		List<CyNode> nodes = detailedNetwork.nodesList();
		
		// Add the edges induced by "origPhysNetwork" to our new nested network.
		List<CyEdge> edges = (List<CyEdge>) origPhysNetwork.getConnectingEdges(getIntersectingNodes(origPhysNetwork, nodes));
		for (final CyEdge edge : edges)
		{
			detailedNetwork.addEdge(edge);
			cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical");
		}

		// Add the edges induced by "origGenNetwork" to our new nested network.
		edges = (List<CyEdge>) origGenNetwork.getConnectingEdges(getIntersectingNodes(origGenNetwork, nodes));
		for (final CyEdge edge : edges)
		{
			detailedNetwork.addEdge(edge);
			Object existingAttribute = cyEdgeAttrs.getAttribute(edge.getIdentifier(), "PanGIA.Interaction Type");
			if (existingAttribute==null || !existingAttribute.equals("Physical"))  cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic");
			else cyEdgeAttrs.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic");
		}

		CyNetworkView theView = Cytoscape.createNetworkView(detailedNetwork);
			
		theView.setVisualStyle(VisualStyleObserver.VS_MODULE_NAME);
		Cytoscape.getVisualMappingManager().setVisualStyle(Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(VisualStyleObserver.VS_MODULE_NAME));
		theView.redrawGraph(false, true);	
	}
	
	public static void goToNestedNetwork(Node n)
	{
		if (n.getNestedNetwork() == null)
            return;

	    CyNetwork nestedNetwork = (CyNetwork)n.getNestedNetwork();
	
	    CyNetworkView theView = Cytoscape.getNetworkView(nestedNetwork.getIdentifier());
	    if (theView == null || theView.getIdentifier() == null)
	    {
	    	theView = Cytoscape.createNetworkView(nestedNetwork);
	    	CyLayoutAlgorithm alg = cytoscape.layout.CyLayouts.getLayout("force-directed");
	    	theView.applyLayout(alg);
	    	theView.redrawGraph(false, false);
	    }

	    Cytoscape.getDesktop().setFocus(nestedNetwork.getIdentifier());
	}
	
	private static String findNextAvailableNetworkName(final String initialPreference) {
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
	 * Returns the first network with title "networkTitle" or null, if there is
	 * no network w/ this title.
	 */
	private static CyNetwork getNetworkByTitle(final String networkTitle) {
		for (final CyNetwork network : Cytoscape.getNetworkSet()) {
			if (network.getTitle().equals(networkTitle))
				return network;
		}

		return null;
	}
	
	/**
	 *  @returns the list of nodes that are both, in "network", and in "nodes"
	 */
	private static List<CyNode> getIntersectingNodes(final CyNetwork network, final List<CyNode> nodes) {
		final List<CyNode> commonNodes = new ArrayList<CyNode>();
		for (final CyNode node : nodes) {
			if (network.containsNode(node))
				commonNodes.add(node);
		}

		return commonNodes;
	}
}
