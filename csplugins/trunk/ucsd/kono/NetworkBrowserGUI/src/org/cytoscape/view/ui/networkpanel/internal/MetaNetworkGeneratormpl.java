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
	
	

	public CyNetwork generateMetaNetwrok(String metaNetName, CyNetwork parent, Set<CyGroup> groups) {
		
		String groupNodeName;
		final List<CyNode> nodes = new ArrayList<CyNode>();
		final List<CyEdge> edges = new ArrayList<CyEdge>();
		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		
		CyNode node;
		
		for(CyGroup group: groups) {
			
			groupNodeName = group.getGroupName();
			node = Cytoscape.getCyNode(groupNodeName, true);
			nodes.add(node);
			
		}
		
		
		return Cytoscape.createNetwork(nodes, edges, metaNetName, parent, true);
	}

}
