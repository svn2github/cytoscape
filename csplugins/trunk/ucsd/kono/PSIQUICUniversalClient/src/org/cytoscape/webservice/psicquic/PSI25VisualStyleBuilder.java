package org.cytoscape.webservice.psicquic;

import static cytoscape.visual.VisualPropertyType.NODE_LABEL;
import giny.view.Justification;
import giny.view.Position;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.webservice.psicquic.mapper.Mitab25Mapper;

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
import cytoscape.visual.mappings.PassThroughMapping;
import ding.view.ObjectPositionImpl;

public class PSI25VisualStyleBuilder {

	// Default visual style name
	private static final String DEF_VS_NAME = "PSIMI 25 Style";

	// Top level interaction types
	private static final String[] ITR_TYPE_ROOT_TERMS = { "MI:0208", "MI:0403", "MI:0914" };

	// Presets
	private static final Color NODE_COLOR = new Color(0xee, 0xee, 0xee);
	private static final Color NODE_LABEL_COLOR = new Color(0x36, 0x36, 0x36);

	private static final Color EDGE_COLOR = new Color(0x9c, 0x9c, 0x9c);
	private static final int EDGE_OPACITY = 160;
	private static final Color EDGE_LABEL_COLOR = new Color(0xA2, 0xB5, 0xCD);
	private static final int EDGE_LABEL_OPACITY = 160;
	private static final int EDGE_LABEL_SIZE = 9;

	// Color presets for some model organisms
	private static final Color COLOR_HUMAN = new Color(0x43, 0x6E, 0xEE);

	private static final Color COLOR_MOUSE = new Color(0xEE, 0x76, 0x21);
	private static final Color COLOR_RAT = new Color(0xFF, 0xA5, 0x00);

	private static final Color COLOR_FLY = new Color(0x93, 0x70, 0xDB);

	private static final Color COLOR_WORM = new Color(0x6B, 0x8E, 0x23);

	private static final Color COLOR_YEAST = new Color(0x9C, 0x9C, 0x9C);

	private static final Color COLOR_ECOLI = new Color(0xB0, 0xE2, 0xFF);

	private static final Color COLOR_ARABIDOPSIS = new Color(0xFF, 0xF5, 0xEE);

	private static final Map<String, String> type = new HashMap<String, String>();

	private static VisualStyle defStyle;

	static {
		buildOntologyMap();
		defStyle = defaultVisualStyleBuilder();
	}

	public static VisualStyle getDefVS() {
		return defStyle;
	}

