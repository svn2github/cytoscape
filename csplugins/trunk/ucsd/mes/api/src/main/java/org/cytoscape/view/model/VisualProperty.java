package org.cytoscape.view.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

/**
 * Defines all of the possible visual properties of a network in Cytoscape as
 * well as the type of that property. The benefit of making this an enum is that
 * it allows us to quickly see all properties that have been set. We'll probably
 * want some way to distinguish node properties from edge and network
 * properties.
 */
public enum VisualProperty {

	// Position of node
	NODE_X_POSITION(Double.class), NODE_Y_POSITION(Double.class), NODE_Z_POSITION(Double.class),

	// For time series data
	NODE_T_POSITION(Double.class),

	// Use java.awt.Paint to support both Color and custom graphics
	NODE_PAINT(Paint.class), NODE_PAINT_OPACITY(Integer.class),

	// Shape of node
	NODE_SHAPE(NodeShape.class), 
	
	// Size
	NODE_WIDTH(Integer.class), NODE_HEIGHT(Integer.class), NODE_DEPTH(Integer.class), 
	
	// Label/tooltip strings
	NODE_LABEL(String.class), NODE_TOOLTIP(String.class),
	
	// Max. width of label on a line
	NODE_LABEL_WIDTH(Integer.class), 
	
	// Color and Font data of node label
	NODE_LABEL_COLOR(Paint.class), 
	NODE_LABEL_OPACITY(Integer.class), NODE_LABEL_POSITION(LabelPosition.class), 
	NODE_LABEL_FONT_FACE(Font.class), NODE_LABEL_FONT_SIZE(Integer.class), 
	
	// Node border line.  Maybe ignored in 3D
	NODE_BORDER_WIDTH(Integer.class), NODE_BORDER_STYLE(LineStyle.class), 
	NODE_BORDER_COLOR(Paint.class), NODE_BORDER_OPACITY(Integer.class),

	// Edge color
	EDGE_COLOR(Paint.class), EDGE_OPACITY(Integer.class),
	
	// Edge label/tooltip strings
	EDGE_LABEL(String.class), EDGE_TOOLTIP(String.class),
	
	// Edge label appearances
	EDGE_LABEL_WIDTH(Integer.class), EDGE_LABEL_COLOR(Paint.class), 
	EDGE_LABEL_OPACITY(Integer.class), 
	EDGE_LABEL_POSITION(LabelPosition.class), EDGE_LABEL_FONT_FACE(Font.class), 
	EDGE_LABEL_FONT_SIZE(Integer.class),
	
	// Edge styles
	EDGE_LINE_WIDTH(Integer.class), EDGE_LINE_STYLE(LineStyle.class), 
	EDGE_CURVE_STYLE(EdgeStyle.class),
	
	// Arrow appearances
	EDGE_SRC_ARROW_SHAPE(ArrowShape.class), EDGE_TGT_ARROW_SHAPE(ArrowShape.class), 
	EDGE_SRC_ARROW_PAINT(Paint.class), EDGE_TGT_ARROW_PAINT(Paint.class), 
	EDGE_SRC_ARROW_OPACITY(Double.class), EDGE_TGT_ARROW_OPACITY(Double.class),
	
	// Background Paint.  Images can be used here.
	NETWORK_BACKGROUND_PAINT(Paint.class), 

	// Zoom level
	NETWORK_ZOOM_FACTOR(Double.class), 
	
	// Colors for selection.  Should also be Paint...?
	NETWORK_NODE_SELECTION_COLOR(Paint.class), NETWORK_NODE_REVERSE_SELECTION_COLOR(Paint.class), 
	NETWORK_EDGE_SELECTION_COLOR(Paint.class), NETWORK_EDGE_REVERSE_SELECTION_COLOR(Paint.class),

	// Background for Groups
	GROUP_BACKGROUND_PAINT(Paint.class);

	private Class<?> classType;

	private VisualProperty(Class<?> ct) {
		classType = ct;
	}

	private Class<?> getClassType() {
		return classType;
	}
}
