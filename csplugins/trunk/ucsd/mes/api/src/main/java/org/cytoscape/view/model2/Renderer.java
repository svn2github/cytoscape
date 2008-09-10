package org.cytoscape.view.model2;

import java.util.Set;

/**
 * Should be implemented as a service.  
 */
public interface Renderer {

	/**
	 * Returns the Set of VisualPropertys supported by this Renderer.
	 */
	public Set<VisualProperty> getVisualProperties();

	/**
	 * Given a String, returns a VisualProperty object.
	 */
	public VisualProperty<?> parseVisualProperty(String s);

	/**
	 * Returns a string suitable for parsing by {Renderer#parseVisualProperty(String s)}
	 */
	public String getVisualPropertyString(VisualProperty<?> vp);

	/**
	 * Renders the CyNetworkView.
	 */
	public void draw(CyNetworkView view);
}
