package org.cytoscape.view.renderers;

import java.util.Collection;

import org.cytoscape.view.VisualProperty;

public interface Renderer {
	/**
	 * Return a list of visual attributes this renderer can use
	 */
	public Collection<VisualProperty> supportedVisualAttributes();
	public String name();
}
