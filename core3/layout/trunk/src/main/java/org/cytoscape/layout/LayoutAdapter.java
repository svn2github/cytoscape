/* vim :set ts=2:
  File: LayoutAdapter.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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
package org.cytoscape.layout;

import org.cytoscape.work.TaskMonitor;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyNode;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * LayoutAdapter provides a *very* minimal interface to implement a layout
 * algorithm.  It is meant to be used by graph readers and plugins that provide
 * their own node placement and whose layout approach should not be made
 * available outside of the reader or plugin's context.
 */
abstract public class LayoutAdapter implements CyLayoutAlgorithm {

	/**
	 * This method performs the layout on the current network.
	 *
	 * @param networkView the CyNetworkView on which to perform the layout
	 */
	public void doLayout(CyNetworkView networkView) {
		doLayout(networkView, null);
	}

	/**
	 * This method performs the layout on the current network, but assumes
	 * that the layout is part of an existing monitored task
	 *
	 * @param networkView the CyNetworkView on which to perform the layout
	 * @param monitor the task monitor to use
	 */
	public abstract void doLayout(CyNetworkView networkView, TaskMonitor monitor);

	/**
	 * Get the name of this layout.
	 *
	 * @return String representing the name of the layout.
	 */
	public String getName() {return "LayoutAdaptor";}

	/**
	 * Tests to see if this layout supports doing a layout on a subset of the
	 * nodes in this network view.
	 *
	 * @return true if layout supports layouts on a subset of the nodes
	 */
	public boolean supportsSelectedOnly() {return false;}

	/**
	 * Sets the "selectedOnly" flag
	 *
	 * @param selectedOnly boolean value that tells the layout algorithm whether to
	 * only layout the selected nodes
	 */
	public void setSelectedOnly(boolean selectedOnly) {}

	/**
	 * Tests to see if this layout supports doing a layout based on node attributes.
	 *
	 * @return byte array of allowable attribute types or "null" if not supported.  If the
	 *              first type is "-1", all types are supported
	 */
	public Set<Class<?>> supportsNodeAttributes() { return new HashSet<Class<?>>(); }

	/**
	 * Tests to see if this layout supports doing a layout based on edge attributes.
	 *
	 * @return type array of allowable attribute types or "null" if not supported.  If the
	 *              first type is "-1", all types are supported
	 */
	public Set<Class<?>> supportsEdgeAttributes() { return new HashSet<Class<?>>(); }

	/**
	 * Sets the attribute to use for node- or edge- based attribute layouts
	 *
	 * @param attributeName String with the name of the attribute to use
	 */
	public void setLayoutAttribute(String attributeName) {}

	/**
	 * This returns a (possibly empty) List of Strings that is used for
	 * the attribute list in the menu for attribute-based layouts.  This
	 * allows layout algorithms to provide "special" attributes.  For example,
	 * a force directed layout might want to set the list to ["(unweighted)"]
	 * to allow the user to perform an unweighted layout.  Note that this value
	 * will be set using setLayoutAttribute() just like other attributes, so the
	 * layout algorithm will need to check for it.
	 *
	 * @return List of Strings
	 */
	public List<String> getInitialAttributeList() {return new ArrayList<String>();}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodes DOCUMENT ME!
	 */
	public void lockNodes(View<CyNode>[] nodes) {}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 */
	public void lockNode(View<CyNode> v) {}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 */
	public void unlockNode(View<CyNode> v) {}

	/**
	 *  DOCUMENT ME!
	 */
	public void unlockAllNodes() {}

	/**
	 *  DOCUMENT ME!
	 */
	public void halt() {}
}
