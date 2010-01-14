package org.cytoscape.DenovoPGNetworkAlignmentPlugin;

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
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public class VisualStyleBuilder {

	private static final double DEF_EDGE_CUTOFF = 20;
	
	private VisualStyle overviewVS = null;
	
	private Double edgeCutoff;

	private static final String OVERVIEW_VS_NAME = "Complex Overview Style";
	private static final String MODULE_VS_NAME = "Denovo Module Style";

	// This is a Singleton.
	private static VisualStyleBuilder builder = new VisualStyleBuilder();

	public static VisualStyle getVisualStyle() {
		return builder.getOverviewStyle();
	}

	private VisualStyleBuilder() {
		edgeCutoff = DEF_EDGE_CUTOFF;
		overviewVS = buidlOverviewStyle();
		Cytoscape.getVisualMappingManager().getCalculatorCatalog()
				.addVisualStyle(overviewVS);
	}

	public VisualStyle getOverviewStyle() {
		return overviewVS;
	}

	/**
	 * Generate default visual style. The style is database-dependent.
	 * 
	 * @return default visual style.
	 */
	private VisualStyle buidlOverviewStyle() {
		final Color NODE_COLOR = new Color(25, 25, 200);
		final Color NODE_BORDER_COLOR = new Color(25, 25, 25);
		final Color NODE_LABEL_COLOR = new Color(10, 10, 10);

		final Color EDGE_COLOR = new Color(10, 10, 10);
		final Color EDGE_LABEL_COLOR = new Color(50, 50, 255);
		
		final LabelPosition POSITION = new LabelPosition(Label.SOUTH, Label.NORTH, Label.JUSTIFY_CENTER, 0.0, 4.0);

		final VisualStyle defStyle = new VisualStyle(OVERVIEW_VS_NAME);

		NodeAppearanceCalculator nac = defStyle.getNodeAppearanceCalculator();
		EdgeAppearanceCalculator eac = defStyle.getEdgeAppearanceCalculator();
		GlobalAppearanceCalculator gac = defStyle
				.getGlobalAppearanceCalculator();

		gac.setDefaultBackgroundColor(Color.white);

		PassThroughMapping labelMapping = new PassThroughMapping("", AbstractCalculator.ID);
		PassThroughMapping sizeMapping = new PassThroughMapping(65, NestedNetworkCreator.NODE_SIZE);

		final Calculator calc = new BasicCalculator(OVERVIEW_VS_NAME + "-"
				+ "NodeLabelMapping", labelMapping, NODE_LABEL);
		
		final Calculator sizeCalc = new BasicCalculator(OVERVIEW_VS_NAME + "-"
				+ "NodeSizeMapping", sizeMapping, VisualPropertyType.NODE_SIZE);
		nac.setCalculator(sizeCalc);
		
		PassThroughMapping me = new PassThroughMapping("", "interaction");

		final Calculator calce = new BasicCalculator(OVERVIEW_VS_NAME + "-"
				+ "EdgeLabelMapping", me, EDGE_LABEL);
		nac.setCalculator(calc);

		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,
				NODE_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_OPACITY, 0);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE,
				NodeShape.ROUND_RECT);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_OPACITY,
				225);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_POSITION, POSITION);
		
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH, 4);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR, NODE_BORDER_COLOR);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SIZE, 65);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR,
				NODE_LABEL_COLOR);
		nac.setNodeSizeLocked(true);

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

		DiscreteMapping edgeLineStyle = new DiscreteMapping(LineStyle.SOLID,
				"interaction type", ObjectMapping.EDGE_MAPPING);

		edgeLineStyle.putMapValue("physical", LineStyle.SOLID);
		edgeLineStyle.putMapValue("genetic", LineStyle.LONG_DASH);


		Calculator edgeLineStyleCalc = new BasicCalculator(OVERVIEW_VS_NAME
				+ "-" + "EdgeLineStyleMapping", edgeLineStyle,
				VisualPropertyType.EDGE_LINE_STYLE);

		eac.setCalculator(edgeLineStyleCalc);
		
		
		// Edge continuous mappings
		
		// 1. Edge opacity
		final ContinuousMapping edgeOpacityMapping = new ContinuousMapping(edgeCutoff, ObjectMapping.EDGE_MAPPING);
		final BoundaryRangeValues range1 = new BoundaryRangeValues(20.0, 20.0, 255.0);
		edgeOpacityMapping.setControllingAttributeName(NestedNetworkCreator.EDGE_SCORE, null, false);
		edgeOpacityMapping.addPoint(DEF_EDGE_CUTOFF, range1);
		Calculator edgeOpacityCalc = new BasicCalculator(OVERVIEW_VS_NAME
				+ "-" + "EdgeOpacityMapping", edgeOpacityMapping,
				VisualPropertyType.EDGE_OPACITY);

		eac.setCalculator(edgeOpacityCalc);
		
		// 2. Edge width
		final ContinuousMapping edgeWidthMapping = new ContinuousMapping(edgeCutoff, ObjectMapping.EDGE_MAPPING);
		final BoundaryRangeValues edgeWidthRange1 = new BoundaryRangeValues(2.0, 2.0, 4.0);
		final BoundaryRangeValues edgeWidthRange2 = new BoundaryRangeValues(20.0, 20.0, 20.0);
		edgeWidthMapping.setControllingAttributeName(NestedNetworkCreator.EDGE_SCORE, null, false);
		edgeWidthMapping.addPoint(DEF_EDGE_CUTOFF, edgeWidthRange1);
		edgeWidthMapping.addPoint(DEF_EDGE_CUTOFF+100, edgeWidthRange2);
		Calculator edgeWidthCalc = new BasicCalculator(OVERVIEW_VS_NAME
				+ "-" + "EdgeWidthMapping", edgeWidthMapping,
				VisualPropertyType.EDGE_LINE_WIDTH);

		eac.setCalculator(edgeWidthCalc);

		return defStyle;
	}
}