	// Actual design
	private static VisualStyle defaultVisualStyleBuilder() {

		final VisualStyle defStyle = new VisualStyle(DEF_VS_NAME);

		NodeAppearanceCalculator nac = defStyle.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator eac = defStyle.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator gac = defStyle.getGlobalAppearanceCalculator();

		// Default values
		final Color nodeLineColor = new Color(46, 46, 46);
		final Font nodeLabelFont = new Font("Helvetica", Font.PLAIN, 14);

		// Network background
		gac.setDefaultBackgroundColor(Color.white);

		final PassThroughMapping m = new PassThroughMapping(String.class, Mitab25Mapper.PREDICTED_GENE_NAME);
		final Calculator calc = new BasicCalculator(DEF_VS_NAME + "-" + "NodeLabelMapping", m, NODE_LABEL);
		nac.setCalculator(calc);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR, NODE_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE, NodeShape.RECT);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 120);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_OPACITY, 200);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH, 2);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR, nodeLineColor);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_WIDTH, 25);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_HEIGHT, 15);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR, NODE_LABEL_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FONT_FACE, nodeLabelFont);

		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_POSITION,
				new ObjectPositionImpl(Position.SOUTH, Position.NORTH_WEST, Justification.JUSTIFY_CENTER, -7, 1.0));

		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FONT_SIZE, 14);
		nac.setNodeSizeLocked(false);

		// Node Color Mapping based on species name
		DiscreteMapping nodeColorMapping = new DiscreteMapping(Color.class, Mitab25Mapper.SPECIES_ATTR_NAME);
		nodeColorMapping.putMapValue("9606", COLOR_HUMAN);
		nodeColorMapping.putMapValue("10090", COLOR_MOUSE);
		nodeColorMapping.putMapValue("10116", COLOR_RAT);
		nodeColorMapping.putMapValue("111?", COLOR_FLY);
		nodeColorMapping.putMapValue("10116", COLOR_WORM);
		nodeColorMapping.putMapValue("4932", COLOR_YEAST);
		nodeColorMapping.putMapValue("83333", COLOR_ECOLI);
		nodeColorMapping.putMapValue("3702", COLOR_ARABIDOPSIS);

		final Calculator nodeColorCalc = new BasicCalculator(DEF_VS_NAME + "-" + "NodeColorMapping", nodeColorMapping,
				VisualPropertyType.NODE_FILL_COLOR);
		nac.setCalculator(nodeColorCalc);

		DiscreteMapping nodeShapeMapping = new DiscreteMapping(NodeShape.class, Mitab25Mapper.ATTR_PREFIX
				+ "interactor type");
		nodeShapeMapping.putMapValue("compound", NodeShape.ELLIPSE);
		nodeShapeMapping.putMapValue("nested", NodeShape.ROUND_RECT);
		final Calculator nodeShapeCalc = new BasicCalculator(DEF_VS_NAME + "-" + "NodeShapeMapping", nodeShapeMapping,
				VisualPropertyType.NODE_SHAPE);
		nac.setCalculator(nodeShapeCalc);

		DiscreteMapping nodeWidthMapping = new DiscreteMapping(Number.class, Mitab25Mapper.ATTR_PREFIX
				+ "interactor type");
		nodeWidthMapping.putMapValue("compound", 15);
		nodeWidthMapping.putMapValue("nested", 100);
		final Calculator nodeWidthCalc = new BasicCalculator(DEF_VS_NAME + "-" + "NodeWidthMapping", nodeWidthMapping,
				VisualPropertyType.NODE_WIDTH);
		nac.setCalculator(nodeWidthCalc);
		DiscreteMapping nodeHeightMapping = new DiscreteMapping(Number.class, Mitab25Mapper.ATTR_PREFIX
				+ "interactor type");
		nodeHeightMapping.putMapValue("compound", 15);
		nodeHeightMapping.putMapValue("nested", 100);
		final Calculator nodeHeightCalc = new BasicCalculator(DEF_VS_NAME + "-" + "NodeHeightMapping",
				nodeHeightMapping, VisualPropertyType.NODE_HEIGHT);
		nac.setCalculator(nodeHeightCalc);

		final PassThroughMapping edgeLabelMapping = new PassThroughMapping(String.class,
				Mitab25Mapper.INTERACTION_TYPE_ATTR_NAME);
		final Calculator edgeCalc = new BasicCalculator(DEF_VS_NAME + "-" + "EdgeLabelMapping", edgeLabelMapping,
				VisualPropertyType.EDGE_LABEL);
		eac.setCalculator(edgeCalc);

		// eac.setCalculator(calce);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_COLOR, EDGE_COLOR);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL_COLOR, EDGE_LABEL_COLOR);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_FONT_SIZE, EDGE_LABEL_SIZE);

		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_OPACITY, EDGE_OPACITY);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_SRCARROW_OPACITY, EDGE_OPACITY);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_TGTARROW_OPACITY, EDGE_OPACITY);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL_OPACITY, EDGE_LABEL_OPACITY);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LINE_WIDTH, 1);

		// Interaction Type mapping
		DiscreteMapping lineStyle = new DiscreteMapping(LineStyle.class, Mitab25Mapper.ATTR_PREFIX + "interaction type");
		DiscreteMapping lineWidth = new DiscreteMapping(Number.class, Mitab25Mapper.ATTR_PREFIX + "interaction type");
		DiscreteMapping edgeColorMap = new DiscreteMapping(Color.class, Mitab25Mapper.ATTR_PREFIX + "interaction type");
		generateInteractionTypeMap(lineStyle, lineWidth, edgeColorMap);

		final Calculator lineStyleCalc = new BasicCalculator(DEF_VS_NAME + "-" + "EdgeLineStyleMapping", lineStyle,
				VisualPropertyType.EDGE_LINE_STYLE);

		final Calculator lineWidthCalc = new BasicCalculator(DEF_VS_NAME + "-" + "EdgeLineWidthMapping", lineWidth,
				VisualPropertyType.EDGE_LINE_WIDTH);

		final Calculator edgeColorCalc = new BasicCalculator(DEF_VS_NAME + "-" + "EdgeColorMapping", edgeColorMap,
				VisualPropertyType.EDGE_COLOR);

		//
		// DiscreteMapping sourceShape = new DiscreteMapping(ArrowShape.NONE,
		// "source experimental role", ObjectMapping.EDGE_MAPPING);
		//
		// sourceShape.putMapValue("bait", ArrowShape.DIAMOND);
		// sourceShape.putMapValue("prey", ArrowShape.CIRCLE);
		//
		// EdgeCalculator sourceShapeCalc = new EdgeCalculator(DEF_VS_NAME + "-"
		// + "EdgeSourceArrowShapeMapping", sourceShape, null,
		// VisualPropertyType.EDGE_SRCARROW_SHAPE);
		//
		// DiscreteMapping targetColor = new DiscreteMapping(Color.black,
		// "target experimental role", ObjectMapping.EDGE_MAPPING);
		//
		// targetColor.putMapValue("bait", Color.red);
		// targetColor.putMapValue("prey", Color.red);
		//
		// EdgeCalculator targetColorCalc = new EdgeCalculator(DEF_VS_NAME + "-"
		// + "EdgeTargetArrowColorMapping", targetColor, null,
		// VisualPropertyType.EDGE_TGTARROW_COLOR);
		//
		// DiscreteMapping sourceColor = new DiscreteMapping(Color.black,
		// "source experimental role", ObjectMapping.EDGE_MAPPING);
		//
		// sourceColor.putMapValue("bait", Color.red);
		// sourceColor.putMapValue("prey", Color.red);
		//
		// EdgeCalculator sourceColorCalc = new EdgeCalculator(DEF_VS_NAME + "-"
		// + "EdgeSourceArrowColorMapping", targetColor, null,
		// VisualPropertyType.EDGE_SRCARROW_COLOR);
		//
		eac.setCalculator(lineStyleCalc);
		eac.setCalculator(lineWidthCalc);
		eac.setCalculator(edgeColorCalc);
		// eac.setCalculator(targetColorCalc);

		return defStyle;
	}

	private static void generateInteractionTypeMap(DiscreteMapping lineStyle, DiscreteMapping lineWidth,
			DiscreteMapping edgeColor) {
		// TODO Auto-generated method stub
		// for (String childTerm : type.keySet()) {
		// if ((type.get(childTerm)).equals("MI:0208")) {
		// lineStyle.putMapValue(childTerm, LineStyle.LONG_DASH);
		// lineWidth.putMapValue(childTerm, 2.0);
		// edgeColor.putMapValue(childTerm, Color.CYAN);
		// } else if ((type.get(childTerm)).equals("MI:0403")) {
		// lineStyle.putMapValue(childTerm, LineStyle.SOLID);
		// lineWidth.putMapValue(childTerm, 2.0);
		// edgeColor.putMapValue(childTerm, Color.green);
		// } else if ((type.get(childTerm)).equals("MI:0914")) {
		// lineStyle.putMapValue(childTerm, LineStyle.SOLID);
		// lineWidth.putMapValue(childTerm, 3.0);
		// edgeColor.putMapValue(childTerm, Color.DARK_GRAY);
		// }
		// }

		lineStyle.putMapValue("MI:0208", LineStyle.LONG_DASH);
		lineWidth.putMapValue("MI:0208", 3.0);
		edgeColor.putMapValue("MI:0208", Color.CYAN);

		lineStyle.putMapValue("MI:0403", LineStyle.SOLID);
		lineWidth.putMapValue("MI:0403", 1.0);
		edgeColor.putMapValue("MI:0403", Color.green);

		lineStyle.putMapValue("MI:0914", LineStyle.SOLID);
		lineWidth.putMapValue("MI:0914", 3.0);
		edgeColor.putMapValue("MI:0914", Color.DARK_GRAY);
	}

	private static void buildOntologyMap() {

		// try {
		// System.out.println("############Ontology Test=============");
		//
		// // Get child terms for each interaction type category.
		// for (String rootTerm : ITR_TYPE_ROOT_TERMS) {
		// final Map<String, String> children = OLSUtil
		// .getAllChildren(rootTerm);
		// for (String childTerm : children.keySet()) {
		// type.put(childTerm, rootTerm);
		// System.out.println(childTerm + ", root = " + rootTerm);
		// }
		// }
		//
		// System.out.println("############Ontology Test DONE!=============");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

}
