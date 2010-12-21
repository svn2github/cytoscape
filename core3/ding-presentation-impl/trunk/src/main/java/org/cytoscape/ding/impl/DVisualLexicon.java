/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.ding.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.ding.ArrowShape;
import org.cytoscape.ding.NodeShape;
import org.cytoscape.ding.ObjectPosition;
import org.cytoscape.ding.customgraphics.CustomGraphicsManager;
import org.cytoscape.ding.customgraphics.CustomGraphicsRange;
import org.cytoscape.ding.customgraphics.CyCustomGraphics;
import org.cytoscape.ding.customgraphics.NullCustomGraphics;
import org.cytoscape.ding.impl.visualproperty.ArrowShapeTwoDVisualProperty;
import org.cytoscape.ding.impl.visualproperty.CustomGraphicsVisualProperty;
import org.cytoscape.ding.impl.visualproperty.FontTwoDVisualProperty;
import org.cytoscape.ding.impl.visualproperty.IntegerTwoDVisualProperty;
import org.cytoscape.ding.impl.visualproperty.NodeShapeTwoDVisualProperty;
import org.cytoscape.ding.impl.visualproperty.ObjectPositionVisualProperty;
import org.cytoscape.ding.impl.visualproperty.StrokeTwoDVisualProperty;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;
import org.cytoscape.view.presentation.property.BooleanVisualProperty;
import org.cytoscape.view.presentation.property.DefaultVisualizableVisualProperty;
import org.cytoscape.view.presentation.property.DoubleVisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.PaintVisualProperty;
import org.cytoscape.view.presentation.property.StringVisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;


public class DVisualLexicon extends TwoDVisualLexicon {
	
	private static final int DEF_FONT_SIZE = 12;
	private static final double DEF_BORDER_WIDTH = 2.0d;
	
	private static final Set<VisualProperty<?>> CG_POSITIONS = new HashSet<VisualProperty<?>>();
	
	// Root of Ding's VP tree.
	public static final VisualProperty<NullDataType> DING_ROOT = new NullVisualProperty(
			"DING_RENDERING_ENGINE_ROOT", "Ding Rndering Engine Root Visual Property");
	

	public static final VisualProperty<Boolean> NETWORK_NODE_SELECTION = new BooleanVisualProperty(
			Boolean.TRUE, "NETWORK_NODE_SELECTION", "Network Node Selection", CyNetwork.class);
	public static final VisualProperty<Boolean> NETWORK_EDGE_SELECTION = new BooleanVisualProperty(
			Boolean.TRUE, "NETWORK_EDGE_SELECTION", "Network Edge Selection", CyNetwork.class);

	
	public static final VisualProperty<NodeShape> NODE_SHAPE = new NodeShapeTwoDVisualProperty(
			NodeShape.ROUND_RECT, "NODE_SHAPE", "Node Shape");

	public static final VisualProperty<Paint> NODE_SELECTED_PAINT = new PaintVisualProperty(
			Color.YELLOW, TwoDVisualLexicon.PAINT_RANGE, "NODE_SELECTED_PAINT", "Node Selected Paint", CyNode.class);
	public static final VisualProperty<Paint> NODE_BORDER_PAINT = new PaintVisualProperty(
			Color.BLACK, TwoDVisualLexicon.PAINT_RANGE, "NODE_BORDER_PAINT", "Node Border Paint", CyNode.class);

	public static final VisualProperty<Double> NODE_BORDER_WIDTH = new DoubleVisualProperty(
			DEF_BORDER_WIDTH, TwoDVisualLexicon.NONE_ZERO_POSITIVE_DOUBLE_RANGE, "NODE_BORDER_WIDTH", "Node Border Width", CyNode.class);
	public static final VisualProperty<? extends Stroke> NODE_BORDER_STROKE = new StrokeTwoDVisualProperty(
			StrokeTwoDVisualProperty.DEFAULT_STROKE, "NODE_BORDER_STROKE", "Node Border Stroke", CyNode.class);

