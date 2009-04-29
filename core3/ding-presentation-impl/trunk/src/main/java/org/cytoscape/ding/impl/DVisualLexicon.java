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

import static org.cytoscape.model.GraphObject.EDGE;
import static org.cytoscape.model.GraphObject.NETWORK;
import static org.cytoscape.model.GraphObject.NODE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import org.cytoscape.ding.ArrowShape;
import org.cytoscape.ding.NodeShape;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.twod.BooleanTwoDVisualProperty;
import org.cytoscape.view.presentation.twod.ColorTwoDVisualProperty;
import org.cytoscape.view.presentation.twod.DoubleTwoDVisualProperty;
import org.cytoscape.view.presentation.twod.StringTwoDVisualProperty;
import org.cytoscape.view.presentation.twod.TwoDVisualLexicon;

public class DVisualLexicon extends TwoDVisualLexicon {

	public static final VisualProperty<Boolean> NETWORK_NODE_SELECTION = new BooleanTwoDVisualProperty(
			NETWORK, Boolean.TRUE, "NETWORK_NODE_SELECTION",
			"Network Node Selection");
	public static final VisualProperty<Boolean> NETWORK_EDGE_SELECTION = new BooleanTwoDVisualProperty(
			NETWORK, Boolean.TRUE, "NETWORK_EDGE_SELECTION",
			"Network Edge Selection");

	public static final VisualProperty<NodeShape> NODE_SHAPE = new NodeShapeTwoDVisualProperty(
			NODE, NodeShape.ELLIPSE, "NODE_SHAPE", "Node Shape");

	public static final VisualProperty<? extends Paint> NODE_SELECTED_PAINT = new ColorTwoDVisualProperty(
			NODE, Color.YELLOW, "NODE_SELECTED_PAINT", "Node Selected Paint");

	public static final VisualProperty<? extends Paint> NODE_BORDER_PAINT = new ColorTwoDVisualProperty(
			NODE, Color.BLACK, "NODE_BORDER_PAINT", "Node Border Paint");

	public static final VisualProperty<Double> NODE_BORDER_WIDTH = new DoubleTwoDVisualProperty(
			NODE, 2.0, "NODE_BORDER_WIDTH", "Node Border Width");

	public static final VisualProperty<? extends Stroke> NODE_BORDER_STROKE = new StrokeTwoDVisualProperty(
			NODE, new BasicStroke(), "NODE_BORDER_STROKE", "Node Border Stroke");

	public static final VisualProperty<Double> NODE_WIDTH = new DoubleTwoDVisualProperty(
			NODE, 40.0, "NODE_WIDTH", "Node Width");
	public static final VisualProperty<Double> NODE_HEIGHT = new DoubleTwoDVisualProperty(
			NODE, 40.0, "NODE_HEIGHT", "Node Height");
	public static final VisualProperty<String> NODE_TOOLTIP = new StringTwoDVisualProperty(
			NODE, "", "NODE_TOOLTIP", "Node Tooltip");
	public static final VisualProperty<Font> NODE_LABEL_FONT_FACE = new FontTwoDVisualProperty(
			NODE, new Font("SansSerif", Font.PLAIN, 10),
			"NODE_LABEL_FONT_FACE", "Node Label Font Face");
	public static final VisualProperty<Integer> NODE_LABEL_FONT_SIZE = new IntegerTwoDVisualProperty(
			NODE, 10, "NODE_LABEL_FONT_SIZE", "Node Label Font Size");

	public static final VisualProperty<Anchor> NODE_LABEL_TEXT_ANCHOR = new AnchorTwoDVisualProperty(
			NODE, Anchor.CENTER, "NODE_LABEL_TEXT_ANCHOR",
			"Node Label Text Anchor");
	public static final VisualProperty<Anchor> NODE_LABEL_NODE_ANCHOR = new AnchorTwoDVisualProperty(
			NODE, Anchor.CENTER, "NODE_LABEL_NODE_ANCHOR",
			"Node Label Node Anchor");

