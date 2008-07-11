package org.cytoscape.view.model;


import java.awt.Stroke;

/**
 * Defines the Stroke used to render a line.  Generally
 * this just means dashes, but could be extended to include
 * other Strokes like parallel lines and the like.
 */
public interface LineStyle extends Saveable {

	/**
	 * The stroke used to define an Edge.
	 */
	public Stroke getStroke();
}
