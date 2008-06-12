package org.cytoscape.view;

import java.awt.Color;
import java.awt.Font;

/**
 * Defines all of the possible visual properties of a network in Cytoscape as
 * well as the type of that property.
 * The benefit of making this an enum is that it allows us to quickly see all
 * properties that have been set.  We'll probably want some way to distinguish
 * node properties from edge and network properties.
 */
public enum VisualProperty {

	NODE_X_POSITION(Double.class),
	NODE_Y_POSITION(Double.class),
	NODE_Z_POSITION(Double.class),
	NODE_T_POSITION(Double.class),
	NODE_FILL_COLOR(Color.class),
	NODE_FILL_OPACITY(Integer.class),
	NODE_SHAPE(NodeShape.class),
	NODE_WIDTH(Integer.class),
	NODE_HEIGHT(Integer.class),
	NODE_LABEL(String.class),
	NODE_LABEL_WIDTH(Integer.class),
	NODE_LABEL_COLOR(Color.class),
	NODE_LABEL_OPACITY(Integer.class),
	NODE_LABEL_POSITION(LabelPosition.class),
	NODE_FONT_FACE(Font.class),
	NODE_FONT_SIZE(Integer.class),
	NODE_TOOLTIP(String.class),
	NODE_BORDER_WIDTH(Integer.class),
	NODE_BORDER_STYLE(LineStyle.class),
	NODE_BORDER_COLOR(Color.class),
	NODE_BORDER_OPACITY(Integer.class),

	EDGE_COLOR(Color.class),
	EDGE_OPACITY(Integer.class),
	EDGE_LABEL(String.class),
	EDGE_LABEL_WIDTH(Integer.class),
	EDGE_LABEL_COLOR(Color.class),
	EDGE_LABEL_OPACITY(Integer.class),
	EDGE_LABEL_POSITION(LabelPosition.class),
	EDGE_FONT_FACE(Font.class),
	EDGE_FONT_SIZE(Integer.class),
	EDGE_TOOLTIP(String.class),
	EDGE_LINE_WIDTH(Integer.class),
	EDGE_LINE_STYLE(LineStyle.class),
	EDGE_SRC_ARROW_SHAPE(ArrowShape.class),
	EDGE_TGT_ARROW_SHAPE(ArrowShape.class),
	EDGE_SRC_ARROW_COLOR(Color.class),
	EDGE_TGT_ARROW_COLOR(Color.class),
	EDGE_SRC_ARROW_OPACITY(Double.class),
	EDGE_TGT_ARROW_OPACITY(Double.class),
	EDGE_CURVE_STYLE(EdgeStyle.class),

	NETWORK_BACKGROUND_COLOR(Color.class),
	NETWORK_ZOOM_FACTOR(Double.class),
	NETWORK_NODE_SELECTION_COLOR(Color.class),
	NETWORK_NODE_REVERSE_SELECTION_COLOR(Color.class),
	NETWORK_EDGE_SELECTION_COLOR(Color.class),
	NETWORK_EDGE_REVERSE_SELECTION_COLOR(Color.class),

	GROUP_BACKGROUND_COLOR(Color.class),
	;

	private Class classType;

	private VisualProperty(Class ct) {
		classType = ct;
	}

	private Class getClassType() {
		return classType;
	}
}
