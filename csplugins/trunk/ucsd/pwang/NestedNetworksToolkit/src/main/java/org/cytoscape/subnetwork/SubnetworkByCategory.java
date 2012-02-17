package org.cytoscape.subnetwork;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

public class SubnetworkByCategory {
	
	public CyNetwork execute(CyNetwork parentNetwork, String attributeName){
		// 1. get the node list for each category
		HashMap categoryMap = getNodeCategoryMap(parentNetwork, attributeName);
					
		// 2. create subnetwork for each category			
		CyNetwork[] subnetworks = createSubnetworks(parentNetwork, categoryMap);

		//3. create an overview network for all nested network
		CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		Set overview_nodes = new HashSet();
		for (int i=0; i< subnetworks.length; i++){
			CyNode newNode =Cytoscape.getCyNode(subnetworks[i].getTitle(), true); 
			newNode.setNestedNetwork(subnetworks[i]);
			overview_nodes.add(newNode);
		}

		Set  overview_edges = getOverviewEdges(parentNetwork, overview_nodes);
		final CyNetwork overview = Cytoscape.createNetwork(overview_nodes, overview_edges, SubnetworkByCategoryPlugin.overviewTitle, parentNetwork, false);

		// add a network attribute for overview network
		CyAttributes networkAttr = Cytoscape.getNetworkAttributes();
		networkAttr.setAttribute(overview.getIdentifier(), "parentNetworkId", parentNetwork.getIdentifier());
		
		// 4. Create a view for overview network
		Cytoscape.createNetworkView(overview, overview.getTitle());	
		
		return overview;
	}
	
	
	//
	private Set getOverviewEdges(CyNetwork parentNetwork, Set overview_nodes){
	
		HashSet overview_edgeSet = new HashSet();
		
		//1. Build node and edge set for each subnetwork
		int overview_nodeCount = overview_nodes.size();
		CyNode[] overview_nodeArray = new CyNode[overview_nodeCount];
		
		HashSet[] nodeSet = new HashSet[overview_nodeCount];
		HashSet[] edgeSet = new HashSet[overview_nodeCount];			
		
		Iterator it = overview_nodes.iterator();
		int i=0;
		while(it.hasNext()){
			CyNode node = (CyNode)it.next();
			overview_nodeArray[i] = node;
			CyNetwork subnetwork = (CyNetwork)node.getNestedNetwork();
			
			// build node set
			List subnetworkNodeList = subnetwork.nodesList();
			nodeSet[i] = new HashSet();
			Iterator nodeIt = subnetworkNodeList.iterator();
			while (nodeIt.hasNext()){
				nodeSet[i].add(nodeIt.next());
			}
			
			//build edge set
			List subnetworkEdgeList = subnetwork.edgesList();
			edgeSet[i] = new HashSet();
			Iterator edgeIt = subnetworkEdgeList.iterator();
			while (edgeIt.hasNext()){
				edgeSet[i].add(edgeIt.next());
			}
			
			i++;
		}
		
		//2.Build outgoing edge set for each subnetwork
		HashSet[] outgoing_edgeSet = new HashSet[overview_nodeCount];			
		
		// build edge set for parent network
		//HashSet parentEdgeSet = new HashSet();
		Iterator parentEdgeIt = parentNetwork.edgesList().iterator();
		while (parentEdgeIt.hasNext()){
			//parentEdgeSet.add(parentEdgeIt.next());;
			CyEdge edge = (CyEdge)parentEdgeIt.next();
			
			// Check if edge is an outing edge for each subnetwork
			for (int k=0; k< overview_nodeCount; k++){
				if (nodeSet[k].contains(edge.getSource()) && !nodeSet[k].contains(edge.getTarget()) ||
					(!nodeSet[k].contains(edge.getSource()) && nodeSet[k].contains(edge.getTarget()))){
					if (outgoing_edgeSet[k] == null){
						outgoing_edgeSet[k] = new HashSet();
					}
					outgoing_edgeSet[k].add(edge);
				}
			}
		}

		// determine if there are any edge between overview_node and its attribute "overlapCount"
		for (int m=0; m<overview_nodeArray.length; m++ ){
			for (int n=m+1; n< overview_nodeArray.length; n++){
				int overlapCount = getEdgeSetOverlapCount(outgoing_edgeSet[m], outgoing_edgeSet[n]);
				
				// Create an edge if edge overlapCount is more than 0
				if (overlapCount >0){
					CyEdge newEdge =Cytoscape.getCyEdge(overview_nodeArray[m], overview_nodeArray[n],
							Semantics.INTERACTION, "pp", true);
					// add an attribute "overlapCount" for this edge
					Cytoscape.getEdgeAttributes().setAttribute(newEdge.getIdentifier(), "overlapCount", new Integer(overlapCount));
					
					overview_edgeSet.add(newEdge);
				}
			}
		}
					
		return overview_edgeSet;
	}
	
	
	//
	private int getEdgeSetOverlapCount(HashSet edgeSetA, HashSet edgeSetB){
	
		int overlapCount = 0;
		
		Iterator it = edgeSetA.iterator();
		while (it.hasNext()){
			CyEdge edge = (CyEdge)it.next();
			if (edgeSetB.contains(edge)){
				overlapCount++;
			}
		}			
		
		return overlapCount;
	}
	
	
	//
	private CyNetwork[] createSubnetworks(CyNetwork parentNet, HashMap categoryMap) {
		CyNetwork[] subnetworks = new CyNetwork[categoryMap.keySet().size()];

		Iterator it = categoryMap.keySet().iterator();
		
		int i=0;
		while (it.hasNext()){
			String categoryName = it.next().toString();
			
			// 1. get node set for this subnetwork
			Vector nodeVect = (Vector)categoryMap.get(categoryName);
			
			Set nodeSet = new HashSet();
			for (int j = 0; j < nodeVect.size(); j++) {
				CyNode oneNode = (CyNode) nodeVect.elementAt(j);
				if (oneNode != null)
					nodeSet.add(oneNode);
			}
			
			// 2. get edge set for this subnetwork
			Set edgeSet = new HashSet();
			Iterator iterator = parentNet.edgesIterator();
			while (iterator.hasNext()) {
				CyEdge edge = (CyEdge) iterator.next();
				if (nodeSet.contains(edge.getSource()) && nodeSet.contains(edge.getTarget()))
					edgeSet.add(edge);
			}

			// 3. Create subnetwork based on given nodeSet and EdgeSet
			subnetworks[i++] = Cytoscape.createNetwork(nodeSet, edgeSet, categoryName, parentNet, false);
		}
		
		return subnetworks;
	}

	
	////// attribute type could be String or Integer
	private HashMap getNodeCategoryMap(CyNetwork net, String attributeName){
		HashMap categoryMap = new HashMap();
		
		int[] nodeIndices = net.getNodeIndicesArray();
		CyAttributes attributes = Cytoscape.getNodeAttributes();

		for (int i=0; i< nodeIndices.length; i++){
			CyNode node = (CyNode) Cytoscape.getRootGraph().getNode(nodeIndices[i]);
			String nodeId = node.getIdentifier();
			
			String attValue = "unknown"; // default, if attrValue is null
			
			if (attributes.getAttribute(nodeId, attributeName) != null){
				attValue = attributes.getAttribute(nodeId, attributeName).toString();	
			}
						
			if (categoryMap.containsKey(attValue.toLowerCase())){
				Vector nodeVect = (Vector)categoryMap.get(attValue.toLowerCase());
				nodeVect.add(node);
			}
			else {// It is a new category
				Vector nodeVect = new Vector();
				nodeVect.add(node);
				categoryMap.put(attValue.toLowerCase(), nodeVect);
			}
		}
		
		return categoryMap;
	}
}