	public static final VisualProperty<String> NODE_TOOLTIP = new StringVisualProperty(
			"", TwoDVisualLexicon.ARBITRARY_STRING_RANGE, "NODE_TOOLTIP", "Node Tooltip", CyNode.class);
	
	public static final VisualProperty<Font> NODE_LABEL_FONT_FACE = new FontTwoDVisualProperty(
			new Font("SansSerif", Font.PLAIN, DEF_FONT_SIZE),
			"NODE_LABEL_FONT_FACE", "Node Label Font Face", CyNode.class);
	public static final VisualProperty<Integer> NODE_LABEL_FONT_SIZE = new IntegerTwoDVisualProperty(
			DEF_FONT_SIZE, "NODE_LABEL_FONT_SIZE", "Node Label Font Size", CyNode.class);
	public static final VisualProperty<ObjectPosition> NODE_LABEL_POSITION = new ObjectPositionVisualProperty(
			ObjectPositionImpl.DEFAULT_POSITION, "NODE_LABEL_POSITION",
			"Node Label Position", CyNode.class);
	
	public static final VisualProperty<Integer> NODE_TRANSPARENCY = new IntegerTwoDVisualProperty(
			200, "NODE_TRANSPARENCY", "Node Transparency", CyNode.class);
	
	
	// Range object for custom graphics.
	private static final CustomGraphicsRange CG_RANGE = new CustomGraphicsRange();
	
