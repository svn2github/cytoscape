
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

package csplugins.layout.algorithms.graphPartition;

import cern.colt.list.*;

import cern.colt.map.*;

import cytoscape.CyNetwork;

import cytoscape.view.CyNetworkView;

import giny.model.*;

import giny.view.*;

import java.util.*;


/**
 * Class that represents the Layout of a given graph.
 */
public class Layout {
	OpenIntDoubleHashMap nodeXMap;
	OpenIntDoubleHashMap nodeYMap;
	CyNetwork gp;

	/**
	 * Creates a new Layout object.
	 *
	 * @param gp  DOCUMENT ME!
	 */
	public Layout(CyNetwork gp) {
		this.gp = gp;
		nodeXMap = new OpenIntDoubleHashMap(PrimeFinder.nextPrime(gp.getNodeCount()));
		nodeYMap = new OpenIntDoubleHashMap(PrimeFinder.nextPrime(gp.getNodeCount()));
	}

	/**
	 * Creates a new Layout object.
	 *
	 * @param view  DOCUMENT ME!
	 * @param load_current_values  DOCUMENT ME!
	 */
	public Layout(CyNetworkView view, boolean load_current_values) {
		this(view.getNetwork());

		// initialize current values
		if (load_current_values) {
			Iterator i = view.getNodeViewsIterator();

			while (i.hasNext()) {
				NodeView nv = (NodeView) i.next();
				setX(nv, nv.getXPosition());
				setY(nv, nv.getYPosition());
			}
		}
	}

	/**
	 * Apply the layout to a given GraphView
	 */
	public void applyLayout(CyNetworkView view) {
		Iterator i = view.getNodeViewsIterator();

		while (i.hasNext()) {
			NodeView nv = (NodeView) i.next();
			nv.setXPosition(getX(nv), false);
			nv.setYPosition(getY(nv), false);
			nv.setNodePosition(true);
		}
	}

	// set
	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setX(int node, double x) {
		return nodeXMap.put(node, x);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setY(int node, double y) {
		return nodeYMap.put(node, y);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setX(Node node, double x) {
		return nodeXMap.put(node.getRootGraphIndex(), x);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setY(Node node, double y) {
		return nodeYMap.put(node.getRootGraphIndex(), y);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setX(NodeView node, double x) {
		return nodeXMap.put(node.getRootGraphIndex(), x);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setY(NodeView node, double y) {
		return nodeYMap.put(node.getRootGraphIndex(), y);
	}

	// get
	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getX(int node) {
		return nodeXMap.get(node);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getY(int node) {
		return nodeYMap.get(node);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getX(Node node) {
		return nodeXMap.get(node.getRootGraphIndex());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getY(Node node) {
		return nodeYMap.get(node.getRootGraphIndex());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getX(NodeView node) {
		return nodeXMap.get(node.getRootGraphIndex());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getY(NodeView node) {
		return nodeYMap.get(node.getRootGraphIndex());
	}
}
