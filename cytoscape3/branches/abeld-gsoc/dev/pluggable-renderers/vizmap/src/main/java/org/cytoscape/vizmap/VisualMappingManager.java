/*
 File: VisualMappingManager.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.vizmap;

import org.cytoscape.Node;
import org.cytoscape.Edge;
import org.cytoscape.GraphPerspective;

import org.cytoscape.view.EdgeView;
import org.cytoscape.view.NodeView;
import org.cytoscape.view.GraphView;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

//import cytoscape.CytoscapeInit;


/**
 * Top-level class for controlling the visual appearance of nodes and edges
 * according to data attributes, as well as some global visual attributes. This
 * class holds a reference to a NetworkView that displays the network, a
 * CalculatorCatalog that holds the set of known visual styles and calculators,
 * and a current VisualStyle that is used to determine the values of the visual
 * attributes. A Logger is also supplied to report errors.
 * <P>
 *
 * Note that a null VisualStyle is not allowed; this class always provides at
 * least a default object.
 * <P>
 *
 * The key methods are the apply* methods. These methods first recalculate the
 * visual appearances by delegating to the calculators contained in the current
 * visual style. The usual return value of these methods is an Appearance object
 * that contains the visual attribute values; these values are then applied to
 * the network by calling the appropriate set methods in the graph view API.
 * <P>
 */
public class VisualMappingManager extends SubjectBase {

	// for tracking which styles are applied to which views
	private Map<GraphView,VisualStyle> viewStyleMap = new HashMap<GraphView,VisualStyle>();
	
	// Catalog of visual styles and calculators.
	// This is the actual object to store styles.
	private CalculatorCatalog catalog;
	
	private GraphView networkView; // the object displaying the network
	private VisualStyle activeVS; // the currently active visual style

	private static final String DEF_STYLE_NAME = "default";

	/**
	 * Creates a new VisualMappingManager object.
	 *
	 * @param networkView DOCUMENT ME!
	 */
	public VisualMappingManager() {
		this.catalog = CalculatorCatalogFactory.getCalculatorCatalog();
		
		// TODO figure out where to get properties from
//		// Try to find default style name from prop.
//		String defStyle = CytoscapeInit.getProperties().getProperty("defaultVisualStyle");
//
//		if (defStyle == null)
//			defStyle = DEF_STYLE_NAME;

		VisualStyle vs = catalog.getVisualStyle(DEF_STYLE_NAME);

		if (vs == null)
			vs = catalog.getVisualStyle(DEF_STYLE_NAME);

		setVisualStyle(vs);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param new_view DOCUMENT ME!
	 */
	public void setNetworkView(final GraphView new_view) {
		this.networkView = new_view;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphView getNetworkView() {
		return networkView;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphPerspective getNetwork() {
		return networkView.getGraphPerspective();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public CalculatorCatalog getCalculatorCatalog() {
		return catalog;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public VisualStyle getVisualStyle(String name) {
		return catalog.getVisualStyle(name);
	}

	public VisualStyle getVisualStyle() {
		return activeVS;
	}

	public VisualStyle getVisualStyleForView( GraphView g ) {
		if ( !viewStyleMap.containsKey(g) ) 
			viewStyleMap.put( g, catalog.getVisualStyle(DEF_STYLE_NAME) );

		return viewStyleMap.get(g);
	}

	public void setVisualStyleForView( GraphView g, VisualStyle vs ) {
		if ( g != null && vs != null )
			viewStyleMap.put(g,vs);
	}

	/**
	 * Sets a new visual style, and returns the old style. Also fires an event
	 * to attached listeners only if the visual style changes.
	 *
	 * If the argument is null, the previous visual style is simply returned.
	 */
	public VisualStyle setVisualStyle(final VisualStyle vs) {
		
		if ((vs != null) && (vs != activeVS)) {
			VisualStyle tmp = activeVS;
			activeVS = vs;
			fireStateChanged();

			return tmp;
		} else
			return activeVS;
	}

	/**
	 * Sets a new visual style. Attempts to get the style with the given name
	 * from the catalog and pass that to setVisualStyle(VisualStyle). The return
	 * value is the old style.
	 *
	 * If no visual style with the given name is found, no change is made, an
	 * error message is passed to the logger, and null is returned.
	 */
	public VisualStyle setVisualStyle(final String newVSName) {
		final VisualStyle vs = catalog.getVisualStyle(newVSName);

		if (vs != null)
			return setVisualStyle(vs);
		else
			return activeVS;
	}
}
