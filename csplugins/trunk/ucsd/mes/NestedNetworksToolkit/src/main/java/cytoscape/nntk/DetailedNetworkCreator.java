package cytoscape.nntk;

import java.util.ArrayList;
import java.util.List;


import giny.model.GraphPerspective;
import giny.model.Node;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.view.*;
import cytoscape.visual.VisualStyle;
import cytoscape.*;
import cytoscape.util.CyNetworkNaming;

public class DetailedNetworkCreator
{
	private static String edgeAttrASDF = "PanGIA.Interaction Type";
	private static String edgeValuePHYSICAL = "Physical";
	private static String edgeValueGENETIC_NEGATIVE = "Genetic(negative)";
	private static String edgeValueGENETIC_POSITIVE = "Genetic(postive)";
	private static String edgeValueGENETIC = "Genetic";
	private static String edgeValuePHYS_GEN_POS = "Physical&Genetic(positive)";
	private static String edgeValuePHYS_GEN_NEG = "Physical&Genetic(negative)";
	private static String edgeValuePHYS_GEN = "Physical&Genetic";
				
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
		
		//VisualStyleObserver.setOverviewView(view);
		
		String netID = view.getNetwork().getIdentifier();
		
		//PanGIAOutput output = PanGIAPlugin.output.get(netID);
		
		CyNetwork origPhysNetwork = null; //output.getOrigPhysNetwork();
		CyNetwork origGenNetwork = null; //output.getOrigGenNetwork();
		String physEdgeAttrName = ""; //output.getPhysEdgeAttrName();
		String genEdgeAttrName = ""; //output.getGenEdgeAttrName();
				
		String title = "Detailed View";
		
		int[] selected = view.getSelectedNodeIndices();
		
		if (selected.length<=3)
		{
			
			title = Cytoscape.getRootGraph().getNode(selected[0]).getIdentifier();
			
			for (int ni=1;ni<selected.length;ni++)
				title+=" | "+Cytoscape.getRootGraph().getNode(selected[ni]).getIdentifier();
		}
		
		String name = CyNetworkNaming.getSuggestedNetworkTitle(title);
		
   	 	CyNetwork detailedNetwork = Cytoscape.createNetwork(name,	/* create_view = */false);
   	 	CyAttributes networkAttr = Cytoscape.getNetworkAttributes();
//		networkAttr.setAttribute(detailedNetwork.getIdentifier(), VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, NetworkType.DETAILED.name());
//		networkAttr.setUserVisible(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
//		networkAttr.setUserEditable(VisualStyleObserver.NETWORK_TYPE_ATTRIBUTE_NAME, false);
		
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
				edgeAttributes.setAttribute(edge.getIdentifier(), edgeAttrASDF, edgeValuePHYSICAL);
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
				Object existingAttribute = edgeAttributes.getAttribute(edge.getIdentifier(), edgeAttrASDF);
				
				if (existingAttribute==null || !existingAttribute.equals(edgeValuePHYSICAL))  
				{
					if (true /* FIXME! output.isSigned() */)
					{
						if (attrValue<0) edgeAttributes.setAttribute(edge.getIdentifier(), edgeAttrASDF, edgeValueGENETIC_NEGATIVE);
						else edgeAttributes.setAttribute(edge.getIdentifier(), edgeAttrASDF, edgeValueGENETIC_POSITIVE);
					}else edgeAttributes.setAttribute(edge.getIdentifier(), edgeAttrASDF, edgeValueGENETIC);
				}
				else 
					if (true /* FIXME! output.isSigned() */)
					{
						if (attrValue<0) edgeAttributes.setAttribute(edge.getIdentifier(), edgeAttrASDF, edgeValuePHYS_GEN_NEG);
						else edgeAttributes.setAttribute(edge.getIdentifier(), edgeAttrASDF, edgeValuePHYS_GEN_POS);
					}else edgeAttributes.setAttribute(edge.getIdentifier(), edgeAttrASDF, edgeValuePHYS_GEN);
				
				
				//if (existingAttribute==null || !existingAttribute.equals(edgeValuePHYSICAL))  edgeAttributes.setAttribute(edge.getIdentifier(), edgeAttrASDF, edgeValueGENETIC);
				//else edgeAttributes.setAttribute(edge.getIdentifier(), edgeAttrASDF, edgeValuePHYS_GEN);
			}
		}
		

		CyNetworkView theView = Cytoscape.createNetworkView(detailedNetwork);
		
		DetailedViewLayout.layout(theView, view);
		
//		theView.setVisualStyle(VisualStyleObserver.VS_MODULE_NAME);
//		Cytoscape.getVisualMappingManager().setVisualStyle(Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(VisualStyleObserver.VS_MODULE_NAME));
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
