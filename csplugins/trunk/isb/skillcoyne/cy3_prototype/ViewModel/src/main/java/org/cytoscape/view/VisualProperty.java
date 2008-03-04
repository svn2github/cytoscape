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
package org.cytoscape.view;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.cytoscape.view.parser.*;

/**
 * Enum for calculator types.<br>
 *
 * This will replace public constants defined in VizMapperUI class.<br>
 * This Enum defines visual attributes used in Cytoscape.
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public enum VisualProperty {
	// NODE PROPERTIES
	NODE_FILL_COLOR(Color.class, new ColorParser()),
	NODE_BORDER_COLOR(Color.class, new ColorParser()),
	NODE_OPACITY(Double.class),
	NODE_BORDER_OPACITY(Double.class),
	NODE_BORDER_SIZE(Integer.class),
	NODE_SIZE(Double.class),
	NODE_WIDTH(Double.class),
	NODE_HEIGHT(Double.class),
	NODE_SHAPE(NodeShape.class),
	NODE_LABEL_FONT_FACE(Font.class),
	NODE_LABEL_FONT_SIZE(Integer.class),
	NODE_LABEL_FONT_COLOR(Color.class, new ColorParser()),
	NODE_LABEL_WIDTH(Integer.class),
	NODE_LABEL_POSITION(LabelPosition.class),
	
	NODE_X_POSITION(Double.class),
	NODE_Y_POSITION(Double.class),
	NODE_Z_POSITION(Double.class),
	NODE_T_POSITION(Double.class),
	
	// EDGE PROPERTIES
	EDGE_COLOR(Color.class, new ColorParser()),
	EDGE_LINE_WIDTH(Integer.class),
	EDGE_LINE_TYPE(LineStyle.class),
	EDGE_SRC_ARROW_SHAPE(ArrowShape.class),
	EDGE_SRC_ARROW_COLOR(Color.class, new ColorParser()),
	EDGE_SRC_ARROW_SIZE(Double.class),
	EDGE_SRC_ARROW_OPACITY(Double.class),
	EDGE_TGT_ARROW_SHAPE(ArrowShape.class),
	EDGE_TGT_ARROW_COLOR(Color.class, new ColorParser()),
	EDGE_TGT_ARROW_SIZE(Double.class),
	EDGE_TGT_ARROW_OPACITY(Double.class),
	EDGE_LINE_OPACITY(Double.class),
	EDGE_LABEL_FONT_FACE(Font.class),
	EDGE_LABEL_FONT_SIZE(Integer.class),
	EDGE_LABEL_FONT_COLOR(Color.class),
	EDGE_LABEL_WIDTH(Double.class),
	EDGE_LABEL_POSITION(LabelPosition.class),
	EDGE_CURVE_STYLE(EdgeStyle.class),
	
	BACKGROUND_COLOR(Color.class, new ColorParser()),
	ZOOM_FACTOR(Double.class);
	
	private Class dataClassType;
	private VisualPropertyParser parser;
	
	private VisualProperty(Class dataType, VisualPropertyParser vpp) {
		dataClassType = dataType;
		parser = vpp;
	}
}