	public static final VisualProperty<Visualizable> NODE_CUSTOMPAINT_1 = new DefaultVisualizableVisualProperty(
			"NODE_CUSTOMPAINT_1", "Node Custom Paint 1", CyNode.class);
	public static final VisualProperty<Visualizable> NODE_CUSTOMPAINT_2 = new DefaultVisualizableVisualProperty(
			"NODE_CUSTOMPAINT_2", "Node Custom Paint 2", CyNode.class);
	public static final VisualProperty<Visualizable> NODE_CUSTOMPAINT_3 = new DefaultVisualizableVisualProperty(
			"NODE_CUSTOMPAINT_3", "Node Custom Paint 3", CyNode.class);
	public static final VisualProperty<Visualizable> NODE_CUSTOMPAINT_4 = new DefaultVisualizableVisualProperty(
			"NODE_CUSTOMPAINT_4", "Node Custom Paint 4", CyNode.class);
	public static final VisualProperty<Visualizable> NODE_CUSTOMPAINT_5 = new DefaultVisualizableVisualProperty(
			"NODE_CUSTOMPAINT_5", "Node Custom Paint 5", CyNode.class);
	public static final VisualProperty<Visualizable> NODE_CUSTOMPAINT_6 = new DefaultVisualizableVisualProperty(
			"NODE_CUSTOMPAINT_6", "Node Custom Paint 6", CyNode.class);
	public static final VisualProperty<Visualizable> NODE_CUSTOMPAINT_7 = new DefaultVisualizableVisualProperty(
			"NODE_CUSTOMPAINT_7", "Node Custom Paint 7", CyNode.class);
	public static final VisualProperty<Visualizable> NODE_CUSTOMPAINT_8 = new DefaultVisualizableVisualProperty(
			"NODE_CUSTOMPAINT_8", "Node Custom Paint 8", CyNode.class);
	public static final VisualProperty<Visualizable> NODE_CUSTOMPAINT_9 = new DefaultVisualizableVisualProperty(
			"NODE_CUSTOMPAINT_9", "Node Custom Paint 9", CyNode.class);
	
	
	public static final VisualProperty<CyCustomGraphics> NODE_CUSTOMGRAPHICS_1 = new CustomGraphicsVisualProperty(
			NullCustomGraphics.getNullObject(), CG_RANGE, "NODE_CUSTOMGRAPHICS_1", "Node Custom Graphics 1", CyNode.class);
	public static final VisualProperty<CyCustomGraphics> NODE_CUSTOMGRAPHICS_2 = new CustomGraphicsVisualProperty(
			NullCustomGraphics.getNullObject(), CG_RANGE, "NODE_CUSTOMGRAPHICS_2", "Node Custom Graphics 2", CyNode.class);
	public static final VisualProperty<CyCustomGraphics> NODE_CUSTOMGRAPHICS_3 = new CustomGraphicsVisualProperty(
			NullCustomGraphics.getNullObject(), CG_RANGE, "NODE_CUSTOMGRAPHICS_3", "Node Custom Graphics 3", CyNode.class);
	public static final VisualProperty<CyCustomGraphics> NODE_CUSTOMGRAPHICS_4 = new CustomGraphicsVisualProperty(
			NullCustomGraphics.getNullObject(), CG_RANGE, "NODE_CUSTOMGRAPHICS_4", "Node Custom Graphics 4", CyNode.class);
	public static final VisualProperty<CyCustomGraphics> NODE_CUSTOMGRAPHICS_5 = new CustomGraphicsVisualProperty(
			NullCustomGraphics.getNullObject(), CG_RANGE, "NODE_CUSTOMGRAPHICS_5", "Node Custom Graphics 5", CyNode.class);
	public static final VisualProperty<CyCustomGraphics> NODE_CUSTOMGRAPHICS_6 = new CustomGraphicsVisualProperty(
			NullCustomGraphics.getNullObject(), CG_RANGE, "NODE_CUSTOMGRAPHICS_6", "Node Custom Graphics 6", CyNode.class);
	public static final VisualProperty<CyCustomGraphics> NODE_CUSTOMGRAPHICS_7 = new CustomGraphicsVisualProperty(
			NullCustomGraphics.getNullObject(), CG_RANGE, "NODE_CUSTOMGRAPHICS_7", "Node Custom Graphics 7", CyNode.class);
	public static final VisualProperty<CyCustomGraphics> NODE_CUSTOMGRAPHICS_8 = new CustomGraphicsVisualProperty(
			NullCustomGraphics.getNullObject(), CG_RANGE, "NODE_CUSTOMGRAPHICS_8", "Node Custom Graphics 8", CyNode.class);
	public static final VisualProperty<CyCustomGraphics> NODE_CUSTOMGRAPHICS_9 = new CustomGraphicsVisualProperty(
			NullCustomGraphics.getNullObject(), CG_RANGE, "NODE_CUSTOMGRAPHICS_9", "Node Custom Graphics 9", CyNode.class);
	
