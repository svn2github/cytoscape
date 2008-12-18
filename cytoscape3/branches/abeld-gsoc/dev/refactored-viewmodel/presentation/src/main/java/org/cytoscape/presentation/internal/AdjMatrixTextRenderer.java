package org.cytoscape.presentation.internal;

import org.cytoscape.presentation.TextPresentation;
import org.cytoscape.viewmodel.CyNetworkView;
import org.cytoscape.viewmodel.VisualProperty;

import java.util.HashSet;
import java.util.Set;
/**
 * A TextPresentation that shows network as an Adjacency Matrix
 */
public class AdjMatrixTextRenderer implements TextPresentation, Renderer {
    private CyNetworkView view;
    public AdjMatrixTextRenderer(CyNetworkView view){
	this.view = view;
    }
    public String render(){
	return "AdjMatrixTextRenderer for: "+view;
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
	throw new Exception("can't happen");
    }

	/**
	 * Returns a string suitable for parsing by {Renderer#parseVisualProperty(String s)}
	 *
	 * @param vp  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
    public String getVisualPropertyString(VisualProperty<?> vp){
	throw new Exception("can't happen");
    }
}