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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.EdgeView;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.NodeView;

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
public class VisualMappingManagerImpl extends SubjectBaseImpl implements VisualMappingManager {

	// for tracking which styles are applied to which views
	private Map<GraphView,VisualStyle> viewStyleMap = new HashMap<GraphView,VisualStyle>();
	
	// Catalog of visual styles and calculators.
	// This is the actual object to store styles.
	private CalculatorCatalog catalog;
	
	private GraphView networkView; // the object displaying the network
	private VisualStyle activeVS; // the currently active visual style

	// reusable appearance objects
	private NodeAppearance myNodeApp = new NodeAppearance();
	private EdgeAppearance myEdgeApp = new EdgeAppearance();
	private GlobalAppearance myGlobalApp = new GlobalAppearance();

	private static final String DEF_STYLE_NAME = "default";

	/**
	 * Creates a new VisualMappingManager object.
	 *
	 * @param networkView DOCUMENT ME!
	 */
	public VisualMappingManagerImpl() {
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

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#setNetworkView(org.cytoscape.view.GraphView)
	 */
	public void setNetworkView(final GraphView new_view) {
		this.networkView = new_view;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#getNetworkView()
	 */
	public GraphView getNetworkView() {
		return networkView;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#getNetwork()
	 */
	public CyNetwork getNetwork() {
		return networkView.getGraphPerspective();
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#getCalculatorCatalog()
	 */
	public CalculatorCatalog getCalculatorCatalog() {
		return catalog;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#getVisualStyle(java.lang.String)
	 */
	public VisualStyle getVisualStyle(String name) {
		return catalog.getVisualStyle(name);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#getVisualStyle()
	 */
	public VisualStyle getVisualStyle() {
		return activeVS;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#getVisualStyleForView(org.cytoscape.view.GraphView)
	 */
	public VisualStyle getVisualStyleForView( GraphView g ) {
		if ( !viewStyleMap.containsKey(g) ) 
			viewStyleMap.put( g, catalog.getVisualStyle(DEF_STYLE_NAME) );

		return viewStyleMap.get(g);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#setVisualStyleForView(org.cytoscape.view.GraphView, org.cytoscape.vizmap.VisualStyle)
	 */
	public void setVisualStyleForView( GraphView g, VisualStyle vs ) {
		if ( g != null && vs != null )
			viewStyleMap.put(g,vs);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#setVisualStyle(org.cytoscape.vizmap.VisualStyle)
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

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#setVisualStyle(java.lang.String)
	 */
	public VisualStyle setVisualStyle(final String newVSName) {
		final VisualStyle vs = catalog.getVisualStyle(newVSName);

		if (vs != null)
			return setVisualStyle(vs);
		else
			return activeVS;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#applyNodeAppearances()
	 */
	public void applyNodeAppearances() {
		applyNodeAppearances(getNetwork(), getNetworkView());
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#applyNodeAppearances(org.cytoscape.model.CyNetwork, org.cytoscape.view.GraphView)
	 */
	public void applyNodeAppearances(final CyNetwork network, final GraphView network_view) {
		final NodeAppearanceCalculator nodeAppearanceCalculator = activeVS.getNodeAppearanceCalculator();

		for (Iterator i = network_view.getNodeViewsIterator(); i.hasNext();) {
			NodeView nodeView = (NodeView) i.next();
			CyNode node = nodeView.getNode();

			nodeAppearanceCalculator.calculateNodeAppearance(myNodeApp, node, network);
			myNodeApp.applyAppearance(nodeView);
		}
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#applyEdgeAppearances()
	 */
	public void applyEdgeAppearances() {
		applyEdgeAppearances(getNetwork(), getNetworkView());
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#applyEdgeAppearances(org.cytoscape.model.CyNetwork, org.cytoscape.view.GraphView)
	 */
	public void applyEdgeAppearances(final CyNetwork network, final GraphView network_view) {
		final EdgeAppearanceCalculator edgeAppearanceCalculator = activeVS.getEdgeAppearanceCalculator();

		EdgeView edgeView;

		for (Iterator i = network_view.getEdgeViewsIterator(); i.hasNext();) {
			edgeView = (EdgeView) i.next();

			if (edgeView == null)

				// WARNING: This is a hack, edgeView should not be null, but
				// for now do this! (iliana)
				continue;

			edgeAppearanceCalculator.calculateEdgeAppearance(myEdgeApp, edgeView.getEdge(), network);
			myEdgeApp.applyAppearance(edgeView);
		}
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#applyGlobalAppearances()
	 */
	public void applyGlobalAppearances() {
		applyGlobalAppearances(getNetwork(), getNetworkView());
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#applyGlobalAppearances(org.cytoscape.model.CyNetwork, org.cytoscape.view.GraphView)
	 */
	public void applyGlobalAppearances(CyNetwork network, GraphView network_view) {
		GlobalAppearanceCalculator globalAppearanceCalculator = activeVS.getGlobalAppearanceCalculator();
		globalAppearanceCalculator.calculateGlobalAppearance(myGlobalApp, network);

		// setup proper background colors
		network_view.setBackgroundPaint(myGlobalApp.getBackgroundColor());

		// will ignore sloppy & reverse selection color for now

		for ( CyNode node : network.getNodeList() )
			network_view.getNodeView(node).setSelectedPaint(myGlobalApp.getNodeSelectionColor());

		for ( CyEdge edge : network.getEdgeList() )
			network_view.getEdgeView(edge).setSelectedPaint(myGlobalApp.getEdgeSelectionColor());
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#applyAppearances()
	 */
	public void applyAppearances() {
		/** first apply the node appearance to all nodes */
		applyNodeAppearances();
		/** then apply the edge appearance to all edges */
		applyEdgeAppearances();
		/** now apply global appearances */
		applyGlobalAppearances();
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#vizmapNode(org.cytoscape.view.NodeView, org.cytoscape.view.GraphView)
	 */
	public void vizmapNode(NodeView nodeView, GraphView network_view) {
		CyNode node = nodeView.getNode();
		NodeAppearanceCalculator nodeAppearanceCalculator = activeVS.getNodeAppearanceCalculator();
		nodeAppearanceCalculator.calculateNodeAppearance(myNodeApp, node, network_view.getGraphPerspective());
		myNodeApp.applyAppearance(nodeView);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.VisualMappingManager#vizmapEdge(org.cytoscape.view.EdgeView, org.cytoscape.view.GraphView)
	 */
	public void vizmapEdge(EdgeView edgeView, GraphView network_view) {
		CyEdge edge = edgeView.getEdge();
		EdgeAppearanceCalculator edgeAppearanceCalculator = activeVS.getEdgeAppearanceCalculator();
		edgeAppearanceCalculator.calculateEdgeAppearance(myEdgeApp, edge, network_view.getGraphPerspective());
		myEdgeApp.applyAppearance(edgeView);
	}
}
