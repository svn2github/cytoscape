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

import csplugins.layout.LayoutNode;
import csplugins.layout.LayoutPartition;
import csplugins.layout.Profile;

import cytoscape.*;

import cytoscape.data.*;

import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;

import cytoscape.util.*;

import cytoscape.view.*;

import giny.view.*;

import java.awt.GridLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JPanel;


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
	private static final int DEBUGPROP = 0;
	private static final int RANDOMIZE = 1;
	private static final int MINWEIGHT = 2;
	private static final int MAXWEIGHT = 3;
	private static final int SELECTEDONLY = 4;
	private static final int LAYOUTATTRIBUTE = 5;
	LayoutProperties layoutProperties;

	/**
	 * A small value used to avoid division by zero
	   */
	protected double EPSILON = 0.0000001D;

	/**
	 * Value to set for doing unweighted layouts
	 */
	public static final String UNWEIGHTEDATTRIBUTE = "(unweighted)";

	/**
	 * Enables/disables debugging messages
	 */
	private final static boolean DEBUG = false;
	protected static boolean debug = DEBUG; // so we can overload it with a property

	/**
	 * Minimum and maximum weights.  This is used to
	 * provide a bounds on the weights.
	 */
	protected double minWeightCutoff = 0;
	protected double maxWeightCutoff = Double.MAX_VALUE;

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
	 * Whether or not to use edge weights for layout
	 */
	protected boolean supportWeights = true;

	/**
	 * Whether or not to partition the graph before layout
	 */
	protected boolean partitionGraph = true;

	/**
	 * The list of partitions in this graph
	 */
	protected List<LayoutPartition> partitionList;

	/**
	 * This is the constructor for the bioLayout algorithm.
	 */
	public BioLayoutAlgorithm() {
		super();
		layoutProperties = new LayoutProperties(getName());
	}

	// We do support selected only
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean supportsSelectedOnly() {
		return true;
	}

	// We don't support node attribute-based layouts
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte[] supportsNodeAttributes() {
		return null;
	}

	// We do support edge attribute-based layouts
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte[] supportsEdgeAttributes() {
		if (!supportWeights)
			return null;

		byte[] attrs = { CyAttributes.TYPE_INTEGER, CyAttributes.TYPE_FLOATING };

		return attrs;
	}

	/**
	 * Sets the attribute to use for the weights
	 *
	 * @param value the name of the attribute
	 */
	public void setLayoutAttribute(String value) {
		if ((value == null) || value.equals(UNWEIGHTEDATTRIBUTE)) {
			edgeAttribute = null;
		} else {
			edgeAttribute = value;
		}
	}

	/**
	 * Returns "(unweighted)", which is the "attribute" we
	 * use to tell the algorithm not to use weights
	 *
	 * @returns List of our "special" weights
	 */
	public List<String> getInitialAttributeList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(UNWEIGHTEDATTRIBUTE);

		return list;
	}

	/**
	 * Sets the flag that determines if we're to layout all nodes
	 * or only selected nodes.
	 *
	 * @param value the name of the attribute
	 */
	public void setSelectedOnly(boolean value) {
		// Inherited by AbstractLayout
		selectedOnly = value;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 */
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

	/**
	 *  DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 */
	public void setDebug(String value) {
		Boolean val = new Boolean(value);
		debug = val.booleanValue();
	}

	/**
	 * Sets the partition flag
	 *
	 * @param flag boolean value that turns initial randomization on or off
	 */
	public void setPartition(boolean flag) {
		partitionGraph = flag;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 */
	public void setPartition(String value) {
		Boolean val = new Boolean(value);
		partitionGraph = val.booleanValue();
	}

	/**
	 * Sets the randomize flag
	 *
	 * @param flag boolean value that turns initial randomization on or off
	 */
	public void setRandomize(boolean flag) {
		randomize = flag;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 */
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

	/**
	 *  DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 */
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

	/**
	 *  DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 */
	public void setMaxWeight(String value) {
		Double val = new Double(value);
		this.maxWeightCutoff = val.doubleValue();
	}

	/**
	 * Reads all of our properties from the cytoscape properties map and sets
	 * the values as appropriates.
	 */
	protected void initializeProperties() {
		layoutProperties.add(new Tunable("debug", "Enable debugging", Tunable.BOOLEAN,
		                                 new Boolean(false), Tunable.NOINPUT));
		layoutProperties.add(new Tunable("partition", "Partition graph before layout",
		                                 Tunable.BOOLEAN, new Boolean(true)));
		layoutProperties.add(new Tunable("randomize", "Randomize graph before layout",
		                                 Tunable.BOOLEAN, new Boolean(true)));
		layoutProperties.add(new Tunable("min_weight", "The minimum edge weight to consider",
		                                 Tunable.DOUBLE, new Double(0)));
		layoutProperties.add(new Tunable("max_weight", "The maximum edge weight to consider",
		                                 Tunable.DOUBLE, new Double(Double.MAX_VALUE)));
		layoutProperties.add(new Tunable("selected_only", "Only layout selected nodes",
		                                 Tunable.BOOLEAN, new Boolean(false)));
		layoutProperties.add(new Tunable("edge_attribute",
		                                 "The edge attribute that contains the weights",
		                                 Tunable.EDGEATTRIBUTE, "weight",
		                                 (Object) getInitialAttributeList(), (Object) null,
		                                 Tunable.NUMERICATTRIBUTE));
	}

	/**
	 * Get the settings panel for this layout
	 */
	public JPanel getSettingsPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(layoutProperties.getTunablePanel());

		return panel;
	}

	public LayoutProperties getSettings() {
		return layoutProperties;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void updateSettings() {
		updateSettings(false);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param force DOCUMENT ME!
	 */
	public void updateSettings(boolean force) {
		layoutProperties.updateValues();

		Tunable t = layoutProperties.get("debug");

		if ((t != null) && (t.valueChanged() || force))
			setDebug(t.getValue().toString());

		t = layoutProperties.get("partition");

		if ((t != null) && (t.valueChanged() || force))
			setPartition(t.getValue().toString());

		t = layoutProperties.get("randomize");

		if ((t != null) && (t.valueChanged() || force))
			setRandomize(t.getValue().toString());

		t = layoutProperties.get("min_weight");

		if ((t != null) && (t.valueChanged() || force))
			setMinWeight(t.getValue().toString());

		t = layoutProperties.get("max_weight");

		if ((t != null) && (t.valueChanged() || force))
			setMaxWeight(t.getValue().toString());

		t = layoutProperties.get("selected_only");

		if ((t != null) && (t.valueChanged() || force))
			setSelectedOnly(t.getValue().toString());

		t = layoutProperties.get("edge_attribute");

		if ((t != null) && (t.valueChanged() || force)) {
			setLayoutAttribute(t.getValue().toString());
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void revertSettings() {
		layoutProperties.revertProperties();
	}

	/**
	 * Main entry point for AbstractLayout classes
	 */
	public void construct() {
		taskMonitor.setStatus("Initializing");
		initialize(); // Calls initialize_local

		if (partitionList == null) {
			System.out.println("Nothing to layout!");

			return;
		} else {
			// Set up offsets -- we start with the overall min and max
			double xStart = partitionList.get(0).getMinX();
			double yStart = partitionList.get(0).getMinY();
			Iterator partIter = partitionList.iterator();

			while (partIter.hasNext()) {
				LayoutPartition part = (LayoutPartition) partIter.next();
				xStart = Math.min(xStart, part.getMinX());
				yStart = Math.min(yStart, part.getMinY());
			}

			double next_x_start = xStart;
			double next_y_start = yStart;
			double current_max_y = 0;
			double incr = 50;

			double max_dimensions = Math.sqrt((double) network.getNodeCount());
			// give each node room
			max_dimensions *= incr;
			max_dimensions += xStart;

			// System.out.println("Laying out with "+partitionList.size()+" partitions");
			int partCount = 1;

			// For each partition, layout the graph
			partIter = partitionList.iterator();

			while (partIter.hasNext()) {
				LayoutPartition partition = (LayoutPartition) partIter.next();

				if (partition.nodeCount() > 1) {
					layout(partition);

					// Offset if we have more than one partition
					if (partitionList.size() > 1)
						partition.offset(next_x_start, next_y_start);
				} else {
					// System.out.println(" done");

					// NodeList is either empty or contains a single node
					if (partition.nodeCount() == 0)
						continue;

					// Reset our bounds
					partition.resetNodes();

					// Single node -- get it
					LayoutNode node = (LayoutNode) partition.getNodeList().get(0);
					node.setX(next_x_start);
					node.setY(next_y_start);
					partition.moveNodeToLocation(node);
				}

				double last_max_x = partition.getMaxX();
				double last_max_y = partition.getMaxY();

				if (last_max_y > current_max_y) {
					current_max_y = last_max_y;
				}

				if (last_max_x > max_dimensions) {
					max_dimensions = last_max_x;
					next_x_start = xStart;
					next_y_start = current_max_y;
					next_y_start += (incr * 2);
				} else {
					next_x_start = last_max_x;
					next_x_start += (incr * 2);
				}

				partCount++;
			}
		}
	}

	/**
	 * Main function that must be implemented by the child class.
	 */
	public abstract void layout(LayoutPartition partition);

	/**
	 * Call all of the initializtion code.  Called from
	 * AbstractLayout.initialize().
	 */
	protected void initialize_local() {
		LayoutPartition.setWeightCutoffs(minWeightCutoff, maxWeightCutoff);

		// Depending on whether we are partitioned or not,
		// we use different initialization.  Note that if the user only wants
		// to lay out selected nodes, partitioning becomes a very bad idea!
		if (!partitionGraph || selectedOnly) {
			// We still use the partition abstraction, even if we're
			// not partitioning.  This makes the code further down
			// much cleaner
			LayoutPartition partition = new LayoutPartition(network, networkView, selectedOnly,
			                                                edgeAttribute);
			partitionList = new ArrayList<LayoutPartition>(1);
			partitionList.add(partition);
		} else {
			partitionList = LayoutPartition.partition(network, networkView, selectedOnly,
			                                          edgeAttribute);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param message DOCUMENT ME!
	 */
	public static void debugln(String message) {
		if (debug) {
			System.err.println(message);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param message DOCUMENT ME!
	 */
	public static void debug(String message) {
		if (debug) {
			System.err.print(message);
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void setCancel() {
		canceled = true;
	}
}
