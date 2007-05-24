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

import giny.view.EdgeView;
import ding.view.DGraphView;
import javax.swing.Icon;
import java.util.Map;
import java.util.HashMap;
import java.awt.Shape;
import cytoscape.visual.ui.icon.*; 

/**
 * Defines arrow shapes.<br>
 * This replaces constants defined in Arrow.java.
 *
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public enum ArrowShape {
	NONE("No Arrow", "NONE", EdgeView.NO_END, 
	     new int[]{EdgeView.NO_END}),
	DIAMOND("Diamond", "COLOR_DIAMOND", EdgeView.EDGE_COLOR_DIAMOND,
	     new int[]{EdgeView.EDGE_COLOR_DIAMOND, EdgeView.WHITE_DIAMOND,EdgeView.BLACK_DIAMOND}),
	DELTA("Delta", "COLOR_DELTA", EdgeView.EDGE_COLOR_DELTA,
	     new int[]{EdgeView.EDGE_COLOR_DELTA, EdgeView.WHITE_DELTA,EdgeView.BLACK_DELTA}),
	ARROW("Arrow", "COLOR_ARROW", EdgeView.EDGE_COLOR_ARROW,
	     new int[]{EdgeView.EDGE_COLOR_ARROW, EdgeView.WHITE_ARROW,EdgeView.BLACK_ARROW}),
	T("T", "COLOR_T", EdgeView.EDGE_COLOR_T,
	     new int[]{EdgeView.EDGE_COLOR_T, EdgeView.WHITE_T,EdgeView.BLACK_T}),
	CIRCLE("Circle", "COLOR_CIRCLE", EdgeView.EDGE_COLOR_CIRCLE,
	     new int[]{EdgeView.EDGE_COLOR_CIRCLE, EdgeView.WHITE_CIRCLE,EdgeView.BLACK_CIRCLE}),

	// Not yet implemented
	REVERSE_ARROW("Reverse Arrow", "REVERSE_ARROW", -1,
	     new int[]{-1}),
	;


	private static Map<Integer,Shape> arrowShapes = DGraphView.getArrowShapes();

	private String shapeName;
	private String ginyShapeName;
	private int ginyType;
	private int[] possibleGinyTypes;

	private ArrowShape(String shapeName, String ginyShapeName, int ginyType, int[] possibleGinyTypes) {
		this.shapeName = shapeName;
		this.ginyShapeName = ginyShapeName;
		this.ginyType = ginyType;
		this.possibleGinyTypes = possibleGinyTypes;
	}

	/**
	 * Returns arrow type in GINY.
	 *
	 * @return
	 */
	public int getGinyArrow() {
		return ginyType;
	}

	/**
	 * Returns name of arrow shape.
	 *
	 * @return
	 */
	public String getGinyName() {
		return ginyShapeName;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getName() {
		return shapeName;
	}

	/**
	 *
	 * @param text
	 * @return
	 */
	public static ArrowShape parseArrowText(String text) {
		return valueOf(text);
	}

	public int[] getPossibleGinyTypes() {
		return possibleGinyTypes;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param ginyType
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static ArrowShape getArrowShape(int ginyType) {
		// first try for an exact match
		for (ArrowShape shape : values()) {
			if (shape.getGinyArrow() == ginyType)
				return shape;
		}

		// if no exact match is found, then try the possible ginyTypes 
		for (ArrowShape shape : values()) {
			for ( int possible : shape.getPossibleGinyTypes() ) {
				if ( possible == ginyType ) 
					return shape;
			}
		}

		// if we can't match anything, just return rectangle.
		return NONE;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Shape getShape() {
		return arrowShapes.get(ginyType);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param size DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Map<Object, Icon> getIconSet() {
		Map<Object, Icon> arrowShapeIcons = new HashMap<Object, Icon>();

		for (ArrowShape shape : values()) {
			ArrowIcon icon = new ArrowIcon(arrowShapes.get(shape.getGinyArrow()), 
			                               VisualPropertyIcon.DEFAULT_ICON_SIZE, 
			                               VisualPropertyIcon.DEFAULT_ICON_SIZE, 
										   shape.getName());
			arrowShapeIcons.put(shape, icon);
		}

		return arrowShapeIcons;
	}
}
