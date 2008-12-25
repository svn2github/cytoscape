
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package org.cytoscape.presentation.internal;

import java.util.HashSet;
import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.presentation.TextNodeRenderer;
import org.cytoscape.presentation.TextPresentation;
import org.cytoscape.viewmodel.CyNetworkView;
import org.cytoscape.viewmodel.Renderer;
import org.cytoscape.viewmodel.View;
import org.cytoscape.viewmodel.VisualProperty;
import org.osgi.framework.BundleContext;


/**
 * A TextPresentation that shows network as an Adjacency Matrix.
 */
public class AdjMatrixTextRenderer implements TextPresentation, Renderer {
	private static final VisualProperty<String> headerText = new VisualPropertyImpl<String>("HEADER_TEXT",
	                                                                                        "text printed as header",
	                                                                                        "---[ Adjacency Matrix]---",
	                                                                                        String.class,
	                                                                                        VisualProperty.GraphObjectType.NETWORK);

	// FIXME: this should be VisualProperty<>, only temporary hack to print registered renderers
	// FIXME: this should be 'static' too, but then we can't pass in the BundleContext object
	private final DiscreteVisualProperty<TextNodeRenderer> nodeRenderer;
	private CyNetworkView view;
	private Set<VisualProperty<?>> visualProperties;

	/**
	 * Creates a new AdjMatrixTextRenderer object.
	 *
	 * @param view  DOCUMENT ME!
	 * @param bc  DOCUMENT ME!
	 */
	public AdjMatrixTextRenderer(final CyNetworkView view, final BundleContext bc) {
		this.view = view;
		// FIXME: this should be statically intialized, but how do we get the BundleContext then?
		nodeRenderer = new DiscreteVisualProperty<TextNodeRenderer>("TEXT_NODE_RENDERER",
		                                                            "node Renderer",
		                                                            TextNodeRenderer.class,
		                                                            new TextNodeRendererImpl(),
		                                                            VisualProperty.GraphObjectType.NETWORK,
		                                                            bc);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String render() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n " + view.getNetworkView().getVisualProperty(headerText));
		sb.append("\n AdjMatrixTextRenderer for: \n " + view);

		// render each node:
		for (View<CyNode> nodeView : view.getCyNodeViews()) {
			final TextNodeRenderer renderer = nodeView.getVisualProperty(nodeRenderer);
			sb.append("\n" + renderer.render(nodeView));
		}

		/*
		//just testing DiscreteVisualProperty: -- FIXME: move to unittests
		Set<TextNodeRenderer> renderers = nodeRenderer.getValues();
		System.out.println("available nodeRenderers: "+renderers.size());
		System.out.println(renderers);
		*/
		return sb.toString();
	}

	/**
	 * Returns the Set of VisualPropertys supported by this Renderer.
	 *
	 * @return  DOCUMENT ME!
	 */
	public Set<VisualProperty<?>> getVisualProperties() {
		if (visualProperties == null) {
			populateListOfVisualProperties();
		}

		return new HashSet<VisualProperty<?>>(visualProperties);
	}

	private void populateListOfVisualProperties() {
		visualProperties = new HashSet<VisualProperty<?>>();
		visualProperties.add(headerText);
	}

	/**
	 * Given a String, returns a VisualProperty object.
	 *
	 * @param s  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualProperty<?> parseVisualProperty(final String s) {
		throw new RuntimeException("can't happen");
	}

	/**
	 * Returns a string suitable for parsing by {Renderer#parseVisualProperty(String s)}.
	 *
	 * @param vp  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getVisualPropertyString(final VisualProperty<?> vp) {
		throw new RuntimeException("can't happen");
	}
}
