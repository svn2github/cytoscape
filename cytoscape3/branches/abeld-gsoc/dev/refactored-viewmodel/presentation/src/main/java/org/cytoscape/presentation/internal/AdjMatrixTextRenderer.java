package org.cytoscape.presentation.internal;

import org.cytoscape.presentation.TextPresentation;
import org.cytoscape.presentation.TextNodeRenderer;
import org.cytoscape.viewmodel.CyNetworkView;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.viewmodel.Renderer;
import org.cytoscape.viewmodel.View;
import org.cytoscape.model.CyNode;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

/**
 * A TextPresentation that shows network as an Adjacency Matrix
 */
public class AdjMatrixTextRenderer implements TextPresentation, Renderer {
    private final VisualProperty<String> headerText =
	new VisualPropertyImpl<String>("HEADER_TEXT", "text printed as header",
				       "---[ Adjacency Matrix]---", String.class,
				       VisualProperty.GraphObjectType.NETWORK);


    private final VisualProperty<TextNodeRenderer> nodeRenderer =
	new DiscreteVisualProperty<TextNodeRenderer>("TEXT_NODE_RENDERER", "node Renderer",
						     TextNodeRenderer.class,
						     Arrays.asList((TextNodeRenderer)new TextNodeRendererImpl()),
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
	// render each node:
	for (View<CyNode> nodeView: view.getCyNodeViews()){
	    TextNodeRenderer renderer = nodeView.getVisualProperty(nodeRenderer);
	    sb.append("\n"+renderer.render());
	}
	return sb.toString();
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