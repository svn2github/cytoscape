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

import csplugins.layout.EdgeWeighter;
import csplugins.layout.LayoutNode;
import csplugins.layout.LayoutPartition;
import csplugins.layout.Profile;
import csplugins.layout.algorithms.graphPartition.AbstractGraphPartition;

import cytoscape.data.CyAttributes;

import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;

import java.awt.GridLayout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


/**
 * Superclass for the two bioLayout algorithms (KK and FR).
 *
 * @author <a href="mailto:scooter@cgl.ucsf.edu">Scooter Morris</a>
 * @version 0.9
 */
public abstract class BioLayoutAlgorithm extends AbstractGraphPartition {
	/**
	 * Properties
	 */
	private static final int DEBUGPROP = 0;
	private static final int RANDOMIZE = 1;
	private static final int MINWEIGHT = 2;
	private static final int MAXWEIGHT = 3;
	private static final int SELECTEDONLY = 4;
	private static final int LAYOUTATTRIBUTE = 5;

	/**
	 * A small value used to avoid division by zero
	 */
	protected double EPSILON = 0.0000001D;

	/**
	 * Value to set for doing unweighted layouts
	 */
	public static final String UNWEIGHTEDATTRIBUTE = "(unweighted)";

	/**
 	 * Our list of Tunables
 	 */
	protected LayoutProperties layoutProperties;

	/**
	 * Enables/disables debugging messages
	 */
	private final static boolean DEBUG = false;
	protected static boolean debug = DEBUG; // so we can overload it with a property

	/**
	 * Whether or not to initialize by randomizing all points
	 */
	protected boolean randomize = true;

	/**
	 * Whether or not to use edge weights for layout
	 */
	protected boolean supportWeights = true;

	/**
	 * This is the constructor for the bioLayout algorithm.
	 */
	public BioLayoutAlgorithm() {
		super();

		if (edgeWeighter == null)
			edgeWeighter = new EdgeWeighter();

		layoutProperties = new LayoutProperties(getName());
	}

	/**
	 *  Tells Cytoscape whether we support selected nodes only or not
	 *
	 * @return true since we do support it 
	 */
	public boolean supportsSelectedOnly() { return true; }

	// We don't support node attribute-based layouts
	/**
	 *  Tells Cytoscape whether we support node attribute based layouts
	 *
	 * @return nulls, which indicates that we don't
	 */
	public byte[] supportsNodeAttributes() {
		return null;
	}

	// We do support edge attribute-based layouts
	/**
	 *  Tells Cytoscape whether we support node attribute based layouts
	 *
	 * @return null if supportWeights is false, otherwise return the attribute
	 *         types that can be used for weights.
	 */
	public byte[] supportsEdgeAttributes() {
		if (!supportWeights)
			return null;

		byte[] attrs = { CyAttributes.TYPE_INTEGER, CyAttributes.TYPE_FLOATING };

		return attrs;
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
	 * Sets the randomize flag
	 *
	 * @param flag boolean value that turns initial randomization on or off
	 */
	public void setRandomize(boolean flag) {
		randomize = flag;
	}

	/**
	 * Sets the randomize flag
	 *
	 * @param value boolean string that turns initial randomization on or off
	 */
	public void setRandomize(String value) {
		Boolean val = new Boolean(value);
		randomize = val.booleanValue();
	}

	/**
	 * Reads all of our properties from the cytoscape properties map and sets
	 * the values as appropriates.
	 */
	protected void initializeProperties() {
		
		layoutProperties.add(new Tunable("standard", "Standard settings", Tunable.GROUP,
		                                 new Integer(3)));
		/*
		layoutProperties.add(new Tunable("debug", "Enable debugging", Tunable.BOOLEAN,
		                                 new Boolean(false), Tunable.NOINPUT));
		*/
		layoutProperties.add(new Tunable("partition", "Partition graph before layout",
		                                 Tunable.BOOLEAN, new Boolean(true)));
		layoutProperties.add(new Tunable("randomize", "Randomize graph before layout",
		                                 Tunable.BOOLEAN, new Boolean(true)));
		layoutProperties.add(new Tunable("selected_only", "Only layout selected nodes",
		                                 Tunable.BOOLEAN, new Boolean(false)));

		if (supportWeights) {
			edgeWeighter.getWeightTunables(layoutProperties, getInitialAttributeList());
		}
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
	 *  Update our settings in response to a user setting change
	 */
	public void updateSettings() {
		updateSettings(false);
	}

	/**
	 *  Update the settings the user has requested
	 *
	 * @param force if true, always read the settings
	 */
	public void updateSettings(boolean force) {
		layoutProperties.updateValues();

		Tunable t = layoutProperties.get("debug");
		if ((t != null) && (t.valueChanged() || force)) {
			setDebug(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}


		t = layoutProperties.get("partition");
		if ((t != null) && (t.valueChanged() || force)) {
			setPartition(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("randomize");
		if ((t != null) && (t.valueChanged() || force)) {
			setRandomize(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		t = layoutProperties.get("selected_only");
		if ((t != null) && (t.valueChanged() || force)) {
			setSelectedOnly(t.getValue().toString());
			if (t.valueChanged())
				layoutProperties.setProperty(t.getName(), t.getValue().toString());
		}

		if (supportWeights) 
			edgeWeighter.updateSettings(layoutProperties, force);
	}

	/**
	 *  Revert to the default settings
	 */
	public void revertSettings() {
		layoutProperties.revertProperties();
	}

	/**
	 * Main function that must be implemented by the child class.
	 */
	public abstract void layoutPartion(LayoutPartition partition);

	protected void initialize_local() {
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
}
