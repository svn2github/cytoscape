package org.idekerlab.PanGIAPlugin;

import java.util.ArrayList;
import java.util.List;

import org.idekerlab.PanGIAPlugin.networks.SFNetwork;

import giny.model.GraphPerspective;
import giny.model.Node;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.view.*;
import cytoscape.visual.VisualStyle;
import cytoscape.*;

public class DetailedNetworkCreator
{
	@SuppressWarnings("unchecked")
	public static void createDetailedView(CyNetworkView view)
    {
		if (view.getSelectedNodeIndices().length==0) return;
		
		if (view.getSelectedNodeIndices().length==1)
		{
			goToNestedNetwork(Cytoscape.getRootGraph().getNode(view.getSelectedNodeIndices()[0]));
			System.out.println("Going to nested network.");
			return;
		}
		
		VisualStyleObserver.setOverviewView(view);
		
		String netID = view.getNetwork().getIdentifier();
		
		PanGIAOutput output = PanGIAPlugin.output.get(netID);
		
		CyNetwork origPhysNetwork = output.getOrigPhysNetwork();
		CyNetwork origGenNetwork = output.getOrigGenNetwork();
		String physEdgeAttrName = output.getPhysEdgeAttrName();
		String genEdgeAttrName = output.getGenEdgeAttrName();
				
		//final CyAttributes cyAttributes = Cytoscape.getEdgeAttributes();
		
		String title = "Detailed View";
		
		int[] selected = view.getSelectedNodeIndices();
		
		if (selected.length<=3)
		{
			
			title = Cytoscape.getRootGraph().getNode(selected[0]).getIdentifier();
			
			for (int ni=1;ni<selected.length;ni++)
				title+=" | "+Cytoscape.getRootGraph().getNode(selected[ni]).getIdentifier();
		}
		
		String name = findNextAvailableNetworkName(title);
		
   	 	CyNetwork detailedNetwork = Cytoscape.createNetwork(name,	/* create_view = */false);
   	 	CyAttributes networkAttr = Cytoscape.getNetworkAttributes();
		networkAttr.setAttribute(detailedNetwork.getIdentifier(), VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.DETAILED.name());
		networkAttr.setUserVisible(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		networkAttr.setUserEditable(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		
		//Populate network
		//Nodes
		for (int ni : selected)
		{	
			GraphPerspective nn = Cytoscape.getRootGraph().getNode(ni).getNestedNetwork();
			if (nn!=null)
			{
				for (int ni2 : nn.getNodeIndicesArray())
					detailedNetwork.addNode(ni2);
			}
		}
		
		//Edges
		final CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		
		List<CyNode> nodes = detailedNetwork.nodesList();
		
		// Add the edges induced by "origPhysNetwork" to our new nested network.
		List<CyEdge> edges = (List<CyEdge>) origPhysNetwork.getConnectingEdges(getIntersectingNodes(origPhysNetwork, nodes));
		for (final CyEdge edge : edges)
		{
			final Double attrValue = edgeAttributes.getDoubleAttribute(edge.getIdentifier(), physEdgeAttrName);
			if (attrValue != null)
			{
				detailedNetwork.addEdge(edge);
				edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical");
			}
		}

		// Add the edges induced by "origGenNetwork" to our new nested network.
		edges = (List<CyEdge>) origGenNetwork.getConnectingEdges(getIntersectingNodes(origGenNetwork, nodes));
		for (final CyEdge edge : edges)
		{
			final Double attrValue = edgeAttributes.getDoubleAttribute(edge.getIdentifier(), genEdgeAttrName);
			if (attrValue != null)
			{
				detailedNetwork.addEdge(edge);
				Object existingAttribute = edgeAttributes.getAttribute(edge.getIdentifier(), "PanGIA.Interaction Type");
				
				if (existingAttribute==null || !existingAttribute.equals("Physical"))  
				{
					if (output.isSigned())
					{
						if (attrValue<0) edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic(negative)");
						else edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic(positive)");
					}else edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic");
				}
				else 
					if (output.isSigned())
					{
						if (attrValue<0) edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic(negative)");
						else edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic(positive)");
					}else edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic");
				
				
				//if (existingAttribute==null || !existingAttribute.equals("Physical"))  edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic");
				//else edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic");
			}
		}
		

		CyNetworkView theView = Cytoscape.createNetworkView(detailedNetwork);
		
		DetailedViewLayout.layout(theView, view);
		
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
