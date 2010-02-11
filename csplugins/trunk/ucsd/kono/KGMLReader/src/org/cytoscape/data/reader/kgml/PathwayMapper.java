package org.cytoscape.data.reader.kgml;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.data.reader.kgml.generated.Entry;
import org.cytoscape.data.reader.kgml.generated.Pathway;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

public class PathwayMapper {
	
	private Pathway pathway;
	
	public PathwayMapper(final Pathway pathway) {
		this.pathway = pathway;
	}

	public void doMapping() {
		mapNode();
		
		
	}
	
	private void mapNode() {
		final List<Entry> components = pathway.getEntry();
		final List<CyNode> nodeList = new ArrayList<CyNode>();
		final List<CyEdge> edgeList = new ArrayList<CyEdge>();
		
		for(final Entry comp:components) {
			final CyNode node = Cytoscape.getCyNode(comp.getId(), true);
			nodeList.add(node);
			
			
		}
		Cytoscape.createNetwork(nodeList, edgeList, pathway.getTitle());		
	}
	
}
