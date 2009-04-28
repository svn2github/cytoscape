package org.cytoscape.view.presentation;

import java.util.Collection;

import org.cytoscape.view.model.VisualProperty;


/**
 * A function to draw a given network object.
 * Will be used by pluggable-renderer
 * 
 */
public interface Renderer {
	/**
	 * Return a list of visual attributes this renderer can use
	 */
	public Collection<VisualProperty<?>> getSupportedVisualProperties();
	
	public String getName();
}