	// Location of custom graphics
	public static final VisualProperty<ObjectPosition> NODE_CUSTOMGRAPHICS_POSITION_1 = new ObjectPositionVisualProperty(
			ObjectPositionImpl.DEFAULT_POSITION, "NODE_CUSTOMGRAPHICS_POSITION_1",
			"Node Custom Graphics Position 1", CyNode.class);
	public static final VisualProperty<ObjectPosition> NODE_CUSTOMGRAPHICS_POSITION_2 = new ObjectPositionVisualProperty(
			ObjectPositionImpl.DEFAULT_POSITION, "NODE_CUSTOMGRAPHICS_POSITION_2",
			"Node Custom Graphics Position 2", CyNode.class);
	public static final VisualProperty<ObjectPosition> NODE_CUSTOMGRAPHICS_POSITION_3 = new ObjectPositionVisualProperty(
			ObjectPositionImpl.DEFAULT_POSITION, "NODE_CUSTOMGRAPHICS_POSITION_3",
			"Node Custom Graphics Position 3", CyNode.class);
	public static final VisualProperty<ObjectPosition> NODE_CUSTOMGRAPHICS_POSITION_4 = new ObjectPositionVisualProperty(
			ObjectPositionImpl.DEFAULT_POSITION, "NODE_CUSTOMGRAPHICS_POSITION_4",
			"Node Custom Graphics Position 4", CyNode.class);
	public static final VisualProperty<ObjectPosition> NODE_CUSTOMGRAPHICS_POSITION_5 = new ObjectPositionVisualProperty(
			ObjectPositionImpl.DEFAULT_POSITION, "NODE_CUSTOMGRAPHICS_POSITION_5",
			"Node Custom Graphics Position 5", CyNode.class);
	public static final VisualProperty<ObjectPosition> NODE_CUSTOMGRAPHICS_POSITION_6 = new ObjectPositionVisualProperty(
			ObjectPositionImpl.DEFAULT_POSITION, "NODE_CUSTOMGRAPHICS_POSITION_6",
			"Node Custom Graphics Position 6", CyNode.class);
	public static final VisualProperty<ObjectPosition> NODE_CUSTOMGRAPHICS_POSITION_7 = new ObjectPositionVisualProperty(
			ObjectPositionImpl.DEFAULT_POSITION, "NODE_CUSTOMGRAPHICS_POSITION_7",
			"Node Custom Graphics Position 7", CyNode.class);
	public static final VisualProperty<ObjectPosition> NODE_CUSTOMGRAPHICS_POSITION_8 = new ObjectPositionVisualProperty(
			ObjectPositionImpl.DEFAULT_POSITION, "NODE_CUSTOMGRAPHICS_POSITION_8",
			"Node Custom Graphics Position 8", CyNode.class);
	public static final VisualProperty<ObjectPosition> NODE_CUSTOMGRAPHICS_POSITION_9 = new ObjectPositionVisualProperty(
			ObjectPositionImpl.DEFAULT_POSITION, "NODE_CUSTOMGRAPHICS_POSITION_9",
			"Node Custom Graphics Position 9", CyNode.class);

	
	// Edge VPs
	public static final VisualProperty<Paint> EDGE_SELECTED_PAINT = new PaintVisualProperty(
			Color.RED, TwoDVisualLexicon.PAINT_RANGE, "EDGE_SELECTED_PAINT", "Edge Selected Paint", CyEdge.class);
	
	public static final VisualProperty<? extends Stroke> EDGE_STROKE = new StrokeTwoDVisualProperty(
			StrokeTwoDVisualProperty.DEFAULT_STROKE, "EDGE_STROKE", "Edge Stroke", CyEdge.class);

	public static final VisualProperty<Paint> EDGE_SOURCE_ARROW_SELECTED_PAINT = new PaintVisualProperty(
			Color.YELLOW, TwoDVisualLexicon.PAINT_RANGE, "EDGE_SOURCE_ARROW_SELECTED_PAINT", "Edge Source Arrow Selected Paint", CyEdge.class);
	public static final VisualProperty<Paint> EDGE_TARGET_ARROW_SELECTED_PAINT = new PaintVisualProperty(
			Color.YELLOW, TwoDVisualLexicon.PAINT_RANGE, "EDGE_TARGET_ARROW_SELECTED_PAINT",
			"Edge Target Arrow Selected Paint", CyEdge.class);
	public static final VisualProperty<Paint> EDGE_SOURCE_ARROW_UNSELECTED_PAINT = new PaintVisualProperty(
			Color.BLACK, TwoDVisualLexicon.PAINT_RANGE, "EDGE_SOURCE_ARROW_UNSELECTED_PAINT",
			"Edge Source Arrow Unselected Paint", CyEdge.class);
	public static final VisualProperty<Paint> EDGE_TARGET_ARROW_UNSELECTED_PAINT = new PaintVisualProperty(
			Color.BLACK, TwoDVisualLexicon.PAINT_RANGE, "EDGE_TARGET_ARROW_UNSELECTED_PAINT",
			"Edge Target Arrow Unselected Paint", CyEdge.class);

