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
package cytoscape.layout;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.init.CyInitParams;

import cytoscape.task.TaskMonitor;

import cytoscape.util.*;

import cytoscape.view.CyNetworkView;

import ding.view.DGraphView;
import ding.view.ViewChangeEdit;

import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JPanel;


/**
 * The AbstractLayout provides nice starting point for Layouts
 * written for Cytoscape.
 */
abstract public class AbstractLayout implements CyLayoutAlgorithm {
	protected Set<NodeView> staticNodes;
	protected CyNetworkView networkView;
	protected CyNetwork network;
	protected TaskMonitor taskMonitor; 
	protected boolean selectedOnly = false;
	protected String edgeAttribute = null;
	protected String nodeAttribute = null;
	protected boolean canceled = false;
	protected Dimension currentSize = new Dimension(20, 20);
	protected HashMap propertyMap = null;
	protected HashMap savedPropertyMap = null;
	private ViewChangeEdit undoableEdit;

	protected static TaskMonitor nullTaskMonitor = new TaskMonitor() {
		public void setPercentCompleted(int percent) {}
		public void setEstimatedTimeRemaining(long time) throws IllegalThreadStateException {}
		public void setException(Throwable t, String userErrorMessage) {}
  	        public void setException(Throwable t, String userErrorMessage, String recoveryTip) {}
		public void setStatus(String message) throws IllegalThreadStateException, NullPointerException {}
	};

	// Should definitely be overridden!
	protected String propertyPrefix = "abstract";

	/**
	 * The Constructor is null
	 */
	public AbstractLayout() {
		this.staticNodes = new HashSet();
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
	public byte[] supportsNodeAttributes() {
		return null;
	}

	/**
	 * Returns the types of edge attributes supported by
	 * this algorithm.  This should be overloaded by the
	 * specific algorithm
	 *
	 * @return the list of supported attribute types, or null
	 * if edge attributes are not supported
	 */
	public byte[] supportsEdgeAttributes() {
		return null;
	}

	/**
	 * Set the name of the attribute to use for attribute
	 * dependent layout algorithms.
	 *
	 * @param attributeName The name of the attribute
	 */
	public void setLayoutAttribute(String attributeName) {
		if (supportsNodeAttributes() != null) {
			nodeAttribute = attributeName;
		} else if (supportsEdgeAttributes() != null) {
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
		return new ArrayList();
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
	public LayoutProperties getSettings () {
		return null;
	}

	/**
	 * doLayout on current network view.
	 */
	public void doLayout() {
		doLayout(Cytoscape.getCurrentNetworkView(),nullTaskMonitor);
	}

	/**
	 * doLayout on specified network view.
	 */
	public void doLayout(CyNetworkView nview) {
		doLayout(nview,nullTaskMonitor);
	}

	/**
	 * doLayout on specified network view with specified monitor.
	 */
	public void doLayout(CyNetworkView nview, TaskMonitor monitor) {

		canceled = false;

		networkView = nview;

		// do some sanity checking
		if (networkView == null || networkView == Cytoscape.getNullNetworkView())
			return;

		this.network = networkView.getNetwork();

		if (network == null || network == Cytoscape.getNullNetwork()) 
			return;

		if (network.getNodeCount() <= 0) 
			return;

		if ( monitor == null )
			monitor = nullTaskMonitor;

		taskMonitor = monitor;

		// set up the edit
		undoableEdit = new ViewChangeEdit((DGraphView)networkView, toString() + " Layout");

		// this is overridden by children and does the actual layout
		construct();

		// update the view 
		if (!selectedOnly)
			networkView.fitContent();

		networkView.updateView();

		// post the edit 
		undoableEdit.post();
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
