package edu.ucsd.bioeng.idekerlab.ncbiclient.util;

import static cytoscape.visual.VisualPropertyType.EDGE_LABEL;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL;

import java.awt.Color;

import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.LineStyle;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.calculators.EdgeCalculator;
import cytoscape.visual.calculators.NodeCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public class VisualStyleBuilder {

	private VisualStyle defaultVS = null;
	private VisualStyle newVS = null;

	private static final String DEF_VS_NAME = "Entrez Gene Style";
	private static final String NEW_VS_NAME = "Entrez Gene Style 2";
	
	private static VisualStyleBuilder builder = new VisualStyleBuilder();
	
	public static VisualStyle getDefaultStyle() {
		return builder.getDefStyle();
	}
	
	public static VisualStyle getNewVisualStyle() {
		return builder.getNewStyle();
	}

	private VisualStyleBuilder() {
		defaultVS = buidlDefaultStyle();
		newVS = buildNewStyle(); 
	}
	
	public VisualStyle getDefStyle() {
		return defaultVS;
	}
	
	public VisualStyle getNewStyle() {
		return newVS;
	}
	
	
	/**
	 * Generate default visual style. The style is database-dependent.
	 * 
	 * @return default visual style.
	 */
	private VisualStyle buidlDefaultStyle() {
		final Color NODE_COLOR = new Color(25, 25, 200);
		final Color NODE_LABEL_COLOR = new Color(10, 10, 10);

		final Color EDGE_COLOR = new Color(10, 10, 10);
		final Color EDGE_LABEL_COLOR = new Color(50, 50, 255);

		final VisualStyle defStyle = new VisualStyle(DEF_VS_NAME);

		NodeAppearanceCalculator nac = defStyle.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator eac = defStyle.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator gac = defStyle
				.getGlobalAppearanceCalculator();

		gac.setDefaultBackgroundColor(Color.white);

		PassThroughMapping m = new PassThroughMapping("", AbstractCalculator.ID);

		NodeCalculator calc = new NodeCalculator(DEF_VS_NAME + "-"
				+ "NodeLabelMapping", m, null, NODE_LABEL);
		PassThroughMapping me = new PassThroughMapping("", "interaction");

		EdgeCalculator calce = new EdgeCalculator(DEF_VS_NAME + "-"
				+ "EdgeLabelMapping", me, null, EDGE_LABEL);
		nac.setCalculator(calc);

		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,
				NODE_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE,
				NodeShape.ROUND_RECT);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 50);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_OPACITY,
				225);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH, 0);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_WIDTH, 65);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_HEIGHT, 34);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR,
				NODE_LABEL_COLOR);
		nac.setNodeSizeLocked(false);

		eac.setCalculator(calce);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_COLOR,
				EDGE_COLOR);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL_COLOR,
				EDGE_LABEL_COLOR);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_FONT_SIZE, 5);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_OPACITY, 90);
		eac.getDefaultAppearance().set(
				VisualPropertyType.EDGE_SRCARROW_OPACITY, 120);
		eac.getDefaultAppearance().set(
				VisualPropertyType.EDGE_TGTARROW_OPACITY, 120);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL_OPACITY,
				70);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LINE_WIDTH, 3);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL, "");

		// Set edge color based on datasource name
		DiscreteMapping edgeColor = new DiscreteMapping(EDGE_COLOR,
				"datasource", ObjectMapping.EDGE_MAPPING);

		edgeColor.putMapValue("BIND", new Color(Integer.decode("#b0c4de")));
		edgeColor.putMapValue("BioGRID", new Color(Integer.decode("#3cb371")));
		edgeColor.putMapValue("HPRD", new Color(Integer.decode("#800000")));

		DiscreteMapping edgeLineStyle = new DiscreteMapping(LineStyle.SOLID,
				"interaction type", ObjectMapping.EDGE_MAPPING);

		edgeLineStyle.putMapValue("physical", LineStyle.SOLID);
		edgeLineStyle.putMapValue("genetic", LineStyle.LONG_DASH);

		EdgeCalculator edgeColorCalc = new EdgeCalculator(DEF_VS_NAME + "-"
				+ "EdgeColorMapping", edgeColor, null,
				VisualPropertyType.EDGE_COLOR);

		eac.setCalculator(edgeColorCalc);

		EdgeCalculator edgeLineStyleCalc = new EdgeCalculator(DEF_VS_NAME + "-"
				+ "EdgeLineStyleMapping", edgeLineStyle, null,
				VisualPropertyType.EDGE_LINE_STYLE);

		eac.setCalculator(edgeLineStyleCalc);

		return defStyle;
	}

	private VisualStyle buildNewStyle() {
		final Color NODE_COLOR = new Color(0x00, 0x8b, 0x8b);
		final Color NODE_NUCLEOTIDE_COLOR = new Color(25, 25, 200);
		final Color NODE_BORDER_COLOR = new Color(0x00, 0x64, 0x00);
		final Color NODE_NUCLEOTIDE_BORDER_COLOR = new Color(25, 25, 200);
		final Color NODE_LABEL_COLOR = new Color(0x69, 0x69, 0x69);

		final Color EDGE_COLOR = new Color(0x80, 0x80, 0x80);
		final Color EDGE_LABEL_COLOR = new Color(50, 50, 255);

		final VisualStyle newStyle = new VisualStyle(NEW_VS_NAME);

		NodeAppearanceCalculator nac = newStyle.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator eac = newStyle.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator gac = newStyle
				.getGlobalAppearanceCalculator();

		gac.setDefaultBackgroundColor(Color.white);

		PassThroughMapping m = new PassThroughMapping("", AbstractCalculator.ID);

		NodeCalculator calc = new NodeCalculator(NEW_VS_NAME + "-"
				+ "NodeLabelMapping", m, null, NODE_LABEL);
		PassThroughMapping me = new PassThroughMapping("", "interaction");

		EdgeCalculator calce = new EdgeCalculator(NEW_VS_NAME + "-"
				+ "EdgeLabelMapping", me, null, EDGE_LABEL);
		nac.setCalculator(calc);

		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,
				NODE_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR,
				NODE_BORDER_COLOR);
		
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE,
				NodeShape.RECT);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 110);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_OPACITY, 240);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_OPACITY,
				225);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH, 3);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_WIDTH, 100);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_HEIGHT, 38);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR,
				NODE_LABEL_COLOR);
		nac.setNodeSizeLocked(false);

		eac.setCalculator(calce);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_COLOR,
				EDGE_COLOR);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL_COLOR,
				EDGE_LABEL_COLOR);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_FONT_SIZE, 5);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_OPACITY, 90);
		eac.getDefaultAppearance().set(
				VisualPropertyType.EDGE_SRCARROW_OPACITY, 120);
		eac.getDefaultAppearance().set(
				VisualPropertyType.EDGE_TGTARROW_OPACITY, 120);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL_OPACITY,
				70);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LINE_WIDTH, 3);
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_LABEL, "");

		
		// Modify node apeearence based on type
		
		// Color
		DiscreteMapping nodeColor = new DiscreteMapping(NODE_COLOR,
				"Interactor Type", ObjectMapping.NODE_MAPPING);

		nodeColor.putMapValue("GeneID", new Color(Integer.decode("#b0c4de")));
		nodeColor.putMapValue("Protein", new Color(Integer.decode("#3cb371")));
		nodeColor.putMapValue("Nucleotide", new Color(Integer.decode("#800000")));
		
		NodeCalculator nodeColorCalc = new NodeCalculator(NEW_VS_NAME + "-"
				+ "NodeColorMapping", nodeColor, null,
				VisualPropertyType.NODE_FILL_COLOR);

		nac.setCalculator(nodeColorCalc);

		// Shape
		DiscreteMapping nodeShape = new DiscreteMapping(NodeShape.RECT,
				"Interactor Type", ObjectMapping.NODE_MAPPING);

		nodeShape.putMapValue("GeneID", NodeShape.RECT);
		nodeShape.putMapValue("Protein", NodeShape.ELLIPSE);
		nodeShape.putMapValue("Nucleotide", NodeShape.DIAMOND);
		
		NodeCalculator nodeShapeCalc = new NodeCalculator(NEW_VS_NAME + "-"
				+ "NodeShapeMapping", nodeShape, null,
				VisualPropertyType.NODE_SHAPE);

		nac.setCalculator(nodeShapeCalc);
		
		// Set edge color based on datasource name
		DiscreteMapping edgeColor = new DiscreteMapping(EDGE_COLOR,
				"datasource", ObjectMapping.EDGE_MAPPING);

		edgeColor.putMapValue("BIND", new Color(Integer.decode("#b0c4de")));
		edgeColor.putMapValue("BioGRID", new Color(Integer.decode("#3cb371")));
		edgeColor.putMapValue("HPRD", new Color(Integer.decode("#800000")));

		DiscreteMapping edgeLineStyle = new DiscreteMapping(LineStyle.SOLID,
				"interaction type", ObjectMapping.EDGE_MAPPING);

		edgeLineStyle.putMapValue("physical", LineStyle.SOLID);
		edgeLineStyle.putMapValue("genetic", LineStyle.LONG_DASH);

		EdgeCalculator edgeColorCalc = new EdgeCalculator(NEW_VS_NAME + "-"
				+ "EdgeColorMapping", edgeColor, null,
				VisualPropertyType.EDGE_COLOR);

		eac.setCalculator(edgeColorCalc);

		EdgeCalculator edgeLineStyleCalc = new EdgeCalculator(NEW_VS_NAME + "-"
				+ "EdgeLineStyleMapping", edgeLineStyle, null,
				VisualPropertyType.EDGE_LINE_STYLE);

		eac.setCalculator(edgeLineStyleCalc);

		return newStyle;
	}

}