	public static final VisualProperty<ArrowShape> EDGE_SOURCE_ARROW_SHAPE = new ArrowShapeTwoDVisualProperty(
			ArrowShape.NONE, "EDGE_SOURCE_ARROW_SHAPE",
			"Edge Source Arrow Shape");
	public static final VisualProperty<ArrowShape> EDGE_TARGET_ARROW_SHAPE = new ArrowShapeTwoDVisualProperty(
			ArrowShape.NONE, "EDGE_TARGET_ARROW_SHAPE",
			"Edge Target Arrow Shape");

	public static final VisualProperty<String> EDGE_TOOLTIP = new StringVisualProperty(
			"", TwoDVisualLexicon.ARBITRARY_STRING_RANGE, "EDGE_TOOLTIP", "Edge Tooltip", CyEdge.class);


	public static final VisualProperty<Font> EDGE_LABEL_FONT_FACE = new FontTwoDVisualProperty(
			new Font("SansSerif", Font.PLAIN, 10),
			"EDGE_LABEL_FONT_FACE", "Edge Label Font Face", CyEdge.class);
	public static final VisualProperty<Integer> EDGE_LABEL_FONT_SIZE = new IntegerTwoDVisualProperty(
			10, "EDGE_LABEL_FONT_SIZE", "Edge Label Font Size", CyEdge.class);
	
	public static final VisualProperty<ObjectPosition> EDGE_LABEL_POSITION = new ObjectPositionVisualProperty(
			ObjectPositionImpl.DEFAULT_POSITION, "EDGE_LABEL_POSITION",
			"Edge Label Position", CyEdge.class);
	
	static {
		CG_POSITIONS.add(NODE_CUSTOMGRAPHICS_POSITION_1);
		CG_POSITIONS.add(NODE_CUSTOMGRAPHICS_POSITION_2);
		CG_POSITIONS.add(NODE_CUSTOMGRAPHICS_POSITION_3);
		CG_POSITIONS.add(NODE_CUSTOMGRAPHICS_POSITION_4);
		CG_POSITIONS.add(NODE_CUSTOMGRAPHICS_POSITION_5);
		CG_POSITIONS.add(NODE_CUSTOMGRAPHICS_POSITION_6);
		CG_POSITIONS.add(NODE_CUSTOMGRAPHICS_POSITION_7);
		CG_POSITIONS.add(NODE_CUSTOMGRAPHICS_POSITION_8);
		CG_POSITIONS.add(NODE_CUSTOMGRAPHICS_POSITION_9);
	}
	

