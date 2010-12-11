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

import org.cytoscape.ding.ArrowShape;
import org.cytoscape.ding.NodeShape;
import org.cytoscape.ding.ObjectPosition;
import org.cytoscape.ding.impl.visualproperty.ArrowShapeTwoDVisualProperty;
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
import org.cytoscape.view.presentation.property.BooleanVisualProperty;
import org.cytoscape.view.presentation.property.DoubleVisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.PaintVisualProperty;
import org.cytoscape.view.presentation.property.StringVisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;


public class DVisualLexicon extends TwoDVisualLexicon {
	
	private static final int DEF_FONT_SIZE = 12;
	private static final double DEF_BORDER_WIDTH = 2.0d;
	
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
	

	public DVisualLexicon() {
		super(DING_ROOT);
			
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
}
