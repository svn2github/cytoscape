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

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
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

	private enum KEGGShape {
		CIRCLE("circle", NodeShape.ELLIPSE), RECTANGLE("rectangle",
				NodeShape.RECT), ROUND_RECTANGLE("roundrectangle",
				NodeShape.ROUND_RECT), LINE("line", null);

		private String tag;
		private NodeShape shape;

		private KEGGShape(final String tag, final NodeShape shape) {
			this.shape = shape;
			this.tag = tag;
		}

		public static int getShape(final String shape) {
			for (KEGGShape keggShape : KEGGShape.values()) {
				if (keggShape.tag.equals(shape)) {
					if (keggShape.shape == null)
						return -1;
					else
						return keggShape.shape.getGinyShape();
				}
			}

			return NodeShape.RECT.getGinyShape();
		}
	}

	private final Pathway pathway;
	private final String pathwayName;

	private int[] nodeIdx;
	private int[] edgeIdx;

	public PathwayMapper(final Pathway pathway) {
		this.pathway = pathway;
		this.pathwayName = pathway.getName();
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

		for (final Entry comp : components) {
			if (!comp.getGraphics().getType().equals(KEGGShape.LINE.tag)) {
				CyNode node = Cytoscape.getCyNode(pathwayID + "-"
						+ comp.getId(), true);
				nodeAttr.setAttribute(node.getIdentifier(), "KEGG.name", comp
						.getName());
				nodeMap.put(comp.getId(), node);
				entryMap.put(comp.getId(), comp);
			}
		}

		nodeIdx = new int[nodeMap.values().size()];
		int idx = 0;
		for (CyNode node : nodeMap.values()) {
			nodeIdx[idx] = node.getRootGraphIndex();
			idx++;
		}
	}

	protected void updateView(final CyNetwork network) {

		final String vsName = "KEGG: " + pathway.getTitle() + "(" + pathwayName + ")";
		final VisualStyle defStyle = new VisualStyle(vsName);

		NodeAppearanceCalculator nac = defStyle.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator eac = defStyle.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator gac = defStyle
				.getGlobalAppearanceCalculator();

		// Default values
		final Color nodeColor = new Color(200,200,200);
		final Color nodeLineColor = new Color(20, 20, 20);
		final Color nodeLabelColor = new Color(30, 30, 30);

		final Font nodeLabelFont = new Font("SansSerif.BOLD", 12, Font.BOLD);

		gac.setDefaultBackgroundColor(Color.white);

		final PassThroughMapping m = new PassThroughMapping("", "KEGG.name");

		final Calculator nodeLabelMappingCalc = new BasicCalculator(vsName + "-"
				+ "NodeLabelMapping", m, VisualPropertyType.NODE_LABEL);

		nac.setCalculator(nodeLabelMappingCalc);
		
		nac.setNodeSizeLocked(false);
		
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,
				nodeColor);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE,
				NodeShape.ELLIPSE);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_OPACITY,
				220);

		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR,
				nodeLineColor);

		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR,
				nodeLabelColor);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FONT_FACE,
				nodeLabelFont);
		
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

}