	public DVisualLexicon(final CustomGraphicsManager manager) {
		super(DING_ROOT);
		
		CG_RANGE.setManager(manager);
			
		addVisualProperty(NETWORK_NODE_SELECTION, NETWORK);
		addVisualProperty(NETWORK_EDGE_SELECTION, NETWORK);
		
		addVisualProperty(NODE_BORDER_PAINT, NODE_PAINT);
		
		addVisualProperty(NODE_TOOLTIP, NODE_TEXT);
		
		addVisualProperty(NODE_LABEL_FONT_SIZE, NODE_SIZE);
		
		addVisualProperty(NODE_LABEL_POSITION, NODE);
		addVisualProperty(NODE_LABEL_FONT_FACE, NODE);
		
		addVisualProperty(NODE_SHAPE, NODE);
		addVisualProperty(NODE_SELECTED_PAINT, NODE_PAINT);
		addVisualProperty(NODE_BORDER_WIDTH, NODE);
		addVisualProperty(NODE_BORDER_STROKE, NODE);
		addVisualProperty(NODE_TRANSPARENCY, NODE);
		
		
		//
		addVisualProperty(NODE_CUSTOMPAINT_1, NODE_PAINT);
		addVisualProperty(NODE_CUSTOMPAINT_2, NODE_PAINT);
		addVisualProperty(NODE_CUSTOMPAINT_3, NODE_PAINT);
		addVisualProperty(NODE_CUSTOMPAINT_4, NODE_PAINT);
		addVisualProperty(NODE_CUSTOMPAINT_5, NODE_PAINT);
		addVisualProperty(NODE_CUSTOMPAINT_6, NODE_PAINT);
		addVisualProperty(NODE_CUSTOMPAINT_7, NODE_PAINT);
		addVisualProperty(NODE_CUSTOMPAINT_8, NODE_PAINT);
		addVisualProperty(NODE_CUSTOMPAINT_9, NODE_PAINT);
		
		// Custom Graphics.  Currently Cytoscape supports 9 objects/node.
		addVisualProperty(NODE_CUSTOMGRAPHICS_1, NODE_CUSTOMPAINT_1);
		addVisualProperty(NODE_CUSTOMGRAPHICS_2, NODE_CUSTOMPAINT_2);
		addVisualProperty(NODE_CUSTOMGRAPHICS_3, NODE_CUSTOMPAINT_3);
		addVisualProperty(NODE_CUSTOMGRAPHICS_4, NODE_CUSTOMPAINT_4);
		addVisualProperty(NODE_CUSTOMGRAPHICS_5, NODE_CUSTOMPAINT_5);
		addVisualProperty(NODE_CUSTOMGRAPHICS_6, NODE_CUSTOMPAINT_6);
		addVisualProperty(NODE_CUSTOMGRAPHICS_7, NODE_CUSTOMPAINT_7);
		addVisualProperty(NODE_CUSTOMGRAPHICS_8, NODE_CUSTOMPAINT_8);
		addVisualProperty(NODE_CUSTOMGRAPHICS_9, NODE_CUSTOMPAINT_9);
		
		// These are children of NODE_CUSTOMGRAPHICS.
		addVisualProperty(NODE_CUSTOMGRAPHICS_POSITION_1, NODE_CUSTOMPAINT_1);
		addVisualProperty(NODE_CUSTOMGRAPHICS_POSITION_2, NODE_CUSTOMPAINT_2);
		addVisualProperty(NODE_CUSTOMGRAPHICS_POSITION_3, NODE_CUSTOMPAINT_3);
		addVisualProperty(NODE_CUSTOMGRAPHICS_POSITION_4, NODE_CUSTOMPAINT_4);
		addVisualProperty(NODE_CUSTOMGRAPHICS_POSITION_5, NODE_CUSTOMPAINT_5);
		addVisualProperty(NODE_CUSTOMGRAPHICS_POSITION_6, NODE_CUSTOMPAINT_6);
		addVisualProperty(NODE_CUSTOMGRAPHICS_POSITION_7, NODE_CUSTOMPAINT_7);
		addVisualProperty(NODE_CUSTOMGRAPHICS_POSITION_8, NODE_CUSTOMPAINT_8);
		addVisualProperty(NODE_CUSTOMGRAPHICS_POSITION_9, NODE_CUSTOMPAINT_9);
		
		addVisualProperty(EDGE_SELECTED_PAINT, EDGE_PAINT);
		addVisualProperty(EDGE_STROKE, EDGE);
		addVisualProperty(EDGE_SOURCE_ARROW_SELECTED_PAINT, EDGE_PAINT);
		addVisualProperty(EDGE_TARGET_ARROW_SELECTED_PAINT, EDGE_PAINT);
		addVisualProperty(EDGE_SOURCE_ARROW_UNSELECTED_PAINT, EDGE_PAINT);
		addVisualProperty(EDGE_TARGET_ARROW_UNSELECTED_PAINT, EDGE_PAINT);
		addVisualProperty(EDGE_SOURCE_ARROW_SHAPE, EDGE);
		addVisualProperty(EDGE_TARGET_ARROW_SHAPE, EDGE);
		addVisualProperty(EDGE_TOOLTIP, EDGE_TEXT);
		addVisualProperty(EDGE_LABEL_POSITION, EDGE);
		addVisualProperty(EDGE_LABEL_FONT_FACE, EDGE);
		addVisualProperty(EDGE_LABEL_FONT_SIZE, EDGE);	
	}
	
	static Set<VisualProperty<?>> getGraphicsPositionVP() {
		return CG_POSITIONS;
	}
}
