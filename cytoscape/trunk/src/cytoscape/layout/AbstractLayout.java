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

import cytoscape.util.*;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.layout.LayoutAlgorithm;

import cytoscape.view.CyNetworkView;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;

import giny.view.GraphView;
import giny.view.NodeView;
import giny.view.EdgeView;
import java.awt.Dimension;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ArrayList;
import javax.swing.JPanel;


/**
 * The AbstractLayout provides nice starting point for Layouts
 * written for Cytoscape.
 */
abstract public class AbstractLayout 
		implements LayoutAlgorithm, Task {

  protected Set<NodeView> staticNodes;
  protected CyNetworkView networkView;
  protected CyNetwork network;
	protected TaskMonitor taskMonitor;
	protected boolean selectedOnly = false;
	protected String edgeAttribute = null;
	protected String nodeAttribute = null;
	protected boolean canceled = false;
	protected Dimension currentSize = new Dimension(20,20);
	protected HashMap propertyMap = null;
	protected HashMap savedPropertyMap = null;
	// Should definitely be overridden!
	protected String propertyPrefix = "abstract";

  /**
   * The Constructor is null 
   */
  public AbstractLayout () {
    this.staticNodes = new HashSet();
  }

	/**
	 * These abstract methods must be overridden.
	 */
  public abstract void construct ();

	/**
	 * getName is used to construct property strings
	 * for this layout.
	 */
  public abstract String getName ();

	/**
	 * toString is used to get the user-visible name
	 * of the layout
	 */
  public abstract String toString ();

	/**
	 * These methods should be overridden
	 */
	public boolean supportsSelectedOnly () {return false;}
	public void setSelectedOnly (boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}

	public byte[] supportsNodeAttributes () {return null;}
	public byte[] supportsEdgeAttributes () {return null;}

	public void setLayoutAttribute (String attributeName) {
		if (supportsNodeAttributes() != null) {
			nodeAttribute = attributeName;
		} else if (supportsEdgeAttributes() != null) {
			edgeAttribute = attributeName;
		} 
	}

	/*
	 * Override this if you want to provide a custom attribute
	 */
	public List<String> getInitialAttributeList () {
		return new ArrayList();
	}

	/**
	 * Returns a JPanel to be used as part of the Settings dialog for this layout
	 * algorithm.
	 *
	 */
	public JPanel getSettingsPanel() { return null; }

	/**
	 * Property handling -- these must be overridden by any algorithms
	 * that want to use properties or have a settings UI.
	 */
	public void revertSettings () { 
	}

	/**
	 * Property handling -- these must be overridden by any algorithms
	 * that want to use properties or have a settings UI.
	 */
	public void updateSettings () { }

	/**
	 * doLayout
	 */
	public void doLayout( CyNetworkView networkView ) {
		canceled = false;
		this.networkView = networkView;
		this.network = networkView.getNetwork();

		// Do some sanity checking
		if (networkView == null || network == null)
			return; // nothing to layout
		if (network.getNodeCount() <= 0)
			return;

		// Call the layout
		TaskManager.executeTask(this, getNewDefaultTaskConfig());
	}

	/**
	 * doLayout
	 */
	public void doLayout( CyNetworkView networkView, TaskMonitor monitor ) {
		canceled = false;
		this.networkView = networkView;
		this.network = networkView.getNetwork();
		this.taskMonitor = monitor;

		// Do some sanity checking
		if (networkView == null || network == null)
			return; // nothing to layout
		if (network.getNodeCount() <= 0)
			return;

		// Call the layout
		construct();
		networkView.fitContent();
		networkView.updateView();
	}

	public void doLayout() {
		doLayout(Cytoscape.getCurrentNetworkView());
	}
		

	/**
	 * Initializer, calls <tt>intialize_local</tt> to
   * start construction process.
	 */
	public void initialize () {
		double node_count = ( double ) network.getNodeCount();
		node_count = Math.sqrt( node_count );
		node_count *= 100;
		currentSize = new Dimension( (int) node_count, (int) node_count );
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
	protected void initialize_local () {}

  public void lockNodes ( NodeView[] nodes ) {
		for (int i = 0; i < nodes.length; ++i) {
			staticNodes.add( nodes[i] );
		}
	}

  public void lockNode ( NodeView v ) {
		staticNodes.add(v);
	}

  public void unlockNode( NodeView v ) {
		staticNodes.remove(v);
	}

	protected boolean isLocked ( NodeView v ) {
		return (staticNodes.contains(v));
	}

	public void unlockAllNodes () {
		staticNodes.clear();
	}

	/**
	 * Implements Task
	 */

	public void setTaskMonitor (TaskMonitor monitor) {
		taskMonitor = monitor;
	}

	public void run() {
		construct();
		networkView.fitContent();
		networkView.updateView();
	}

	public void halt() { canceled = true; }

	public String getTitle() {
		return "Performing "+toString();
	}

	/**
	 * This method returns a default TaskConfig object
	 */
	protected JTaskConfig getNewDefaultTaskConfig()
  {
    JTaskConfig result = new JTaskConfig();

    result.displayCancelButton(true);
    result.displayCloseButton(false);
    result.displayStatus(true);
    result.displayTimeElapsed(false);
    result.setAutoDispose(true);
    result.setModal(true);
    result.setOwner(Cytoscape.getDesktop());
    return result;
  }
}
