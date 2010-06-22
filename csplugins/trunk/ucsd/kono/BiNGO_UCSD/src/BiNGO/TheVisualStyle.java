package BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere
 * * Date: Mar.25.2005
 * * Description: Class that defines the BiNGO visual style used in the Cytoscape visualization of BiNGO results.     
 **/

import java.awt.Color;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.LineStyle;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.VisualPropertyDependency.Definition;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.PassThroughMapping;

/**
 * *****************************************************************
 * HelpMenuBar.java Steven Maere (c) March 2005
 * 
 * <p>
 * Class that defines the BiNGO visual style used in the Cytoscape visualization
 * of BiNGO results.
 * </p>
 * 
 * Updated on June, 2010 for Cytoscape 2.7.0+ by Keiichiro Ono
 * 
 * ******************************************************************
 */
public class TheVisualStyle {

	private static final Color NADA = Color.white;
	private static final Color COL_MIN = new Color(255, 255, 0);
	private static final Color COL_MAX = new Color(255, 127, 0);

	// Name of analyzed network
	private String networkName;

	private double alpha;
	private final String bingoVSName;
	private final String NODE_COLOR;
	private final String NODE_LABEL;
	private final String NODE_SIZE;

	public TheVisualStyle(final String networkName, double alpha) {
		this.networkName = networkName;
		this.alpha = alpha;
		this.bingoVSName = "BiNGO Style for " + networkName;
		this.NODE_COLOR = "nodeFillColor_" + networkName;
		this.NODE_LABEL = "description_" + networkName;
		this.NODE_SIZE = "nodeSize_" + networkName;
	}

	public void adaptVisualStyle(VisualStyle style, CyNetwork network) {

		// Node default appearence definitions
		style.getNodeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE);
		style.getDependency().set(Definition.NODE_SIZE_LOCKED, true);
		style.getNodeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.NODE_FONT_SIZE, 10);
		style.getNodeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.NODE_LINE_WIDTH, 3);
		style.getNodeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.NODE_BORDER_COLOR, Color.DARK_GRAY);
		style.getNodeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.NODE_OPACITY, 210);
		style.getNodeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.NODE_BORDER_OPACITY, 120);

		// Edge default appearence definitions
		style.getEdgeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.EDGE_TGTARROW_SHAPE, ArrowShape.DELTA);
		style.getEdgeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.EDGE_TGTARROW_COLOR, Color.black);
		style.getEdgeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.EDGE_SRCARROW_SHAPE, ArrowShape.NONE);
		style.getEdgeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.EDGE_LINE_STYLE, LineStyle.SOLID);
		style.getEdgeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.EDGE_LINE_WIDTH, 4.0f);
		style.getEdgeAppearanceCalculator().getDefaultAppearance().set(
				VisualPropertyType.EDGE_COLOR, Color.DARK_GRAY);
		style.getGlobalAppearanceCalculator().setDefaultBackgroundColor(
				Color.white);

		// Display NODE_LABEL as a label
		final PassThroughMapping<String, String> m = new PassThroughMapping<String, String>(
				String.class, NODE_LABEL);
		final Calculator nlc = new BasicCalculator("Node Description_"
				+ networkName, m, VisualPropertyType.NODE_LABEL);
		style.getNodeAppearanceCalculator().setCalculator(nlc);

		// Gradient node color mapping
		final ContinuousMapping<Double, Color> colorMapping = new ContinuousMapping<Double, Color>(
				Color.class, NODE_COLOR);
		final BoundaryRangeValues<Color> colbrVal1 = new BoundaryRangeValues<Color>();
		double cols = -(Math.log(alpha) / Math.log(10));
		colbrVal1.lesserValue = NADA;
		colbrVal1.equalValue = COL_MIN;
		colbrVal1.greaterValue = COL_MIN;
		colorMapping.addPoint(cols, colbrVal1);

		final BoundaryRangeValues<Color> colbrVal2 = new BoundaryRangeValues<Color>();
		cols = -(Math.log(alpha) / Math.log(10)) + 5.0;
		colbrVal2.lesserValue = COL_MAX;
		colbrVal2.equalValue = COL_MAX;
		colbrVal2.greaterValue = COL_MAX;
		colorMapping.addPoint(cols, colbrVal2);

		final Calculator colorCalculator = new BasicCalculator(
				"Bingo Node Color_" + networkName, colorMapping,
				VisualPropertyType.NODE_FILL_COLOR);
		style.getNodeAppearanceCalculator().setCalculator(colorCalculator);

		// Node Size Mapping
		final ContinuousMapping<Double, Double> wMapping = new ContinuousMapping<Double, Double>(
				Double.class, NODE_SIZE);
		final ContinuousMapping<Double, Double> hMapping = new ContinuousMapping<Double, Double>(
				Double.class, NODE_SIZE);

		// The following code defines the range of values

		BoundaryRangeValues<Double> brVals;
		int j;
		for (j = 0; j <= 1; j++) {
			brVals = new BoundaryRangeValues<Double>();
			final double size = 380d * j + 20d;
			final double s = 99 * j + 1;
			brVals.lesserValue = size;
			brVals.equalValue = size;
			brVals.greaterValue = size;
			wMapping.addPoint(s, brVals);
			hMapping.addPoint(s, brVals);
		}

		final Calculator nodeSizeCalculator = new BasicCalculator(
				"Bingo Node Size_" + networkName, wMapping,
				VisualPropertyType.NODE_SIZE);
		style.getNodeAppearanceCalculator().setCalculator(nodeSizeCalculator);
	}

	public VisualStyle createVisualStyle(CyNetwork network) {

		final VisualMappingManager vmm = Cytoscape.getVisualMappingManager();

		// gets the currently active visual style
		final VisualStyle currentStyle = vmm.getVisualStyle();

		// methods to access the node, edge, and global appearance calculators
		final NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator(
				currentStyle.getNodeAppearanceCalculator());
		final EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator(
				currentStyle.getEdgeAppearanceCalculator());
		final GlobalAppearanceCalculator globalAppCalc = new GlobalAppearanceCalculator(
				currentStyle.getGlobalAppearanceCalculator());

		// create the visual style
		final VisualStyle visualStyle = new VisualStyle(bingoVSName, nodeAppCalc,
				edgeAppCalc, globalAppCalc);

		// update with BiNGO specific style
		adaptVisualStyle(visualStyle, network);

		return visualStyle;
	}
}
