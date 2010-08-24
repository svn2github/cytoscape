/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.view.model;

import java.util.Collection;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.GraphObject;

/**
 * 
 * Additional methods for CyNetworkView. Network view should implement BOTH View
 * and CyNetworkView.
 * 
 * Consolidated data structure for graph object views.
 * 
 * @author kono
 * 
 */
public interface CyNetworkView extends View<CyNetwork> {

	/**
	 * Returns a View for a specified Node.
	 * 
	 * @param node
	 *            Node object
	 * 
	 * @return View for the given node object.
	 */
	View<CyNode> getNodeView(final CyNode node);

	/**
	 * Returns a list of Views for all CyNodes in the network.
	 * 
	 * @return List of all node views.
	 */
	Collection<View<CyNode>> getNodeViews();

	/**
	 * Returns a View for a specified Edge.
	 * 
	 * @param n
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	View<CyEdge> getEdgeView(final CyEdge edge);

	/**
	 * Returns a list of Views for all CyEdges in the network.
	 * 
	 * @return DOCUMENT ME!
	 */
	Collection<View<CyEdge>> getEdgeViews();

	/**
	 * Returns a list of all View including those for Nodes, Edges, and Network.
	 * 
	 * @return DOCUMENT ME!
	 */
	Collection<View<? extends GraphObject>> getAllViews();
	
	
	// These are utility methods to fire events to presentation layer.
	public void fitContent();
	public void fitSelected();
	public void updateView();

}
