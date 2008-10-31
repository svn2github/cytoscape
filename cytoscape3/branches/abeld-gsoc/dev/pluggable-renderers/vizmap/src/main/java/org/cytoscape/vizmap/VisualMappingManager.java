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
	private static final String DEF_STYLE_NAME = "default";

	/**
	 * Creates a new VisualMappingManager object.
	 *
	 * @param networkView DOCUMENT ME!
	 */
	public VisualMappingManager() {
		this.catalog = CalculatorCatalogFactory.getCalculatorCatalog();
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

	/** Returns the VisualSTyle used by the give GraphView */
	public VisualStyle getVisualStyleForView( GraphView g ) {
		if ( !viewStyleMap.containsKey(g) ) 
			viewStyleMap.put( g, catalog.getVisualStyle(DEF_STYLE_NAME) );

		return viewStyleMap.get(g);
	}

	/** Sets the visual style of the given GraphView to the given VisualStyle */
	public void setVisualStyleForView( GraphView g, VisualStyle vs ) {
		if ( g != null && vs != null )
			viewStyleMap.put(g,vs);
	}
	/** Sets the visual style of the given GraphView to the given VisualStyle */
	public void setVisualStyleForView( GraphView g, String vsName ) {
		if ( g != null && vsName != null && catalog.getVisualStyle(vsName)!=null){
			viewStyleMap.put(g, catalog.getVisualStyle(vsName));
		}
	}
	
}
