package org.cytoscape.view.presentation;

import org.cytoscape.view.model.VisualLexicon;


/**
 * A function to draw a given network object.
 * Will be used by pluggable-renderer
 * 
 */
public interface Renderer {
	/**
	 * Return a list of Visual Properties which this renderer can draw.
	 * 
	 */
	public VisualLexicon getVisualLexicon();
}
