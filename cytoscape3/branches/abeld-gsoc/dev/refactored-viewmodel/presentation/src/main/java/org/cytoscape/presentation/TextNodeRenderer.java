package org.cytoscape.presentation;

import java.util.Set;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.viewmodel.Renderer;


/**
 * Should be implemented as a service.
 * 'Renderer' is simply anything that provides VisualProperties.
 * With a 'VisualProperties as annotations' this won't be needed. 
 */
public interface TextNodeRenderer extends Renderer {

    public String render();


	/**
	 * Returns the Set of VisualPropertys supported by this Renderer.
	 *
	 * @return  DOCUMENT ME!
	 */
	public Set<VisualProperty> getVisualProperties();

	/**
	 * Given a String, returns a VisualProperty object.
	 *
	 * @param s  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualProperty<?> parseVisualProperty(String s);

	/**
	 * Returns a string suitable for parsing by {Renderer#parseVisualProperty(String s)}
	 *
	 * @param vp  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getVisualPropertyString(VisualProperty<?> vp);
}
