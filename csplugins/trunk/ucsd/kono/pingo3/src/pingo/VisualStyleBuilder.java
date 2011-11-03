package pingo;

/**
 * * Copyright (c) 2010 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere
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
 * * Date: Jul.27.2010
 * * Description: PiNGO is a Cytoscape plugin that leverages functional enrichment
 * * analysis to discover lead genes from biological networks.          
 **/

import java.awt.Color;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.RichVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;

/**
 * *****************************************************************
 * TheVisualStyle.java Steven Maere (c) 2005-2010
 * 
 * <p>
 * Class that defines the PiNGO visual style used in the Cytoscape visualization
 * of PiNGO results.
 * </p>
 * 
 * Updated on June, 2010 for Cytoscape 2.7.0+ by Keiichiro Ono
 * 
 * ******************************************************************
 */
public class VisualStyleBuilder {

	private static final Color NADA = Color.white;
	private static final Color COL_MIN = new Color(255, 255, 0);
	private static final Color COL_MAX = new Color(255, 127, 0);

	private static final Double DEF_NODE_SIZE = 100d;

	// Name of analyzed network
	private String networkName;

	private double alpha;
	private final String pingoVSName;
	private final String NODE_COLOR;
	private final String NODE_LABEL;
	private final String NODE_SIZE;
	private final String EDGE_COLOR;
	
	private final CyPluginAdapter adapter;

	public VisualStyleBuilder(final CyPluginAdapter adapter, final String networkName, double alpha) {
		this.adapter = adapter;
		this.networkName = networkName;
		this.alpha = alpha;
		this.pingoVSName = "PiNGO Style for " + networkName;
		this.NODE_COLOR = "nodeFillColor_" + networkName;
		this.NODE_LABEL = "description_" + networkName;
		this.NODE_SIZE = "nodeSize_" + networkName;
		this.EDGE_COLOR = "edgeType_" + networkName;
	}

	private void adaptVisualStyle(VisualStyle style, CyNetwork network) {

		// Node default appearance definitions
		style.setDefaultValue(RichVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE);
		style.setDefaultValue(RichVisualLexicon.NODE_LABEL_FONT_SIZE, 14);
		style.setDefaultValue(RichVisualLexicon.NODE_WIDTH, 40d);
		style.setDefaultValue(RichVisualLexicon.NODE_HEIGHT, 40d);
		style.setDefaultValue(RichVisualLexicon.NODE_BORDER_WIDTH, 2d);
		style.setDefaultValue(RichVisualLexicon.NODE_BORDER_PAINT, Color.DARK_GRAY);
		style.setDefaultValue(RichVisualLexicon.NODE_TRANSPARENCY, 210);
		
		// Edge default appearence definitions
		// FIXME
//		style.getEdgeAppearanceCalculator().getDefaultAppearance()
//				.set(VisualPropertyType.EDGE_TGTARROW_SHAPE, ArrowShape.NONE);
//		style.getEdgeAppearanceCalculator().getDefaultAppearance()
//				.set(VisualPropertyType.EDGE_TGTARROW_COLOR, Color.LIGHT_GRAY);
//		style.getEdgeAppearanceCalculator().getDefaultAppearance()
//				.set(VisualPropertyType.EDGE_SRCARROW_SHAPE, ArrowShape.NONE);
//		style.getEdgeAppearanceCalculator().getDefaultAppearance()
		
		style.setDefaultValue(RichVisualLexicon.EDGE_LINE_TYPE, LineTypeVisualProperty.SOLID);
		style.setDefaultValue(RichVisualLexicon.EDGE_WIDTH, 4d);
		style.setDefaultValue(RichVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, Color.DARK_GRAY);
		
		style.setDefaultValue(RichVisualLexicon.NETWORK_BACKGROUND_PAINT, Color.WHITE);
		
		// Display NODE_LABEL as a label
		final VisualMappingFunctionFactory passthroughF = adapter.getVisualMappingFunctionPassthroughFactory();
		final VisualMappingFunction<String, String> labelMapping = passthroughF.createVisualMappingFunction(NODE_LABEL,
				String.class, RichVisualLexicon.NODE_LABEL);
		style.addVisualMappingFunction(labelMapping);

//		// Gradient node color mapping
//		// TODO: Replace when 2.8 released.
//		// final ContinuousMapping<Double, Color> colorMapping = new
//		// ContinuousMapping<Double, Color>(
//		// Color.class, NODE_COLOR);
//		final ContinuousMapping colorMapping = new ContinuousMapping(NADA, ObjectMapping.NODE_MAPPING);
//		colorMapping.setControllingAttributeName(NODE_COLOR, network, false);
//		// TODO: Add generics parameter when 2.8 released.
//		final BoundaryRangeValues colbrVal1 = new BoundaryRangeValues();
//		double cols = -(Math.log(alpha) / Math.log(10));
//		colbrVal1.lesserValue = NADA;
//		colbrVal1.equalValue = COL_MIN;
//		colbrVal1.greaterValue = COL_MIN;
//		colorMapping.addPoint(cols, colbrVal1);
//
//		// TODO: Add generics parameter when 2.8 released.
//		final BoundaryRangeValues colbrVal2 = new BoundaryRangeValues();
//		cols = -(Math.log(alpha) / Math.log(10)) + 5.0;
//		colbrVal2.lesserValue = COL_MAX;
//		colbrVal2.equalValue = COL_MAX;
//		colbrVal2.greaterValue = COL_MAX;
//		colorMapping.addPoint(cols, colbrVal2);
//
//		final Calculator colorCalculator = new BasicCalculator("Pingo Node Color_" + networkName, colorMapping,
//				VisualPropertyType.NODE_FILL_COLOR);
//		style.getNodeAppearanceCalculator().setCalculator(colorCalculator);
//
//		// Node Size Mapping
//		// TODO: Update when 2.8 released.
//		final ContinuousMapping wMapping = new ContinuousMapping(DEF_NODE_SIZE, ObjectMapping.NODE_MAPPING);
//		wMapping.setControllingAttributeName(NODE_SIZE, network, false);
//		final ContinuousMapping hMapping = new ContinuousMapping(DEF_NODE_SIZE, ObjectMapping.NODE_MAPPING);
//		hMapping.setControllingAttributeName(NODE_SIZE, network, false);
//
//		// The following code defines the range of values
//
//		BoundaryRangeValues brVals;
//		int j;
//		for (j = 0; j <= 1; j++) {
//			brVals = new BoundaryRangeValues();
//			final double size = 380d * j + 20d;
//			final double s = 99 * j + 1;
//			brVals.lesserValue = size;
//			brVals.equalValue = size;
//			brVals.greaterValue = size;
//			wMapping.addPoint(s, brVals);
//			hMapping.addPoint(s, brVals);
//		}
//
//		final Calculator nodeSizeCalculator = new BasicCalculator("Pingo Node Size_" + networkName, wMapping,
//				VisualPropertyType.NODE_SIZE);
//		style.getNodeAppearanceCalculator().setCalculator(nodeSizeCalculator);
	}

	public VisualStyle createVisualStyle(final CyNetwork network) {
		final VisualStyleFactory vsFactory = adapter.getVisualStyleFactory();
		final VisualStyle visualStyle = vsFactory.getInstance(pingoVSName);
		adaptVisualStyle(visualStyle, network);
		return visualStyle;
	}
}
