package org.cytoscape.presentation.internal;

import java.util.Set;
import java.util.HashSet;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.viewmodel.View;
import org.cytoscape.viewmodel.Renderer;
import org.cytoscape.presentation.TextNodeRenderer;

public class TextNodeRendererImpl implements TextNodeRenderer, Renderer  {
    private static final VisualProperty<String> nodeLabel =
	new VisualPropertyImpl<String>("NODE_LABEL", "node label (string)",
				       "default label", String.class,
				       VisualProperty.GraphObjectType.NODE);


    public String render(View<?> view){
	String label = view.getVisualProperty(nodeLabel);
	return label;
    }


	/**
	 * Returns the Set of VisualPropertys supported by this Renderer.
	 *
	 * @return  DOCUMENT ME!
	 */
    public Set<VisualProperty> getVisualProperties(){
	Set<VisualProperty> ret = new HashSet<VisualProperty>();
	ret.add(nodeLabel);
	return ret;
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