	public static final VisualProperty<Double> NODE_LABEL_ANCHOR_X_OFFSET = new DoubleTwoDVisualProperty(
			NODE, 0.0, "NODE_LABEL_ANCHOR_X_OFFSET",
			"Node Label Anchor X Offset");
	public static final VisualProperty<Double> NODE_LABEL_ANCHOR_Y_OFFSET = new DoubleTwoDVisualProperty(
			NODE, 0.0, "NODE_LABEL_ANCHOR_Y_OFFSET",
			"Node Label Anchor Y Offset");

	public static final VisualProperty<Justify> NODE_LABEL_JUSTIFY = new JustifyTwoDVisualProperty(
			NODE, Justify.LEFT, "NODE_LABEL_JUSTIFY", "Node Label Justify");
	public static final VisualProperty<Integer> NODE_TRANSPARENCY = new IntegerTwoDVisualProperty(
			NODE, 255, "NODE_TRANSPARENCY", "Node Transparency");

	public static final VisualProperty<? extends Paint> EDGE_SELECTED_PAINT = new ColorTwoDVisualProperty(
			EDGE, Color.RED, "EDGE_SELECTED_PAINT", "Edge Selected Paint");
	public static final VisualProperty<? extends Stroke> EDGE_STROKE = new StrokeTwoDVisualProperty(
			EDGE, new BasicStroke(), "EDGE_STROKE", "Edge Stroke");

	public static final VisualProperty<Double> EDGE_WIDTH = new DoubleTwoDVisualProperty(
			EDGE, 2.0, "EDGE_WIDTH", "Edge Width");

	public static final VisualProperty<? extends Paint> EDGE_SOURCE_ARROW_SELECTED_PAINT = new ColorTwoDVisualProperty(
			EDGE, Color.YELLOW, "EDGE_SOURCE_ARROW_SELECTED_PAINT",
			"Edge Source Arrow Selected Paint");
	public static final VisualProperty<? extends Paint> EDGE_TARGET_ARROW_SELECTED_PAINT = new ColorTwoDVisualProperty(
			EDGE, Color.YELLOW, "EDGE_TARGET_ARROW_SELECTED_PAINT",
			"Edge Target Arrow Selected Paint");
	public static final VisualProperty<? extends Paint> EDGE_SOURCE_ARROW_UNSELECTED_PAINT = new ColorTwoDVisualProperty(
			EDGE, Color.BLACK, "EDGE_SOURCE_ARROW_UNSELECTED_PAINT",
			"Edge Source Arrow Unselected Paint");
	public static final VisualProperty<? extends Paint> EDGE_TARGET_ARROW_UNSELECTED_PAINT = new ColorTwoDVisualProperty(
			EDGE, Color.BLACK, "EDGE_TARGET_ARROW_UNSELECTED_PAINT",
			"Edge Target Arrow Unselected Paint");

	public static final VisualProperty<ArrowShape> EDGE_SOURCE_ARROW_SHAPE = new ArrowShapeTwoDVisualProperty(
			EDGE, ArrowShape.NONE, "EDGE_SOURCE_ARROW_SHAPE",
			"Edge Source Arrow Shape");
	public static final VisualProperty<ArrowShape> EDGE_TARGET_ARROW_SHAPE = new ArrowShapeTwoDVisualProperty(
			EDGE, ArrowShape.NONE, "EDGE_TARGET_ARROW_SHAPE",
			"Edge Target Arrow Shape");

	public static final VisualProperty<String> EDGE_TOOLTIP = new StringTwoDVisualProperty(
			EDGE, "", "EDGE_TOOLTIP", "Edge Tooltip");

	public static final VisualProperty<Anchor> EDGE_LABEL_TEXT_ANCHOR = new AnchorTwoDVisualProperty(
			EDGE, Anchor.CENTER, "EDGE_LABEL_TEXT_ANCHOR",
			"Edge Label Text Anchor");
	public static final VisualProperty<Anchor> EDGE_LABEL_EDGE_ANCHOR = new AnchorTwoDVisualProperty(
			EDGE, Anchor.CENTER, "EDGE_LABEL_EDGE_ANCHOR",
			"Edge Label Edge Anchor");

