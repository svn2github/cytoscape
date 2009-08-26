package org.cytoscape.view.ui.networkpanel.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cytoscape.view.ui.networkpanel.MetaNetworkGenerator;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;

public class MetaNetworkGeneratormpl implements MetaNetworkGenerator {
	
	private CyNetwork metaNetwork;
	private List<CyNode> nodes;
	private List<CyEdge> edges;
	
	private Integer run;
	
	
	
	final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
	
	
	public MetaNetworkGeneratormpl() {
		nodes = new ArrayList<CyNode>();
		edges = new ArrayList<CyEdge>();
		metaNetwork = Cytoscape.getRootGraph().createNetwork(nodes, edges);
		metaNetwork.setTitle("Meta Network");
		run = 0;
	}
		

	public CyNetwork generateMetaNetwork(String metaNetName, CyNetwork parent, Set<CyGroup> groups) {
		try {
		run++;
		
		String groupNodeName;
		
		
		// Parent network
		CyNode hubNode = Cytoscape.getCyNode("Network: " + parent.getTitle(), true);
		nodeAttr.setAttribute(hubNode.getIdentifier(), NODE_TYPE, NETWORK_NODE);
		
		CyNode node = null;
		CyEdge edge = null;
		for(CyGroup group: groups) {
			
			groupNodeName = group.getGroupName();
			node = Cytoscape.getCyNode(groupNodeName, true);
			nodes.add(node);	
			metaNetwork.addNode(node);
			nodeAttr.setAttribute(node.getIdentifier(), NODE_TYPE, MODULE_NODE);
			nodeAttr.setAttribute(node.getIdentifier(), EXEC_COUNTER, run);
			edge = Cytoscape.getCyEdge(hubNode, node, "interaction", "part_of", true);
			metaNetwork.addEdge(edge);
		}
		
		return metaNetwork;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public CyNetwork getMetaNetwork() {
		return metaNetwork;
	}
}