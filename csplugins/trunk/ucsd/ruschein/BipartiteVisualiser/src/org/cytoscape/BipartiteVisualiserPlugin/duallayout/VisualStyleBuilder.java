package org.cytoscape.BipartiteVisualiserPlugin.duallayout;

import static cytoscape.visual.VisualPropertyType.EDGE_LABEL;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL;
import giny.view.Label;

import java.awt.Color;

import cytoscape.Cytoscape;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.LabelPosition;
import cytoscape.visual.LineStyle;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public class VisualStyleBuilder {

	// This is a Singleton.
	private static VisualStyleBuilder builder = new VisualStyleBuilder();

	public static VisualStyle getVisualStyle(final String vsName) {
		return builder.getStyle(vsName);
	}

	private VisualStyleBuilder() {
	}

	public VisualStyle getStyle(final String vsName) {
		final VisualStyle newStyle = buildStyle(vsName);
		Cytoscape.getVisualMappingManager().getCalculatorCatalog()
				.addVisualStyle(newStyle);
		return newStyle;
	}

	/**
	 * Generate default visual style. The style is database-dependent.
	 * 
	 * @return default visual style.
	 */
	private VisualStyle buildStyle(final String vsName) {
		final Color NODE_COLOR = new Color(25, 25, 200);
		final Color NODE_BORDER_COLOR = new Color(25, 25, 25);
		final Color NODE_LABEL_COLOR = new Color(10, 10, 10);

		final Color EDGE_COLOR = new Color(10, 10, 10);
		final Color EDGE_LABEL_COLOR = new Color(50, 50, 255);

		final LabelPosition POSITION = new LabelPosition(Label.SOUTH,
				Label.NORTH, Label.JUSTIFY_CENTER, 0.0, 4.0);

		final VisualStyle defStyle = new VisualStyle(vsName);

		NodeAppearanceCalculator nac = defStyle.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator eac = defStyle.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator gac = defStyle
				.getGlobalAppearanceCalculator();

		gac.setDefaultBackgroundColor(Color.white);

		PassThroughMapping labelMapping = new PassThroughMapping("", AbstractCalculator.ID);

		final Calculator calc = new BasicCalculator(vsName + "-"
				+ "NodeLabelMapping", labelMapping, NODE_LABEL);

		PassThroughMapping me = new PassThroughMapping("", "interaction");

		final Calculator calce = new BasicCalculator(vsName + "-"
				+ "EdgeLabelMapping", me, EDGE_LABEL);
		nac.setCalculator(calc);

		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,
				NODE_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 0);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE,
				NodeShape.ROUND_RECT);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_OPACITY,
				225);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_POSITION,
				POSITION);

		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH, 4);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR,
				NODE_BORDER_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SIZE, 65);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR,
				NODE_LABEL_COLOR);

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


		final DiscreteMapping edgeLineStyle = new DiscreteMapping(LineStyle.SOLID,
				vsName, ObjectMapping.EDGE_MAPPING);

		edgeLineStyle.putMapValue("module-module", LineStyle.LONG_DASH);

		Calculator edgeLineStyleCalc = new BasicCalculator(vsName + "-"
				+ "EdgeLineStyleMapping", edgeLineStyle,
				VisualPropertyType.EDGE_LINE_STYLE);

		eac.setCalculator(edgeLineStyleCalc);
		
		final DiscreteMapping edgeColor = new DiscreteMapping(Color.black,
				vsName, ObjectMapping.EDGE_MAPPING);

		edgeColor.putMapValue("module-module", Color.GREEN);

		Calculator edgeColorCalc = new BasicCalculator(vsName + "-"
				+ "EdgeColorMapping", edgeColor,
				VisualPropertyType.EDGE_COLOR);

		eac.setCalculator(edgeColorCalc);

		// Edge continuous mappings

		return defStyle;
	}
}
