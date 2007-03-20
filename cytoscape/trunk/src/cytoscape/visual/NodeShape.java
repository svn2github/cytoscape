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
package cytoscape.visual;

import giny.view.NodeView;

/**
 * This is a replacement for ShapeNodeRealizer.java
 *
 * @author kono
 *
 */
public enum NodeShape {
	RECT(NodeView.RECTANGLE, "Rectangle"),
	ROUND_RECT(NodeView.ROUNDED_RECTANGLE, "Round Rectangle"),
	RECT_3D(NodeView.RECTANGLE, "3D Rectabgle"),
	TRAPEZOID(NodeView.RECTANGLE, "Trapezoid"),
	TRAPEZOID_2(NodeView.RECTANGLE, "Trapezoid 2"),
	TRIANGLE(NodeView.TRIANGLE, "Traiangle"),
	PARALLELOGRAM(NodeView.PARALELLOGRAM, "Parallelogram"),
	DIAMOND(NodeView.DIAMOND, "Diamond"),
	ELLIPSE(NodeView.ELLIPSE, "Ellipse"),
	HEXAGON(NodeView.HEXAGON, "Hexagon"),
	OCTAGON(NodeView.OCTAGON, "Octagon");

	private int ginyShape;
	private String name;

	private NodeShape(int ginyShape, String name) {
		this.ginyShape = ginyShape;
		this.name = name;
	}

	/**
	 * Get name of the shape.
	 *
	 * @return DOCUMENT ME!
	 */
	public String getShapeName() {
		return name;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param type
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	@Deprecated
	public static boolean isValidShape(byte type) {
		return isValidShape(values()[type]);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param type
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static boolean isValidShape(NodeShape type) {
		for (NodeShape curType : values()) {
			if (type == curType)
				return true;
		}

		return false;
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
	 * DOCUMENT ME!
	 *
	 * @param ginyType
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static NodeShape getNodeShape(byte ginyType) {
		for (NodeShape shape : values()) {
			if (shape.ginyShape == ginyType)
				return shape;
		}

		// if not found, just return rectangle.
		return RECT;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param shape
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getGinyShape(NodeShape shape) {
		return ginyShape;
	}
}
