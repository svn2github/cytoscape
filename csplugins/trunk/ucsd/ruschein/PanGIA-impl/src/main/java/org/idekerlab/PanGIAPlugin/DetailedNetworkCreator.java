package org.idekerlab.PanGIAPlugin;

import java.util.ArrayList;
import java.util.List;

import org.idekerlab.PanGIAPlugin.networks.SFNetwork;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;

import org.cytoscape.view.vizmap.VisualStyle;
import java.util.Set;
import org.cytoscape.view.layout.CyLayoutAlgorithm;


public class DetailedNetworkCreator
{
	@SuppressWarnings("unchecked")
	public static void createDetailedView(CyNetworkView view)
    {
    
		List<CyNode> nodes_selected = CyTableUtil.getNodesInState(view.getModel(), CyNetwork.SELECTED, true);
		
		if (nodes_selected.size()==0) return;
		
		if (nodes_selected.size()==1)
		{
			//goToNestedNetwork(Cytoscape.getRootGraph().getNode(view.getSelectedNodeIndices()[0]));
			goToNestedNetwork(nodes_selected.get(0));
			System.out.println("Going to nested network.");
			return;
		}
		
		VisualStyleObserver.setOverviewView(view);
		
		String netID = view.getModel().getCyRow().get("name", String.class);
		
		PanGIAOutput output = PanGIAPlugin.output.get(netID);
		
		CyNetwork origPhysNetwork = output.getOrigPhysNetwork();
		CyNetwork origGenNetwork = output.getOrigGenNetwork();
		String physEdgeAttrName = output.getPhysEdgeAttrName();
		String genEdgeAttrName = output.getGenEdgeAttrName();
				
		//final CyAttributes cyAttributes = Cytoscape.getEdgeAttributes();
		
		String title = "Detailed View";
		
		//int[] selected = view.getSelectedNodeIndices();
		List<CyNode> nodes_selected2 = CyTableUtil.getNodesInState(view.getModel(), CyNetwork.SELECTED, true);
		
		if (nodes_selected2.size()<=3)
		{
			
			title = nodes_selected2.get(0).getCyRow().get("name", String.class); //Cytoscape.getRootGraph().getNode(selected[0]).getIdentifier();
			
			for (int ni=1;ni<nodes_selected2.size();ni++)
				title+=" | "+nodes_selected2.get(ni).getCyRow().get("name",String.class); //getIdentifier();
		}
		
		String name = findNextAvailableNetworkName(title);
		
   	 	//CyNetwork detailedNetwork = Cytoscape.createNetwork(name,	/* create_view = */false);
   	 	CyNetwork detailedNetwork = ServicesUtil.cyNetworkFactoryServiceRef.getInstance();
   	 	
   	 	CyTable networkAttr = detailedNetwork.getDefaultNetworkTable(); //Cytoscape.getNetworkAttributes();
		//networkAttr.setAttribute(detailedNetwork.getIdentifier(), VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.DETAILED.name());
		detailedNetwork.getCyRow().set(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.DETAILED.name());
		//networkAttr.createColumn(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, String.class, false);
		
		//networkAttr.setUserVisible(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		//networkAttr.setUserEditable(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		
		//Populate network
		//Nodes
		//for (int ni : selected)
		for (CyNode ni: nodes_selected2)
		{	
			CyNetwork nn = ni.getNetwork();
			if (nn!=null)
			{
				for (int ni2 : nn.getNodeIndicesArray())
					detailedNetwork.addNode(ni2);
				
//				List<CyNode> nn_nodelist = nn.getNodeList();
//				for (CyNode nn_node: nn_nodelist){
//					detailedNetwork.addNode(nn_node.getSUID());
//				}
			}
		}
		
		//Edges
		final CyTable edgeAttributes = Cytoscape.getEdgeAttributes();
		
		List<CyNode> nodes = detailedNetwork.getNodeList();
		
		// Add the edges induced by "origPhysNetwork" to our new nested network.
		List<CyEdge> edges = (List<CyEdge>) origPhysNetwork.getConnectingEdges(getIntersectingNodes(origPhysNetwork, nodes));
		for (final CyEdge edge : edges)
		{
			//final Double attrValue = edgeAttributes.getDoubleAttribute(edge.getCyRow().get("name", String.class), physEdgeAttrName);
			final Double attrValue =edge.getCyRow().get(physEdgeAttrName, Double.class);
			if (attrValue != null)
			{
				detailedNetwork.addEdge(edge);
				//edgeAttributes.setAttribute(edge.getCyRow().get("name", String.class), "PanGIA.Interaction Type", "Physical");
				edge.getCyRow().set("PanGIA.Interaction Type", "Physical");
			}
		}

		// Add the edges induced by "origGenNetwork" to our new nested network.
		edges = (List<CyEdge>) origGenNetwork.getConnectingEdges(getIntersectingNodes(origGenNetwork, nodes));
		for (final CyEdge edge : edges)
		{
			//final Double attrValue = edgeAttributes.getDoubleAttribute(edge.getCyRow().get("name", String.class), genEdgeAttrName);
			final Double attrValue =edge.getCyRow().get(genEdgeAttrName, Double.class);
			if (attrValue != null)
			{
				detailedNetwork.addEdge(edge);
				//Object existingAttribute = edgeAttributes.getAttribute(edge.getCyRow().get("name", String.class), "PanGIA.Interaction Type");
				Object existingAttribute = edge.getCyRow().get("PanGIA.Interaction Type", String.class);
				if (existingAttribute==null || !existingAttribute.equals("Physical"))  
				{
					if (output.isSigned())
					{
						if (attrValue<0) { 
							//edgeAttributes.setAttribute(edge.getCyRow().get("name",String.class), "PanGIA.Interaction Type", "Genetic(negative)");
							edge.getCyRow().set("PanGIA.Interaction Type", "Genetic(negative)");
						}
						else { 
							//edgeAttributes.setAttribute(edge.getCyRow().get("name", String.class), "PanGIA.Interaction Type", "Genetic(positive)");
							edge.getCyRow().set("PanGIA.Interaction Type", "Genetic(positive)");
						}
					}else {
						//edgeAttributes.setAttribute(edge.getCyRow().get("name", String.class), "PanGIA.Interaction Type", "Genetic");
						edge.getCyRow().set("PanGIA.Interaction Type", "Genetic");
					}
				}
				else 
					if (output.isSigned())
					{
						if (attrValue<0) 
							{
							//edgeAttributes.setAttribute(edge.getCyRow().get("name", String.class), "PanGIA.Interaction Type", "Physical&Genetic(negative)");
							edge.getCyRow().set("PanGIA.Interaction Type", "Physical&Genetic(negative)");
							}
						else 
							{
							//edgeAttributes.setAttribute(edge.getCyRow().get("name", String.class), "PanGIA.Interaction Type", "Physical&Genetic(positive)");
							edge.getCyRow().set("PanGIA.Interaction Type", "Physical&Genetic(positive)");
							}
					}else
					{
						//edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic");
						edge.getCyRow().set("PanGIA.Interaction Type", "Physical&Genetic");
					}
				
				//if (existingAttribute==null || !existingAttribute.equals("Physical"))  edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Genetic");
				//else edgeAttributes.setAttribute(edge.getIdentifier(), "PanGIA.Interaction Type", "Physical&Genetic");
			}
		}
		

		
		CyNetworkView theView = ServicesUtil.cyNetworkViewFactoryServiceRef.getNetworkView(detailedNetwork, false);
		
		
		DetailedViewLayout.layout(theView, view);
		
		//theView.setVisualStyle(VisualStyleObserver.VS_MODULE_NAME);

//		VisualStyle vs = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(VisualStyleObserver.VS_MODULE_NAME);
//		ServicesUtil.visualMappingManagerRef.setVisualStyle(vs, theView);
		//Cytoscape.getVisualMappingManager().setVisualStyle();
		
		theView.updateView(); //.redrawGraph(false, true);	
	}
	
	public static void goToNestedNetwork(CyNode n)
	{
		if (n.getNetwork() == null)
            return;

	    CyNetwork nestedNetwork = (CyNetwork)n.getNetwork();
	
	    CyNetworkView theView = ServicesUtil.cyNetworkViewManagerServiceRef.getNetworkView(nestedNetwork.getSUID());
	    //if (theView == null || theView.getIdentifier() == null)
	    if (theView == null)
	    {
	    	theView = ServicesUtil.cyNetworkViewFactoryServiceRef.getNetworkView(nestedNetwork);

	    	CyLayoutAlgorithm alg = ServicesUtil.cyLayoutsServiceRef.getLayout("force-directed");
	    	alg.setNetworkView(theView);
	    	ServicesUtil.taskManagerServiceRef.execute(alg);
	    	theView.updateView();
	    }

	    //ServicesUtil.cySwingApplicationServiceRef.getJFrame().setf
	    //Cytoscape.getDesktop().setFocus(nestedNetwork.getIdentifier());
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
		Set<CyNetwork> networks = ServicesUtil.cyNetworkManagerServiceRef.getNetworkSet();
		for (final CyNetwork network : networks) {
			String title = network.getCyRow().get("name", String.class);
			if (title.equals(networkTitle))
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
