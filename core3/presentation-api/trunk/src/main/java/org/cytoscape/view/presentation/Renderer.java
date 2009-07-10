package org.cytoscape.view.presentation;

import org.cytoscape.view.model.VisualLexicon;


/**
 * A function to draw a given network object.
 * Will be used by pluggable-renderer
 * 
 */
public interface Renderer {
	/**
	 * Return a list of visual attributes this renderer can use
	 */
	public VisualLexicon getVisualLexicon();
}
