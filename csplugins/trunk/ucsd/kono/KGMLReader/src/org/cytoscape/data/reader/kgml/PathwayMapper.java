package org.cytoscape.data.reader.kgml;

import giny.view.NodeView;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.data.reader.kgml.generated.Entry;
import org.cytoscape.data.reader.kgml.generated.Graphics;
import org.cytoscape.data.reader.kgml.generated.Pathway;
import org.cytoscape.data.reader.kgml.generated.Product;
import org.cytoscape.data.reader.kgml.generated.Reaction;
import org.cytoscape.data.reader.kgml.generated.Relation;
import org.cytoscape.data.reader.kgml.generated.Substrate;
import org.cytoscape.data.reader.kgml.generated.Subtype;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.LineStyle;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public class PathwayMapper {
	
	private final Pathway pathway;
	private final String pathwayName;

	private int[] nodeIdx;
	private int[] edgeIdx;
	
	private Map<String, CyNode> reaction2Entry;
	
	private static final String KEGG_NAME = "KEGG.name";
	private static final String KEGG_ENTRY_TYPE = "KEGG.entry";
	private static final String KEGG_LABEL = "KEGG.label";
	private static final String KEGG_RELATION_TYPE = "KEGG.relation";
	
	
	private static final String KEGG_LINK = "KEGG.link";

	public PathwayMapper(final Pathway pathway) {
		this.pathway = pathway;
		this.pathwayName = pathway.getName();
		reaction2Entry = new HashMap<String, CyNode>();
	}

	public void doMapping() {
		mapNode();
		mapEdge();
		//readReaction();
	}

	private final Map<String, Entry> entryMap = new HashMap<String, Entry>();
	final Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();
	final Map<String, CyNode> cpdMap = new HashMap<String, CyNode>();

	private void mapNode() {

		final String pathwayID = pathway.getName();
		final List<Entry> components = pathway.getEntry();
		final List<CyEdge> edgeList = new ArrayList<CyEdge>();

		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();

		for (final Entry comp : components) {
			if (!comp.getGraphics().getType().equals(KEGGShape.LINE.getTag())) {
				CyNode node = Cytoscape.getCyNode(pathwayID + "-"
						+ comp.getId(), true);
				nodeAttr.setAttribute(node.getIdentifier(), KEGG_NAME, comp
						.getName());
				nodeAttr.setAttribute(node.getIdentifier(), KEGG_LINK, comp.getLink());
				nodeAttr.setAttribute(node.getIdentifier(), KEGG_ENTRY_TYPE, comp.getType());
				
				final String reaction = comp.getReaction();
				if (reaction2Entry.containsKey(reaction))
					System.out.println("######## Dup: " + comp.getReaction());
				
				if (reaction != null)
					reaction2Entry.put(reaction, node);
				
				final Graphics graphics = comp.getGraphics();
				if(graphics != null && graphics.getName() != null) {
					nodeAttr.setAttribute(node.getIdentifier(), KEGG_LABEL, graphics.getName());
				}
				nodeMap.put(comp.getId(), node);
				entryMap.put(comp.getId(), comp);
				cpdMap.put(comp.getName(), node);
			}
		}

		nodeIdx = new int[nodeMap.values().size()];
		int idx = 0;
		for (CyNode node : nodeMap.values()) {
			nodeIdx[idx] = node.getRootGraphIndex();
			idx++;
		}
	}
	
	
	private void readReaction() {
		final List<Reaction> reactions = pathway.getReaction();
		final List<CyEdge> edges = new ArrayList<CyEdge>();
		
		for (Reaction r: reactions) {
			final List<Product> products = r.getProduct();
			final List<Substrate> substrates = r.getSubstrate();
			final String name = r.getName();
			
			final CyNode node = this.reaction2Entry.get(name);
			for(Substrate s: substrates) {
				CyNode source = cpdMap.get(s.getName());
				CyEdge edge1 = Cytoscape.getCyEdge(source, node, "interaction", name, true);
				edges.add(edge1);
			}
			
			for(Product p: products) {
				CyNode target = cpdMap.get(p.getName());
				CyEdge edge1 = Cytoscape.getCyEdge(node, target, "interaction", name, true);
				edges.add(edge1);
			}
		}
		
		edgeIdx = new int[edges.size()];
		int idx = 0;
		for (CyEdge edge : edges) {
			edgeIdx[idx] = edge.getRootGraphIndex();
			idx++;
		}
	}
	
	
	private void mapEdge() {
		final List<Relation> relations = pathway.getRelation();
		final List<CyEdge> edges = new ArrayList<CyEdge>();
		
		final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		
		for(Relation rel: relations) {
			final String ent1 = rel.getEntry1();
			final String ent2 = rel.getEntry2();
			
			final List<Subtype> subs = rel.getSubtype();
			final String type = rel.getType();
			CyNode source = nodeMap.get(ent1);
			CyNode target = nodeMap.get(ent2);
			
			for(Subtype sub: subs) {
				CyNode hub = nodeMap.get(sub.getValue());
				System.out.println(source.getIdentifier());
				System.out.println(target.getIdentifier());
				System.out.println(hub.getIdentifier() + "\n\n");
				CyEdge edge1 = Cytoscape.getCyEdge(source, hub, "interaction", type, true);
				edges.add(edge1);
				
				CyEdge edge2 = Cytoscape.getCyEdge(hub, target, "interaction", type, true);
				
				edges.add(edge2);
				
				edgeAttr.setAttribute(edge1.getIdentifier(), KEGG_RELATION_TYPE, type);
				edgeAttr.setAttribute(edge2.getIdentifier(), KEGG_RELATION_TYPE, type);
			}	
		}
		
		edgeIdx = new int[edges.size()];
		int idx = 0;
		for (CyEdge edge : edges) {
			edgeIdx[idx] = edge.getRootGraphIndex();
			idx++;
		}
		
	}
	
	private void mapReaction() {
		final List<Reaction> reactions = pathway.getReaction();
		final List<CyEdge> edges = new ArrayList<CyEdge>();
		
		final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		
//		for(Reaction rel: reactions) {
//			entry = reaction2Entry.get(rel.getName())
//			rel.getSubstrate();
//		
//			
//			final List<Subtype> subs = rel.getSubtype();
//			final String type = rel.getType();
//			CyNode source = nodeMap.get(ent1);
//			CyNode target = nodeMap.get(ent2);
//			
//			for(Subtype sub: subs) {
//				CyNode hub = nodeMap.get(sub.getValue());
//				System.out.println(source.getIdentifier());
//				System.out.println(target.getIdentifier());
//				System.out.println(hub.getIdentifier() + "\n\n");
//				CyEdge edge1 = Cytoscape.getCyEdge(source, hub, "interaction", type, true);
//				CyEdge edge2 = Cytoscape.getCyEdge(hub, target, "interaction", type, true);
//				edges.add(edge1);
//				edges.add(edge2);
//				
//				edgeAttr.setAttribute(edge1.getIdentifier(), this.KEGG_RELATION_TYPE, type);
//			}	
//		}
//		
//		edgeIdx = new int[edges.size()];
//		int idx = 0;
//		for (CyEdge edge : edges) {
//			edgeIdx[idx] = edge.getRootGraphIndex();
//			idx++;
//		}
		
	}

	protected void updateView(final CyNetwork network) {

		final String vsName = "KEGG: " + pathway.getTitle() + "(" + pathwayName + ")";
		final VisualStyle defStyle = new VisualStyle(vsName);

		NodeAppearanceCalculator nac = defStyle.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator eac = defStyle.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator gac = defStyle
				.getGlobalAppearanceCalculator();

		// Default values
		final Color nodeColor = Color.WHITE;
		final Color nodeLineColor = new Color(20, 20, 20);
		final Color nodeLabelColor = new Color(30, 30, 30);
		
		final Color geneNodeColor = new Color(153,255,153);

		final Font nodeLabelFont = new Font("SansSerif", 7, Font.PLAIN);

		gac.setDefaultBackgroundColor(Color.white);

		final PassThroughMapping m = new PassThroughMapping("", KEGG_LABEL);

		final Calculator nodeLabelMappingCalc = new BasicCalculator(vsName + "-"
				+ "NodeLabelMapping", m, VisualPropertyType.NODE_LABEL);

		nac.setCalculator(nodeLabelMappingCalc);
		
		nac.setNodeSizeLocked(false);
		
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,
				nodeColor);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE,
				NodeShape.ROUND_RECT);

		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR,
				nodeLineColor);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH,
				1);
		
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR,
				nodeLabelColor);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FONT_FACE, nodeLabelFont);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FONT_SIZE, 6);
		
		//Default Edge appr
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_TGTARROW_SHAPE, ArrowShape.DELTA);
		final DiscreteMapping edgeLineStyle = new DiscreteMapping(LineStyle.SOLID,
				KEGG_RELATION_TYPE, ObjectMapping.EDGE_MAPPING);
		final Calculator edgeLineStyleCalc = new BasicCalculator(vsName + "-"
				+ "EdgeLineStyleMapping", edgeLineStyle,
				VisualPropertyType.EDGE_LINE_STYLE);
		edgeLineStyle.putMapValue(KEGGRelationType.MAPLINK.getTag(), LineStyle.LONG_DASH);
		eac.setCalculator(edgeLineStyleCalc);
		
		
		final DiscreteMapping nodeShape = new DiscreteMapping(NodeShape.RECT,
				KEGG_ENTRY_TYPE, ObjectMapping.NODE_MAPPING);
		final Calculator nodeShapeCalc = new BasicCalculator(vsName + "-"
				+ "NodeShapeMapping", nodeShape,
				VisualPropertyType.NODE_SHAPE);
		nodeShape.putMapValue(KEGGEntryType.MAP.getTag(), NodeShape.ROUND_RECT);
		nodeShape.putMapValue(KEGGEntryType.GENE.getTag(), NodeShape.RECT);
		nodeShape.putMapValue(KEGGEntryType.ORTHOLOG.getTag(), NodeShape.RECT);
		nodeShape.putMapValue(KEGGEntryType.COMPOUND.getTag(), NodeShape.ELLIPSE);
		nac.setCalculator(nodeShapeCalc);
		
		final DiscreteMapping nodeColorMap = new DiscreteMapping(nodeColor,
				KEGG_ENTRY_TYPE, ObjectMapping.NODE_MAPPING);
		final Calculator nodeColorCalc = new BasicCalculator(vsName + "-"
				+ "NodeColorMapping", nodeColorMap,
				VisualPropertyType.NODE_FILL_COLOR);
		nodeColorMap.putMapValue(KEGGEntryType.GENE.getTag(), geneNodeColor);
		nac.setCalculator(nodeColorCalc);
		
		final DiscreteMapping nodeBorderColorMap = new DiscreteMapping(nodeColor,
				KEGG_ENTRY_TYPE, ObjectMapping.NODE_MAPPING);
		final Calculator nodeBorderColorCalc = new BasicCalculator(vsName + "-"
				+ "NodeBorderColorMapping", nodeBorderColorMap,
				VisualPropertyType.NODE_BORDER_COLOR);
		nodeBorderColorMap.putMapValue(KEGGEntryType.MAP.getTag(), Color.BLUE);
		nac.setCalculator(nodeBorderColorCalc);
		
		final DiscreteMapping nodeWidth = new DiscreteMapping(30,
				"ID", ObjectMapping.NODE_MAPPING);
		final Calculator nodeWidthCalc = new BasicCalculator(vsName + "-"
				+ "NodeWidthMapping", nodeWidth,
				VisualPropertyType.NODE_WIDTH);
		final DiscreteMapping nodeHeight = new DiscreteMapping(30,
				"ID", ObjectMapping.NODE_MAPPING);
		final Calculator nodeHeightCalc = new BasicCalculator(vsName + "-"
				+ "NodeHeightMapping", nodeHeight,
				VisualPropertyType.NODE_HEIGHT);

		nac.setCalculator(nodeHeightCalc);
		nac.setCalculator(nodeWidthCalc);
		

		final CyNetworkView view = Cytoscape.getNetworkView(network
				.getIdentifier());
		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();

		
		nodeWidth.setControllingAttributeName("ID", null,false);
		nodeHeight.setControllingAttributeName("ID", null,false);
		
		for (String key : nodeMap.keySet()) {

			Graphics nodeGraphics = entryMap.get(key).getGraphics();

			if (KEGGShape.getShape(nodeGraphics.getType()) != -1) {
				final String nodeID = nodeMap.get(key).getIdentifier();
				final NodeView nv = view.getNodeView(nodeMap.get(key));

				nv.setXPosition(Double.parseDouble(nodeGraphics.getX()));
				nv.setYPosition(Double.parseDouble(nodeGraphics.getY()));
				final double w = Double.parseDouble(nodeGraphics.getWidth());
				nodeAttr.setAttribute(nodeID, "KEGG.nodeWidth", w);
				nodeWidth.putMapValue(nodeID, w);
				
				final double h = Double.parseDouble(nodeGraphics.getHeight());
				nodeAttr.setAttribute(nodeID, "KEGG.nodeHeight", h);
				nodeHeight.putMapValue(nodeID, h);
				
				nv.setShape(KEGGShape.getShape(nodeGraphics.getType()));
			}
		}
		
		Cytoscape.getVisualMappingManager().getCalculatorCatalog().addVisualStyle(defStyle);
		Cytoscape.getVisualMappingManager().setVisualStyle(defStyle);
		view.setVisualStyle(defStyle.getName());
		Cytoscape.getVisualMappingManager().setNetworkView(view);
		view.redrawGraph(false, true);
	}

	public int[] getNodeIdx() {
		return nodeIdx;
	}

	public int[] getEdgeIdx() {
		return edgeIdx;
	}

}
