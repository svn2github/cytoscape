package bingo.internal;

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
 * * Description: Class that defines the bingo visual style used in the Cytoscape visualization of bingo results.     
 **/

import java.awt.Color;
import java.awt.Paint;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.LineTypeVisualProperty;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.values.ArrowShape;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.view.vizmap.VisualPropertyDependency;
import java.util.Set;

/**
 * *****************************************************************
 * HelpMenuBar.java Steven Maere (c) March 2005
 * 
 * <p>
 * Class that defines the bingo visual style used in the Cytoscape visualization
 * of bingo results.
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
	
	private static final Double DEF_NODE_SIZE = 50d;

	// Name of analyzed network
	private String networkName;

	private double alpha;
	private final String bingoVSName;
	private final String NODE_COLOR;
	private final String NODE_LABEL;
	private final String NODE_SIZE;
	private final String EDGE_COLOR;
	
	private final CySwingAppAdapter adapter;

	public TheVisualStyle(final CySwingAppAdapter adapter, final String networkName, double alpha) {
		this.adapter = adapter;
		this.networkName = networkName;
		this.alpha = alpha;
		this.bingoVSName = "bingo Style for " + networkName;
		this.NODE_COLOR = "nodeFillColor_" + networkName;
		this.NODE_LABEL = "description_" + networkName;
		this.NODE_SIZE = "nodeSize_" + networkName;
		this.EDGE_COLOR = "edgeType_" + networkName;
	}

	public void adaptVisualStyle(final VisualStyle style, final CyNetwork network) {

		// Node default appearence definitions
		style.setDefaultValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE);
		//FIXME
//		style.getDependency().set(Definition.NODE_SIZE_LOCKED, true);
		
		style.setDefaultValue(BasicVisualLexicon.NODE_LABEL_FONT_SIZE, 11);
		style.setDefaultValue(BasicVisualLexicon.NODE_WIDTH, 40d);
		style.setDefaultValue(BasicVisualLexicon.NODE_HEIGHT, 40d);
		style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_WIDTH, 2d);
		style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_PAINT, Color.DARK_GRAY);
		style.setDefaultValue(BasicVisualLexicon.NODE_TRANSPARENCY, 210);
		//FIXME
//		style.getNodeAppearanceCalculator().getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_OPACITY, 120);
		
		style.setDefaultValue(BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE, ArrowShapeVisualProperty.DELTA);
		style.setDefaultValue(BasicVisualLexicon.EDGE_SOURCE_ARROW_SHAPE, ArrowShapeVisualProperty.NONE);
		
		style.setDefaultValue(BasicVisualLexicon.EDGE_LINE_TYPE, LineTypeVisualProperty.SOLID);
		style.setDefaultValue(BasicVisualLexicon.EDGE_WIDTH, 2d);
		style.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, Color.DARK_GRAY);
		style.setDefaultValue(BasicVisualLexicon.EDGE_TRANSPARENCY, 190);
		
		style.setDefaultValue(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT, Color.WHITE);

		// Display NODE_LABEL as a label
		final VisualMappingFunctionFactory passthroughF = adapter.getVisualMappingFunctionPassthroughFactory();
		final VisualMappingFunction<String, String> labelMapping = passthroughF.createVisualMappingFunction(
			NODE_LABEL, String.class, BasicVisualLexicon.NODE_LABEL);
		style.addVisualMappingFunction(labelMapping);

		// Gradient node color mapping
		final VisualMappingFunctionFactory continuousF = adapter.getVisualMappingFunctionContinuousFactory();
		final VisualMappingFunction<Double, Paint> nodeColorMapping = continuousF.createVisualMappingFunction(
				NODE_COLOR, Double.class, BasicVisualLexicon.NODE_FILL_COLOR);
		style.addVisualMappingFunction(nodeColorMapping);

		double cols1 = -(Math.log(alpha) / Math.log(10));
		final BoundaryRangeValues<Paint> colbrVal1 = new BoundaryRangeValues<Paint>(NADA, COL_MIN, COL_MIN);
		
		double cols2 = -(Math.log(alpha) / Math.log(10)) + 5.0;
		final BoundaryRangeValues<Paint> colbrVal2 = new BoundaryRangeValues<Paint>(COL_MAX, COL_MAX, COL_MAX);
		
		if (nodeColorMapping instanceof ContinuousMapping) {
			((ContinuousMapping<Double, Paint>) nodeColorMapping).addPoint(cols1, colbrVal1);
			((ContinuousMapping<Double, Paint>) nodeColorMapping).addPoint(cols2, colbrVal2);
		}

		// Node Size Mapping
		final ContinuousMapping<Double, Double> nodeWidthMapping = (ContinuousMapping<Double, Double>) continuousF.createVisualMappingFunction(NODE_SIZE, Double.class, BasicVisualLexicon.NODE_WIDTH);
		style.addVisualMappingFunction(nodeWidthMapping);
		final ContinuousMapping<Double, Double> nodeHeightMapping = (ContinuousMapping<Double, Double>) continuousF.createVisualMappingFunction(NODE_SIZE, Double.class, BasicVisualLexicon.NODE_HEIGHT);
		style.addVisualMappingFunction(nodeHeightMapping);
		
		for (int j = 0; j <= 1; j++) {		
			final double size = 380d * j + 20d;
			final double s = 99 * j + 1;
			final BoundaryRangeValues<Double> brVals = new BoundaryRangeValues<Double>(size, size, size);
			nodeWidthMapping.addPoint(s, brVals);
			nodeHeightMapping.addPoint(s, brVals);
		}
		
		// Set node size unLock
		Set<VisualPropertyDependency<?>> deps = style.getAllVisualPropertyDependencies();
		for (VisualPropertyDependency<?> dep : deps) {
			final String depName = dep.getIdString();
			if (depName.equals("nodeSizeLocked"))
				dep.setDependency(false);
		}

	}

	public VisualStyle createVisualStyle(final CyNetwork network) {
		final VisualStyleFactory vsFactory = adapter.getVisualStyleFactory();
		final VisualStyle visualStyle = vsFactory.createVisualStyle(bingoVSName);
		adaptVisualStyle(visualStyle, network);
		return visualStyle;
	}
}
