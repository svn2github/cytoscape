/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package csplugins.layout.algorithms.bioLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Random;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.awt.Dimension;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.data.*;
import cytoscape.task.TaskMonitor;
import giny.view.*;

import csplugins.layout.AbstractLayout;

import csplugins.layout.algorithms.bioLayout.LayoutNode;
import csplugins.layout.algorithms.bioLayout.LayoutEdge;
import csplugins.layout.algorithms.bioLayout.Profile;


/**
 * Superclass for the two bioLayout algorithms (KK and FR).
 *
 * @author <a href="mailto:scooter@cgl.ucsf.edu">Scooter Morris</a>
 * @version 0.9
 */

public abstract class BioLayoutAlgorithm extends AbstractLayout {
	/**
	 * Properties
	 */
	private static final String debugProp = "debug";
	private static final String randomizeProp = "randomize";
	private static final String minWeightProp = "min_weight";
	private static final String maxWeightProp = "max_weight";

  /**
   * A small value used to avoid division by zero
	 */
	protected double EPSILON = 0.0000001D;

  /**
   * Enables/disables debugging messages
   */
	private final static boolean DEBUG = false;
	protected static boolean debug = DEBUG; // so we can overload it with a property

  /** 
   * Default attribute to use for edge weighting
   */
	protected String eValueAttribute = "eValue";

	/**
	 * The task monitor that we report to
	 */
	protected TaskMonitor taskMonitor;

	/**
	 * This flag tells us if the user has
	 * has requested a cancel.
	 */
	protected boolean cancel = false;

	/**
	 * The LayoutNode (CyNode) and Edge (CyEdge) arrays.
	 */
	protected static ArrayList nodeList;
 	protected static ArrayList edgeList;

	/**
	 * Minimum and maximum weights.  This is used to
	 * provide a bounds on the weights.
	 */
	protected double minWeightCutoff = 0;
	protected double maxWeightCutoff = Double.MAX_VALUE;
	protected static double logWeightCeiling = 1074;  // Maximum log value (-Math.log(Double.MIN_VALU))

	/**
	 * The computed width and height of the resulting layout
	 */
	protected double width = 0;
	protected double height = 0;

	/**
	 * Whether or not to initialize by randomizing all points
	 */
	protected boolean randomize = true;

	/**
	 * Whether or not to layout all nodes or only selected nodes
	 */
	protected boolean selectedOnly = false;

	/**
	 * This hashmap provides a quick way to get an index into
	 * the LayoutNode array given a graph index.
	 */
	protected HashMap nodeToLayoutNode;

	/**
	 * This is the constructor for the bioLayout algorithm.
	 * @param networkView the CyNetworkView of the network 
	 *                    are going to lay out.
	 */
	public BioLayoutAlgorithm (CyNetworkView networkView, String prefix) {
		super (networkView);
		LayoutEdge e = new LayoutEdge();
		e.reset(); // This allows us to reset the static variables
		LayoutNode v = new LayoutNode();
		v.reset(); // This allows us to reset the static variables
		initializeProperties(prefix);
	}

	/**
	 * Sets the attribute to use for the weights
	 *
	 * @param value the name of the attribute
	 */
	public void setEvalueAttribute(String value) {
		this.eValueAttribute = value;
	}

	/**
	 * Sets the flag that determines if we're to layout all nodes
	 * or only selected nodes.
	 *
	 * @param value the name of the attribute
	 */
	public void setSelectedOnly(boolean value) {
		this.selectedOnly = value;
	}

	public void setSelectedOnly(String value) {
		Boolean val = new Boolean(value);
		selectedOnly = val.booleanValue();
	}

	/**
	 * Sets the debug flag
	 *
	 * @param flag boolean value that turns debugging on or off
	 */
	public void setDebug(boolean flag) {
		debug = flag;
	}

	public void setDebug(String value) {
		Boolean val = new Boolean(value);
		debug = val.booleanValue();
	}

	/**
	 * Sets the randomize flag
	 *
	 * @param flag boolean value that turns initial randomization on or off
	 */
	public void setRandomize(boolean flag) {
		randomize = flag;
	}

	public void setRandomize(String value) {
		Boolean val = new Boolean(value);
		randomize = val.booleanValue();
	}

	/**
	 * Sets the minimum weight cutoff
	 *
	 * @param value minimum weight cutoff
	 */
	public void setMinWeight(double value) {
		this.minWeightCutoff = value;
	}

	public void setMinWeight(String value) {
		Double val = new Double(value);
		this.minWeightCutoff = val.doubleValue();
	}

	/**
	 * Sets the maximum weight cutoff
	 *
	 * @param value maximum weight cutoff
	 */
	public void setMaxWeight(double value) {
		this.maxWeightCutoff = value;
	}

	public void setMaxWeight(String value) {
		Double val = new Double(value);
		this.maxWeightCutoff = val.doubleValue();
	}

	/**
	 * Set the task monitor we're going to use
	 */
	public void setTaskMonitor(TaskMonitor t) {
		this.taskMonitor = t;
	}

	/**
	 * Reads all of our properties from the cytoscape properties map and sets
	 * the values as appropriates.
	 */
	private void initializeProperties(String propPrefix) {
		// Initialize our tunables from the properties
		Properties properties = CytoscapeInit.getProperties();
		String pValue = null;

		// debug
		if ( (pValue = properties.getProperty(propPrefix+debugProp) ) != null ) {
			setDebug(pValue);
		}
		// randomize
		if ( (pValue = properties.getProperty(propPrefix+randomizeProp) ) != null ) {
			setRandomize(pValue);
		}
		// max_weight
		if ( (pValue = properties.getProperty(propPrefix+maxWeightProp) ) != null ) {
			setMaxWeight(pValue);
		}
		// min_weight
		if ( (pValue = properties.getProperty(propPrefix+minWeightProp) ) != null ) {
			setMinWeight(pValue);
		}
	}

