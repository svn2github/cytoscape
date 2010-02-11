package org.cytoscape.data.reader.kgml;

import giny.view.NodeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.data.reader.kgml.generated.Entry;
import org.cytoscape.data.reader.kgml.generated.Graphics;
import org.cytoscape.data.reader.kgml.generated.Pathway;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.NodeShape;

public class PathwayMapper {
	
	
	private enum KEGGShape {
		CIRCLE("circle", NodeShape.ELLIPSE), RECTANGLE("rectangle", NodeShape.RECT),
		ROUND_RECTANGLE("roundrectangle", NodeShape.ROUND_RECT);
		
		private String tag;
		private NodeShape shape;
		
		private KEGGShape(final String tag, final NodeShape shape) {
			this.shape = shape;
			this.tag = tag;
		}
		
		public static int getShape(final String shape) {
			for(KEGGShape keggShape: KEGGShape.values()) {
				if(keggShape.tag.equals(shape)) {
					return keggShape.shape.getGinyShape();
				}
			}
			
			return NodeShape.RECT.getGinyShape();
		}
	}
	
	
	private Pathway pathway;
	
	private int[] nodeIdx;
	private int[] edgeIdx;
	
	public PathwayMapper(final Pathway pathway) {
		this.pathway = pathway;
	}

	public void doMapping() {
		mapNode();
	}
	
	private final Map<String, Entry> entryMap = new HashMap<String, Entry>();
	final Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();
	
	private void mapNode() {
		
		final String pathwayID = pathway.getName();
		final List<Entry> components = pathway.getEntry();
		final List<CyEdge> edgeList = new ArrayList<CyEdge>();
		
		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		
		for(final Entry comp:components) {
			
			CyNode node = Cytoscape.getCyNode(pathwayID+ "-" + comp.getId(), true);
			nodeAttr.setAttribute(node.getIdentifier(), "KEGG.name", comp.getName());
			nodeMap.put(comp.getId(), node);
			entryMap.put(comp.getId(), comp);
			
		}
		
		nodeIdx = new int[nodeMap.values().size()];
		int idx = 0;
		for(CyNode node: nodeMap.values()) {
			nodeIdx[idx] = node.getRootGraphIndex();
			idx++;
		}
	}
	
	protected void updateView(final CyNetwork network) {
		
		final CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		
		for (String key:nodeMap.keySet()) {
			
			final NodeView nv = view.getNodeView(nodeMap.get(key));
			Graphics nodeGraphics = entryMap.get(key).getGraphics();
			nv.setXPosition(Double.parseDouble(nodeGraphics.getX()));
			nv.setYPosition(Double.parseDouble(nodeGraphics.getY()));
			nv.setWidth(Double.parseDouble(nodeGraphics.getWidth()));
			nv.setHeight(Double.parseDouble(nodeGraphics.getHeight()));
			nv.setShape(KEGGShape.getShape(nodeGraphics.getType()));
		}
	}

	public int[] getNodeIdx() {
		return nodeIdx;
	}
	
}
