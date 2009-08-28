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
import cytoscape.groups.CyGroupManager;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;

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
	
	public CyNetwork generateOverviewNetwork(CyNetwork parent, Set<CyGroup> groups) {
		List<CyNode> groupNodes = new ArrayList<CyNode>();
		List<CyEdge> groupEdges = new ArrayList<CyEdge>();
		
		for(CyGroup group: groups) {
			groupNodes.addAll(group.getNodes());
			groupEdges.addAll(group.getInnerEdges());
		}
		
		final CyNetwork overview = Cytoscape.createNetwork(groupNodes, groupEdges, "Module Overview for " + parent.getTitle(), parent, true);
		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
		Cytoscape.getNetworkView(overview.getIdentifier()).applyLayout(layout);
		
		for(CyGroup group: groups) {
			CyGroupManager.createGroup("overview." + group.getGroupName(), group.getNodes(),
				"moduleFinderView");
		}
		
		return overview;
	}
	
	public CyNetwork getMetaNetwork() {
		return metaNetwork;
	}
}