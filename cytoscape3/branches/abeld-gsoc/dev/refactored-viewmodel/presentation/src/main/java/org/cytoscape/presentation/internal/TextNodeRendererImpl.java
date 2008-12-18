package org.cytoscape.presentation.internal;

import java.util.Set;
import java.util.HashSet;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.presentation.TextNodeRenderer;

public class TextNodeRendererImpl implements TextNodeRenderer   {

    public String render(){
	return "oneTextNode";
    }


	/**
	 * Returns the Set of VisualPropertys supported by this Renderer.
	 *
	 * @return  DOCUMENT ME!
	 */
    public Set<VisualProperty> getVisualProperties(){
	return new HashSet<VisualProperty>();
    }

	/**
	 * Given a String, returns a VisualProperty object.
	 *
	 * @param s  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
    public VisualProperty<?> parseVisualProperty(String s){
	throw new RuntimeException("can't happen");
    }

	/**
	 * Returns a string suitable for parsing by {Renderer#parseVisualProperty(String s)}
	 *
	 * @param vp  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
    public String getVisualPropertyString(VisualProperty<?> vp){
	throw new RuntimeException("can't happen");
    }
}
