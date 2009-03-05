/* vim :set ts=2:
  File: AbstractLayout.java

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
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.tunable.ModuleProperties;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.GraphViewFactory;
import org.cytoscape.view.NodeView;
import org.cytoscape.view.ViewChangeEdit;
import org.cytoscape.work.UndoSupport;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The AbstractLayout provides nice starting point for Layouts
 * written for Cytoscape.
 */
abstract public class AbstractLayout implements CyLayoutAlgorithm {
	// Graph Objects and Views
	protected Set<NodeView> staticNodes;
	protected GraphView networkView;
	protected CyNetwork network;

	//
	protected boolean selectedOnly = false;
	protected String edgeAttribute = null;
	protected String nodeAttribute = null;
	protected boolean canceled = false;
	protected Dimension currentSize = new Dimension(20, 20);
	protected HashMap propertyMap = null;  // TODO figure out if this is used in a child somewhere
	protected HashMap savedPropertyMap = null;  // TODO figure out if this is used in a child somewhere
	private ViewChangeEdit undoableEdit;

	// Monitor
	protected TaskMonitor taskMonitor;

	protected static TaskMonitor nullTaskMonitor = new TaskMonitor() {
		public void setProgress(double percent) { }
		public void setStatusMessage(String message) {} 
		public void setTitle(String title) {} 

	};

	// Should definitely be overridden!
	protected String propertyPrefix = "abstract";
	protected UndoSupport undo;

	/**
	 * The Constructor is null
	 */
	public AbstractLayout(UndoSupport undo) {
		this.staticNodes = new HashSet<NodeView>();
		this.undo = undo;
	}

	/**
	 * These abstract methods must be overridden.
	 */
	public abstract void construct();

	/**
	 * getName is used to construct property strings
	 * for this layout.
	 */
	public abstract String getName();

	/**
	 * toString is used to get the user-visible name
	 * of the layout
	 */
	public abstract String toString();

	/**
	 * These methods should be overridden
	 */
	public boolean supportsSelectedOnly() {
		return false;
	}

	/**
	 * Set the flag that indicates that this algorithm
	 * should only operate on the currently selected nodes.
	 *
	 * @param selectedOnly set to "true" if the algorithm should
	 * only apply to selected nodes only
	 */
	public void setSelectedOnly(boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}

	/**
	 * Returns the types of node attributes supported by
	 * this algorithm.  This should be overloaded by the
	 * specific algorithm
	 *
	 * @return the list of supported attribute types, or null
	 * if node attributes are not supported
	 */
	public Set<Class<?>> supportsNodeAttributes() {
		return new HashSet<Class<?>>();
	}

	/**
	 * Returns the types of edge attributes supported by
	 * this algorithm.  This should be overloaded by the
	 * specific algorithm
	 *
	 * @return the list of supported attribute types, or null
	 * if edge attributes are not supported
	 */
	public Set<Class<?>> supportsEdgeAttributes() {
		return new HashSet<Class<?>>();
	}

	/**
	 * Set the name of the attribute to use for attribute
	 * dependent layout algorithms.
	 *
	 * @param attributeName The name of the attribute
	 */
	public void setLayoutAttribute(String attributeName) {
		if (supportsNodeAttributes().size() > 0) {
			nodeAttribute = attributeName;
		} else if (supportsEdgeAttributes().size() > 0) {
			edgeAttribute = attributeName;
		}
	}

	/*
	 * Override this if you want to provide a custom attribute
	 */

	/**
	 * This returns the list of "attributes" that are provided
	 * by an algorithm for internal purposes.  For example,
	 * an edge-weighted algorithmn might seed the list of
	 * attributes with "unweighted".  This should be overloaded
	 * by algorithms that intend to return custom attributes.
	 *
	 * @return A (possibly empty) list of attributes
	 */
	public List<String> getInitialAttributeList() {
		return new ArrayList<String>();
	}

	/**
	 * Returns a JPanel to be used as part of the Settings dialog for this layout
	 * algorithm.
	 *
	 */
	public JPanel getSettingsPanel() {
		return null;
	}

	/**
	 * Property handling -- these must be overridden by any algorithms
	 * that want to use properties or have a settings UI.
	 */
	public void revertSettings() {
	}

	/**
	 * Property handling -- these must be overridden by any algorithms
	 * that want to use properties or have a settings UI.
	 */
	public void updateSettings() {
	}

	/**
	 * Property handling -- these must be overridden by any algorithms
	 * that want to use properties or have a settings UI.
	 */
	public ModuleProperties getSettings () {
		return null;
	}

	/**
	 * doLayout on specified network view.
	 */
	public void doLayout(GraphView nview) {
		doLayout(nview, nullTaskMonitor);
	}

	/**
	 * doLayout on specified network view with specified monitor.
	 */
	public void doLayout(GraphView nview, TaskMonitor monitor) {
		canceled = false;

		networkView = nview;

		// do some sanity checking
		if ( networkView == null )
			return;

		this.network = networkView.getGraphPerspective();

		if (network == null) 
			return;

		if (network.getNodeCount() <= 0)
			return;

		if (monitor == null)
			monitor = nullTaskMonitor;

		taskMonitor = monitor;

		// set up the edit
		undoableEdit = new ViewChangeEdit(networkView, toString() + " Layout", undo);

		// this is overridden by children and does the actual layout
		construct();

		// update the view 
		if (!selectedOnly)
			networkView.fitContent();

		networkView.updateView();

		// post the edit 
		undoableEdit.post();

		// update the __layoutAlgorithm attribute
		CyRow networkAttributes = network.getCyRow(CyNetwork.HIDDEN_ATTRS);
		network.getNetworkCyDataTables().get(CyNetwork.HIDDEN_ATTRS).createColumn("layoutAlgorithm",String.class,false);
		networkAttributes.set("layoutAlgorithm", getName());

		this.network = null;
		this.networkView = null;

	}

	/**
	 * Initializer, calls <tt>intialize_local</tt> to
	 * start construction process.
	 */
	public void initialize() {
		double node_count = (double) network.getNodeCount();
		node_count = Math.sqrt(node_count);
		node_count *= 100;
		currentSize = new Dimension((int) node_count, (int) node_count);
		initialize_local();
	}

	/**
	 * Initializes all local information, and is called immediately
	 * within the <tt>initialize()</tt> process.
	 * The user is responsible for overriding this method
	 * to do any construction that may be necessary:
	 * for example, to initialize local per-edge or
	 * graph-wide data.
	 */
	protected void initialize_local() {
	}

	/**
	 * Lock these nodes (i.e. prevent them from moving).
	 *
	 * @param nodes An array of NodeView's to lock
	 */
	public void lockNodes(NodeView[] nodes) {
		for (int i = 0; i < nodes.length; ++i) {
			staticNodes.add(nodes[i]);
		}
	}

	/**
	 * Lock this node (i.e. prevent it from moving).
	 *
	 * @param v A NodeView to lock
	 */
	public void lockNode(NodeView v) {
		staticNodes.add(v);
	}

	/**
	 * Unlock this node
	 *
	 * @param v A NodeView to unlock
	 */
	public void unlockNode(NodeView v) {
		staticNodes.remove(v);
	}

	protected boolean isLocked(NodeView v) {
		return (staticNodes.contains(v));
	}

	/**
	 * Unlock all nodes
	 */
	public void unlockAllNodes() {
		staticNodes.clear();
	}

	/**
	 * Halt the algorithm.  
	 */
	public void halt() {
		canceled = true;
	}
}