	public static final VisualProperty<Double> EDGE_LABEL_ANCHOR_X_OFFSET = new DoubleTwoDVisualProperty(
			EDGE, 0.0, "EDGE_LABEL_ANCHOR_X_OFFSET",
			"Edge Label Anchor X Offset");
	public static final VisualProperty<Double> EDGE_LABEL_ANCHOR_Y_OFFSET = new DoubleTwoDVisualProperty(
			EDGE, 0.0, "EDGE_LABEL_ANCHOR_Y_OFFSET",
			"Edge Label Anchor Y Offset");

	public static final VisualProperty<Font> EDGE_LABEL_FONT_FACE = new FontTwoDVisualProperty(
			EDGE, new Font("SansSerif", Font.PLAIN, 10),
			"EDGE_LABEL_FONT_FACE", "Edge Label Font Face");
	public static final VisualProperty<Integer> EDGE_LABEL_FONT_SIZE = new IntegerTwoDVisualProperty(
			EDGE, 10, "EDGE_LABEL_FONT_SIZE", "Edge Label Font Size");
	public static final VisualProperty<Justify> EDGE_LABEL_JUSTIFY = new JustifyTwoDVisualProperty(
			EDGE, Justify.LEFT, "EDGE_LABEL_JUSTIFY", "Edge Label Justify");

	public DVisualLexicon() {
		super();

		visualPropertySet.add(NODE_LABEL_JUSTIFY);
		visualPropertySet.add(NODE_LABEL_ANCHOR_Y_OFFSET);
		visualPropertySet.add(NODE_LABEL_ANCHOR_X_OFFSET);
		visualPropertySet.add(NODE_LABEL_NODE_ANCHOR);
		visualPropertySet.add(NODE_LABEL_TEXT_ANCHOR);
		visualPropertySet.add(NODE_LABEL_FONT_SIZE);
		visualPropertySet.add(NODE_LABEL_FONT_FACE);
		visualPropertySet.add(NODE_TOOLTIP);
		visualPropertySet.add(NETWORK_NODE_SELECTION);
		visualPropertySet.add(NETWORK_EDGE_SELECTION);
		visualPropertySet.add(NODE_SHAPE);
		visualPropertySet.add(NODE_SELECTED_PAINT);
		visualPropertySet.add(NODE_BORDER_PAINT);
		visualPropertySet.add(NODE_BORDER_WIDTH);
		visualPropertySet.add(NODE_BORDER_STROKE);
		visualPropertySet.add(NODE_WIDTH);
		visualPropertySet.add(NODE_HEIGHT);
		visualPropertySet.add(NODE_TRANSPARENCY);
		visualPropertySet.add(EDGE_SELECTED_PAINT);
		visualPropertySet.add(EDGE_STROKE);
		visualPropertySet.add(EDGE_WIDTH);
		visualPropertySet.add(EDGE_SOURCE_ARROW_SELECTED_PAINT);
		visualPropertySet.add(EDGE_TARGET_ARROW_SELECTED_PAINT);
		visualPropertySet.add(EDGE_SOURCE_ARROW_UNSELECTED_PAINT);
		visualPropertySet.add(EDGE_TARGET_ARROW_UNSELECTED_PAINT);
		visualPropertySet.add(EDGE_SOURCE_ARROW_SHAPE);
		visualPropertySet.add(EDGE_TARGET_ARROW_SHAPE);
		visualPropertySet.add(EDGE_TOOLTIP);
		visualPropertySet.add(EDGE_LABEL_TEXT_ANCHOR);
		visualPropertySet.add(EDGE_LABEL_EDGE_ANCHOR);
		visualPropertySet.add(EDGE_LABEL_ANCHOR_X_OFFSET);
		visualPropertySet.add(EDGE_LABEL_ANCHOR_Y_OFFSET);
		visualPropertySet.add(EDGE_LABEL_FONT_FACE);
		visualPropertySet.add(EDGE_LABEL_FONT_SIZE);
		visualPropertySet.add(EDGE_LABEL_JUSTIFY);
	}

}
