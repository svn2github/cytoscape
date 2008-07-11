
package org.cytoscape.view.model;

/**
 * Defines the position that a label should be rendered in. 
 * This should probably be separated into two visual properties LabelJustification,
 * and LabelPosition.  Or possibly three, justify, text anchor position, object
 * anchor position.  Or maybe that's forces too many attributes to be set? 
 */
public class LabelPosition {

	/**
	 * Defines the justification of the text within the bounding box used
	 * to draw the text.
	 */
	public enum Justify {
		CENTER,
		LEFT,
		RIGHT,
		;
	}

	/**
	 * Defines a point on a rectangle that is used to anchor a bounding
	 * box. This point is defined for both the bounding box of the rendered 
	 * text and the bounding box that the text is being rendered on. 
	 */
	public enum Location {
		NORTH,
		NORTHEAST,
		EAST,
		SOUTHEAST,
		SOUTH,
		SOUTHWEST,
		WEST,
		NORTHWEST,
		CENTER,
		;
	}
}
