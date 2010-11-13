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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import org.cytoscape.ding.ArrowShape;
import org.cytoscape.ding.NodeShape;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BooleanVisualProperty;
import org.cytoscape.view.presentation.property.PaintVisualProperty;
import org.cytoscape.view.presentation.property.DoubleVisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
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
			Color.YELLOW, "NODE_SELECTED_PAINT", "Node Selected Paint", CyNode.class);

	public static final VisualProperty<Paint> NODE_BORDER_PAINT = new PaintVisualProperty(
			Color.BLACK, "NODE_BORDER_PAINT", "Node Border Paint", CyNode.class);

	public static final VisualProperty<Double> NODE_BORDER_WIDTH = new DoubleVisualProperty(
			DEF_BORDER_WIDTH, "NODE_BORDER_WIDTH", "Node Border Width", CyNode.class);

	public static final VisualProperty<? extends Stroke> NODE_BORDER_STROKE = new StrokeTwoDVisualProperty(
			new BasicStroke(), "NODE_BORDER_STROKE", "Node Border Stroke", CyNode.class);

	public static final VisualProperty<String> NODE_TOOLTIP = new StringVisualProperty(
			"", "NODE_TOOLTIP", "Node Tooltip", CyNode.class);
	public static final VisualProperty<Font> NODE_LABEL_FONT_FACE = new FontTwoDVisualProperty(
			new Font("SansSerif", Font.PLAIN, DEF_FONT_SIZE),
			"NODE_LABEL_FONT_FACE", "Node Label Font Face", CyNode.class);
	
	public static final VisualProperty<Integer> NODE_LABEL_FONT_SIZE = new IntegerTwoDVisualProperty(
			DEF_FONT_SIZE, "NODE_LABEL_FONT_SIZE", "Node Label Font Size", CyNode.class);

	public static final VisualProperty<Anchor> NODE_LABEL_TEXT_ANCHOR = new AnchorTwoDVisualProperty(
			Anchor.SOUTHEAST, "NODE_LABEL_TEXT_ANCHOR",
			"Node Label Text Anchor", CyNode.class);
	public static final VisualProperty<Anchor> NODE_LABEL_NODE_ANCHOR = new AnchorTwoDVisualProperty(
			Anchor.SOUTHEAST, "NODE_LABEL_NODE_ANCHOR",
			"Node Label Node Anchor", CyNode.class);

	public static final VisualProperty<Double> NODE_LABEL_ANCHOR_X_OFFSET = new DoubleVisualProperty(
			0.0, "NODE_LABEL_ANCHOR_X_OFFSET",
			"Node Label Anchor X Offset", CyNode.class);
	public static final VisualProperty<Double> NODE_LABEL_ANCHOR_Y_OFFSET = new DoubleVisualProperty(
			0.0, "NODE_LABEL_ANCHOR_Y_OFFSET",
			"Node Label Anchor Y Offset", CyNode.class);

	public static final VisualProperty<Justify> NODE_LABEL_JUSTIFY = new JustifyTwoDVisualProperty(
			Justify.LEFT, "NODE_LABEL_JUSTIFY", "Node Label Justify", CyNode.class);
	
	public static final VisualProperty<Integer> NODE_TRANSPARENCY = new IntegerTwoDVisualProperty(
			200, "NODE_TRANSPARENCY", "Node Transparency", CyNode.class);

	
	// Edge VPs
	public static final VisualProperty<Paint> EDGE_SELECTED_PAINT = new PaintVisualProperty(
			Color.RED, "EDGE_SELECTED_PAINT", "Edge Selected Paint", CyEdge.class);
	
	public static final VisualProperty<? extends Stroke> EDGE_STROKE = new StrokeTwoDVisualProperty(
			new BasicStroke(), "EDGE_STROKE", "Edge Stroke", CyEdge.class);

	public static final VisualProperty<Paint> EDGE_SOURCE_ARROW_SELECTED_PAINT = new PaintVisualProperty(
			Color.YELLOW, "EDGE_SOURCE_ARROW_SELECTED_PAINT", "Edge Source Arrow Selected Paint", CyEdge.class);
	public static final VisualProperty<Paint> EDGE_TARGET_ARROW_SELECTED_PAINT = new PaintVisualProperty(
			Color.YELLOW, "EDGE_TARGET_ARROW_SELECTED_PAINT",
			"Edge Target Arrow Selected Paint", CyEdge.class);
	public static final VisualProperty<Paint> EDGE_SOURCE_ARROW_UNSELECTED_PAINT = new PaintVisualProperty(
			Color.BLACK, "EDGE_SOURCE_ARROW_UNSELECTED_PAINT",
			"Edge Source Arrow Unselected Paint", CyEdge.class);
	public static final VisualProperty<Paint> EDGE_TARGET_ARROW_UNSELECTED_PAINT = new PaintVisualProperty(
			Color.BLACK, "EDGE_TARGET_ARROW_UNSELECTED_PAINT",
			"Edge Target Arrow Unselected Paint", CyEdge.class);

	public static final VisualProperty<ArrowShape> EDGE_SOURCE_ARROW_SHAPE = new ArrowShapeTwoDVisualProperty(
			ArrowShape.NONE, "EDGE_SOURCE_ARROW_SHAPE",
			"Edge Source Arrow Shape");
	public static final VisualProperty<ArrowShape> EDGE_TARGET_ARROW_SHAPE = new ArrowShapeTwoDVisualProperty(
			ArrowShape.NONE, "EDGE_TARGET_ARROW_SHAPE",
			"Edge Target Arrow Shape");

	public static final VisualProperty<String> EDGE_TOOLTIP = new StringVisualProperty(
			"", "EDGE_TOOLTIP", "Edge Tooltip", CyEdge.class);

	public static final VisualProperty<Anchor> EDGE_LABEL_TEXT_ANCHOR = new AnchorTwoDVisualProperty(
			Anchor.CENTER, "EDGE_LABEL_TEXT_ANCHOR",
			"Edge Label Text Anchor", CyEdge.class);
	public static final VisualProperty<Anchor> EDGE_LABEL_EDGE_ANCHOR = new AnchorTwoDVisualProperty(
			Anchor.CENTER, "EDGE_LABEL_EDGE_ANCHOR",
			"Edge Label Edge Anchor", CyEdge.class);

	public static final VisualProperty<Double> EDGE_LABEL_ANCHOR_X_OFFSET = new DoubleVisualProperty(
			0.0, "EDGE_LABEL_ANCHOR_X_OFFSET",
			"Edge Label Anchor X Offset", CyEdge.class);
	public static final VisualProperty<Double> EDGE_LABEL_ANCHOR_Y_OFFSET = new DoubleVisualProperty(
			0.0, "EDGE_LABEL_ANCHOR_Y_OFFSET",
			"Edge Label Anchor Y Offset", CyEdge.class);

	public static final VisualProperty<Font> EDGE_LABEL_FONT_FACE = new FontTwoDVisualProperty(
			new Font("SansSerif", Font.PLAIN, 10),
			"EDGE_LABEL_FONT_FACE", "Edge Label Font Face", CyEdge.class);
	public static final VisualProperty<Integer> EDGE_LABEL_FONT_SIZE = new IntegerTwoDVisualProperty(
			10, "EDGE_LABEL_FONT_SIZE", "Edge Label Font Size", CyEdge.class);
	public static final VisualProperty<Justify> EDGE_LABEL_JUSTIFY = new JustifyTwoDVisualProperty(
			Justify.LEFT, "EDGE_LABEL_JUSTIFY", "Edge Label Justify", CyEdge.class);

	public DVisualLexicon() {
		super(DING_ROOT);
				
		addVisualProperty(NODE_BORDER_PAINT, NODE_PAINT);
		
		addVisualProperty(NODE_TOOLTIP, NODE_TEXT);
		
		addVisualProperty(NODE_LABEL_FONT_SIZE, NODE_SIZE);
		
		addVisualProperty(NODE_LABEL_JUSTIFY, NODE);
		addVisualProperty(NODE_LABEL_ANCHOR_Y_OFFSET, NODE);
		addVisualProperty(NODE_LABEL_ANCHOR_X_OFFSET, NODE);
		addVisualProperty(NODE_LABEL_NODE_ANCHOR, NODE);
		addVisualProperty(NODE_LABEL_TEXT_ANCHOR, NODE);
		addVisualProperty(NODE_LABEL_FONT_FACE, NODE);
		addVisualProperty(NETWORK_NODE_SELECTION, NETWORK);
		addVisualProperty(NETWORK_EDGE_SELECTION, NETWORK);
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
		addVisualProperty(EDGE_LABEL_TEXT_ANCHOR, EDGE);
		addVisualProperty(EDGE_LABEL_EDGE_ANCHOR, EDGE);
		addVisualProperty(EDGE_LABEL_ANCHOR_X_OFFSET, EDGE);
		addVisualProperty(EDGE_LABEL_ANCHOR_Y_OFFSET, EDGE);
		addVisualProperty(EDGE_LABEL_FONT_FACE, EDGE);
		addVisualProperty(EDGE_LABEL_FONT_SIZE, EDGE);
		addVisualProperty(EDGE_LABEL_JUSTIFY, EDGE);
	}
}
