package org.cytoscape.presentation.internal;

import org.cytoscape.presentation.TextPresentation;
import org.cytoscape.viewmodel.CyNetworkView;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.viewmodel.Renderer;

import java.util.HashSet;
import java.util.Set;
/**
 * A TextPresentation that shows network as an Adjacency Matrix
 */
public class AdjMatrixTextRenderer implements TextPresentation, Renderer {
    private final VisualProperty<String> headerText =
	new VisualPropertyImpl<String>("HEADER_TEXT", "text printed as header",
				       "---[ Adjacency Matrix]---", String.class,
				       VisualProperty.GraphObjectType.NETWORK);
    private CyNetworkView view;
    private Set<VisualProperty> visualProperties = null;
    public AdjMatrixTextRenderer(CyNetworkView view){
	this.view = view;
    }
    public String render(){
	StringBuilder sb = new StringBuilder();
	sb.append("\n "+view.getNetworkView().getVisualProperty(headerText));
	sb.append("\n AdjMatrixTextRenderer for: \n "+view);
	return sb.toString();
	//return "AdjMatrixTextRenderer for: \n "+view;
    }


	/**
	 * Returns the Set of VisualPropertys supported by this Renderer.
	 *
	 * @return  DOCUMENT ME!
	 */
    public Set<VisualProperty> getVisualProperties(){
	if (visualProperties == null){
	    populateListOfVisualProperties();
	}
	return new HashSet<VisualProperty>(visualProperties);
    }
    private void populateListOfVisualProperties(){
	visualProperties = new HashSet<VisualProperty>();
	visualProperties.add(headerText);

	/* FIXME: define text renderer
	visualProperties.add(new DiscreteVisualProperty("TEXT_NODE_RENDERER", TextNodeRenderer.class,
							true, range, null);
	*/
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