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
package org.cytoscape.ding;

import java.awt.Shape;
import java.util.Map;

import org.cytoscape.graph.render.immed.GraphGraphics;

/**
 * Defines node shapes used in DING rendering engine.
 * This enum wires shape definitions to actual rendering code in graph-render bundle.
 *
 */
public enum NodeShape {

	RECT(GraphGraphics.SHAPE_RECTANGLE, "Rectangle"),
	ROUND_RECT(GraphGraphics.SHAPE_ROUNDED_RECTANGLE, "Round Rectangle"),
	TRIANGLE(GraphGraphics.SHAPE_TRIANGLE, "Triangle"),
	PARALLELOGRAM(GraphGraphics.SHAPE_PARALLELOGRAM, "Parallelogram"),
	DIAMOND(GraphGraphics.SHAPE_DIAMOND, "Diamond"),
	ELLIPSE(GraphGraphics.SHAPE_ELLIPSE, "Ellipse"),
	HEXAGON(GraphGraphics.SHAPE_HEXAGON, "Hexagon"),
	OCTAGON(GraphGraphics.SHAPE_OCTAGON, "Octagon");

	private final Byte rendererShapeID;
	private final String displayName;
	
	private static final Map<Byte, Shape> nodeShapes = GraphGraphics.getNodeShapes();

	private NodeShape(final Byte rendererShapeID, final String displayName) {
		this.rendererShapeID = rendererShapeID;
		this.displayName = displayName;
	}
	
	
	/**
	 * Display name will be used for toString() method.
	 */
	@Override public String toString() {
		return displayName;
	}
	
	public Byte getNativeShape() {
		return this.rendererShapeID;
	}


	/**
	 * Get a node shape from a given string.
	 *
	 * @param text DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static NodeShape parseNodeShapeText(final String text) {
		String trimed = text.trim();

		for (NodeShape shape : values()) {
			if (getNodeShapeText(shape).equalsIgnoreCase(trimed))
				return shape;
		}

		// Unknown shape: return rectangle.
		return NodeShape.RECT;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static String[] valuesAsString() {
		final int length = values().length;
		final String[] nameArray = new String[length];

		for (int i = 0; i < length; i++)
			nameArray[i] = values()[i].getShapeName();

		return nameArray;
	}

	/**
	 * Get name of the shape.
	 *
	 * @return DOCUMENT ME!
	 */
	public String getShapeName() {
		return displayName;
	}


	/**
	 * DOCUMENT ME!
	 *
	 * @param shape
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static String getNodeShapeText(NodeShape shape) {
		String nstext = shape.name();
		nstext = nstext.replaceAll("_", "");

		return nstext.toLowerCase();
	}

	
	/**
	 * Convert from Giny shape to Cytoscape NodeShape enum.
	 *
	 * @param ginyShape
	 * @return
	 */
	public static NodeShape getNodeShape(final Byte ginyShape) {
		for (NodeShape shape : values()) {
			if (shape.rendererShapeID == ginyShape)
				return shape;
		}

		// Unknown. Return rectangle as the def val.
		return NodeShape.RECT;
	}
	

	/**
	 * Returns a Shape object for the NodeShape in question.
	 */
	public Shape getShape() {
		return nodeShapes.get(rendererShapeID);
	}

//	/**
//	 *  DOCUMENT ME!
//	 *
//	 * @return  DOCUMENT ME!
//	 */
//	public static Map<Object, Icon> getIconSet() {
//		Map<Object, Icon> nodeShapeIcons = new HashMap<Object, Icon>();
//
//		for (NodeShape shape : values()) {
//			NodeIcon icon = new NodeIcon(nodeShapes.get(shape.getGinyShape()),
//			                             VisualPropertyIcon.DEFAULT_ICON_SIZE,
//			                             VisualPropertyIcon.DEFAULT_ICON_SIZE, shape.getShapeName());
//			nodeShapeIcons.put(shape, icon);
//		}
//
//		return nodeShapeIcons;
//	}
}
