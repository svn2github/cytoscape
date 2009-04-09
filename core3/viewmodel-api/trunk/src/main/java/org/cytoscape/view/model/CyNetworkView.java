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

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.GraphObject;

import java.util.List;
import java.util.Set;


/**
 * Contains the visual representation of a Network.
 */
public interface CyNetworkView extends View<CyNetwork>{

	/**
	 * Returns a View for a specified Node.
	 *
	 * @param n  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	View<CyNode> getNodeView(CyNode n);

	/**
	 * Returns a list of Views for all CyNodes in the network.
	 *
	 * @return  DOCUMENT ME!
	 */
	List<View<CyNode>> getNodeViews();

	/**
	 * Returns a View for a specified Edge.
	 *
	 * @param n  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	View<CyEdge> getEdgeView(CyEdge n);

	/**
	 * Returns a list of Views for all CyEdges in the network.
	 *
	 * @return  DOCUMENT ME!
	 */
	List<View<CyEdge>> getEdgeViews();

	/**
	 * Returns a list of all View including those for Nodes, Edges, and Network.
	 *
	 * @return  DOCUMENT ME!
	 */
	List<View<? extends GraphObject>> getAllViews();

	<T> ViewColumn<T> getColumn(final VisualProperty<? extends T> vp);


	// temp methods
	void fitContent();
	void fitSelected();
	void updateView();

	/**
	 * Returns the given subset.
	 *
	 * @param name name of the subset to return
	 * @return the subset
	 */
	Set<View<? extends GraphObject>> getSubset(String name);

	/**
	 * If subset already exists, replaces it with given Set.
	 *
	 * @param name name of the subset
	 * @param subset the Views the subset will contain
	 */
	void createSubset(String name, Set<View<?extends GraphObject>> subset);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 * @param toAdd DOCUMENT ME!
	 */
	void addToSubset(String name, Set<View<?extends GraphObject>> toAdd);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 * @param toRemove DOCUMENT ME!
	 */
	void removeFromSubset(String name, Set<View<?extends GraphObject>> toRemove);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param name DOCUMENT ME!
	 */
	void deleteSubset(String name);
}