	/**
	 * Main entry point for AbstractLayout classes
	 */
	public Object construct() {
		taskMonitor.setStatus("Initializing");
		initialize();  // Calls initialize_local
		layout(); 		 // Abstract -- must be overloaded
		networkView.fitContent();
		networkView.updateView();
		return null;
	}

	/**
	 * Main function that must be implemented by the child class.
	 */
	public abstract void layout();

	/**
	 * Call all of the initializtion code.  Called from
	 * AbstractLayout.initialize().
	 */
	protected void initialize_local() {
		this.nodeToLayoutNode = new HashMap();

		// Get our vertices
		vertexInitialize();

		// Get our edges
		edgeInitialize();
	}

	/**
	 * Initialize the edge array.
	 */
	protected void edgeInitialize() {
		LayoutEdge firstEdge = new LayoutEdge();
		firstEdge.setLogWeightCeiling(logWeightCeiling);

		this.edgeList = new ArrayList(network.getEdgeCount());
		Iterator iter = network.edgesIterator();
		while (iter.hasNext()) {
			CyEdge edge = (CyEdge)iter.next();

			// Make sure we clean up after any previous layouts
			EdgeView ev = networkView.getEdgeView(edge);
			ev.clearBends();

			CyNode source = (CyNode)edge.getSource();
			CyNode target = (CyNode)edge.getTarget();
			if (source == target) 
				continue;

			LayoutNode v1 = (LayoutNode)nodeToLayoutNode.get(source);
			LayoutNode v2 = (LayoutNode)nodeToLayoutNode.get(target);
			// Do we care about this edge?
			if (v1.isLocked() && v2.isLocked())
				continue; // no, ignore it
			LayoutEdge newEdge = new LayoutEdge(edge, v1, v2);
			newEdge.setWeight(eValueAttribute);
			edgeList.add(newEdge);
		}
	}

	/**
	 * Initialize the vertex array.
	 */
	protected void vertexInitialize() {
		int nodeIndex = 0;
		this.nodeList = new ArrayList(network.getNodeCount());
		Set selectedNodes = null;
		Iterator iter = networkView.getNodeViewsIterator();
		if (selectedOnly) {
			selectedNodes = ((CyNetwork)network).getSelectedNodes();
		}
		while (iter.hasNext()) {
			NodeView nv = (NodeView)iter.next();
			CyNode node = (CyNode)nv.getNode();
			LayoutNode v;
			if (selectedNodes != null && !selectedNodes.contains(node)) {
				v = new LayoutNode(nv, nodeIndex, false);
				v.lock();
			} else {
				v = new LayoutNode(nv, nodeIndex, true);
			}
			nodeList.add(v);
			nodeToLayoutNode.put(node,v);
			nodeIndex++;
		}
	}

	/**
	 * Randomize the graph locations.
	 */
	protected void randomizeLocations() {
		// Get a seeded pseudo random-number generator
		Date today = new Date();
		Random random = new Random(today.getTime());
		Iterator iter = nodeList.iterator();
		while (iter.hasNext()) { 
			LayoutNode node = (LayoutNode)iter.next();
			if (!node.isLocked())
				node.setRandomLocation(random); 
		}
		return;
	}

	/**
	 * Calculate and set the edge weights.  Note that this will delete
	 * edges from the calculation (not the graph) when certain conditions
	 * are met.
	 */
	protected void calculateEdgeWeights() {
		// Normalize the weights to between 0 and 1
		ListIterator iter = edgeList.listIterator();
		while (iter.hasNext()) { 
			LayoutEdge edge = (LayoutEdge)iter.next();
			// If we're only dealing with selected nodes, drop any edges
			// that don't have any selected nodes
			if (edge.getSource().isLocked() && edge.getTarget().isLocked()) {
				iter.remove();
			} else if (edge.getWeight() <= minWeightCutoff || edge.getWeight() > maxWeightCutoff) {
			// Drop any edges that are outside of our bounds
				iter.remove();
			} else {
				edge.normalizeWeight();
				// Drop any edges where the normalized weight is small
				if (edge.getWeight() < .001)
					iter.remove();
			}
		}
	}

	/**
	 * Calculate and set the average location of this group of nodes.  This
	 * is used when we are only laying out selected nodes to move the group
	 * back to a starting location.
	 */
	protected Dimension calculateAverageLocation() {
		Dimension result = new Dimension();

		double xAverage = 0;
		double yAverage = 0;
		// Calculate the totals
		int nodeCount = 0;
		Iterator iter = nodeList.iterator();
		while (iter.hasNext()) {
			LayoutNode v = (LayoutNode)iter.next();
			if (v.isLocked()) continue;
			xAverage += v.getX();
			yAverage += v.getY();
			nodeCount++;
		}
		result.setSize(xAverage/nodeCount, yAverage/nodeCount);
		return result;
	}

	protected void print_disp() {
		Iterator iter = nodeList.iterator();
		while (iter.hasNext()) {
			LayoutNode v = (LayoutNode)iter.next();
			debugln("Node "+v.getIdentifier()
								+ " displacement="+v.printDisp());
		}
	}

	public static void debugln(String message) {
		if (debug) { System.err.println(message); }
	}

	public static void debug(String message) {
		if (debug) { System.err.print(message); }
	}

	public void setCancel() {
		this.cancel = true;
	}
}
