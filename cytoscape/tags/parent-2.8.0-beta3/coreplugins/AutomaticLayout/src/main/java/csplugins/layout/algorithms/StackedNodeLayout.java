
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

package csplugins.layout.algorithms;

import cytoscape.CyNode;

import cytoscape.layout.AbstractLayout;

import cytoscape.view.CyNetworkView;

import giny.view.NodeView;
import giny.view.NodeView;

import java.util.Collection;
import java.util.Iterator;


/**
 *
 */
public class StackedNodeLayout extends AbstractLayout {
	/**
	 * Puts a collection of nodes into a "stack" layout. This means the nodes are
	 * arranged in a line vertically, with each node overlapping with the previous.
	 *
	 * @param nodes the nodes whose position will be modified
	 * @param x_position the x position for the nodes
	 * @param y_start_position the y starting position for the stack
	 */
	private double y_start_position;
	private double x_position;
	private Collection nodes;

	/**
	 * Creates a new StackedNodeLayout object.
	 *
	 * @param x_position  DOCUMENT ME!
	 * @param y_start_position  DOCUMENT ME!
	 * @param nodes  DOCUMENT ME!
	 */
	public StackedNodeLayout(double x_position, double y_start_position, Collection nodes) {
		super();
		this.x_position = x_position;
		this.y_start_position = y_start_position;
		this.nodes = nodes;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void construct() {
		Iterator it = nodes.iterator();
		double yPosition = y_start_position;

		while (it.hasNext()) {
			CyNode node = (CyNode) it.next();
			NodeView nodeView = networkView.getNodeView(node);
			nodeView.setXPosition(x_position);
			nodeView.setYPosition(yPosition);
			yPosition += (nodeView.getHeight() * 2);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName() {
		return "Stacked Node Layout";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		return getName();
	}
}
