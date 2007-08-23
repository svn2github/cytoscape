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
package csplugins.layout.algorithms.graphPartition;

import csplugins.layout.LayoutNode;
import csplugins.layout.LayoutPartition;

import csplugins.layout.algorithms.graphPartition.AbstractGraphPartition;

import cytoscape.*;

import cytoscape.data.*;

import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;

import cytoscape.view.*;

import filter.cytoscape.*;

import giny.model.*;

import giny.view.*;

import java.awt.GridLayout;

import java.util.*;

import javax.swing.JPanel;


/**
 *
 */
public class AttributeCircleLayout extends AbstractGraphPartition {
	CyAttributes data;
	String attribute = null;
	private double spacing = 50.0;
	boolean supportNodeAttributes = true;
	LayoutProperties layoutProperties = null;

	/**
	 * Creates a new AttributeCircleLayout object.
	 *
	 * @param supportAttributes  DOCUMENT ME!
	 */
	public AttributeCircleLayout(boolean supportAttributes) {
		super();
		initialize(supportAttributes);
	}

	/**
	 * Creates a new AttributeCircleLayout object.
	 */
	public AttributeCircleLayout() {
		super();
		initialize(true);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param supportAttributes DOCUMENT ME!
	 */
	public void initialize(boolean supportAttributes) {
		supportNodeAttributes = supportAttributes;
		layoutProperties = new LayoutProperties(getName());
		initialize_properties();
	}

	// Required methods for AbstactLayout
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte[] supportsNodeAttributes() {
		if (!supportNodeAttributes)
			return null;

		byte[] all = { -1 };

		return all;
	}

	/**
	 * Sets the attribute to use for the weights
	 *
	 * @param value the name of the attribute
	 */
	public void setLayoutAttribute(String value) {
		if (value.equals("(none)"))
			this.attribute = null;
		else
			this.attribute = value;
	}

	/**
	 * Get the settings panel for this layout
	 */
	public JPanel getSettingsPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(layoutProperties.getTunablePanel());

		return panel;
	}

	protected void initialize_properties() {
		layoutProperties.add(new Tunable("spacing", "Circle size", Tunable.DOUBLE, new Double(100.0)));
		layoutProperties.add(new Tunable("attribute", "The attribute to use for the layout",
		                                 Tunable.NODEATTRIBUTE, "(none)",
		                                 (Object) getInitialAttributeList(), (Object) null, 0));
		// We've now set all of our tunables, so we can read the property 
		// file now and adjust as appropriate
		layoutProperties.initializeProperties();

		// Finally, update everything.  We need to do this to update
		// any of our values based on what we read from the property file
		updateSettings(true);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void updateSettings() {
		updateSettings(false);
	}

	public LayoutProperties getSettings() {
		return layoutProperties;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param force DOCUMENT ME!
	 */
	public void updateSettings(boolean force) {
		layoutProperties.updateValues();

		Tunable t = layoutProperties.get("spacing");

		if ((t != null) && (t.valueChanged() || force))
			spacing = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("attribute");

		if ((t != null) && (t.valueChanged() || force)) {
			String newValue = (String) t.getValue();

			if (newValue.equals("(none)")) {
				attribute = null;
			} else {
				attribute = newValue;
				;
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void revertSettings() {
		layoutProperties.revertProperties();
	}

	/**
	 *
	 * We don't have any special widgets
	 *
	 * @returns List of our "special" weights
	 */
	public List<String> getInitialAttributeList() {
		ArrayList<String> attList = new ArrayList<String>();
		attList.add("(none)");

		return attList;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		if (!supportNodeAttributes) {
			return "Circle Layout";
		} else {
			return "Attribute Circle Layout";
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName() {
		if (!supportNodeAttributes)
			return "circle";
		else

			return "attribute-circle";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param partition DOCUMENT ME!
	 */
	public void layoutPartion(LayoutPartition partition) {
		data = Cytoscape.getNodeAttributes();

		int count = partition.nodeCount();
		int r = (int) Math.sqrt(count);
		r *= spacing;

		// nodesList is deprecated, so we need to create our own so
		// that we can hand it off to the sort routine
		List<LayoutNode> nodes = partition.getNodeList();

		if (this.attribute != null)
			Collections.sort(nodes, new AttributeComparator());

		// Compute angle step
		double phi = (2 * Math.PI) / nodes.size();

		partition.resetNodes(); // We want to figure out our mins & maxes anew
		                        // Arrange vertices in a circle

		for (int i = 0; i < count; i++) {
			LayoutNode node = (LayoutNode) nodes.get(i);
			double x = r + (r * Math.sin(i * phi));
			double y = r + (r * Math.cos(i * phi));
			node.setX(x);
			node.setY(y);
			partition.moveNodeToLocation(node);
		}
	}

	private class AttributeComparator implements Comparator<LayoutNode> {
		private AttributeComparator() {
		}

		public int compare(LayoutNode o1, LayoutNode o2) {

			byte type = data.getType(attribute);

			if (type == CyAttributes.TYPE_STRING) {
				String v1 = data.getStringAttribute(o1.getIdentifier(), attribute);
				String v2 = data.getStringAttribute(o2.getIdentifier(), attribute);

				if ((v1 != null) && (v2 != null))
					return v1.compareToIgnoreCase(v2);
				else if ((v1 == null) && (v2 != null))
					return -1;
				else if ((v1 == null) && (v2 == null))
					return 0;
				else if ((v1 != null) && (v2 == null))
					return 1;
			} else if (type == CyAttributes.TYPE_FLOATING) {
				Double v1 = data.getDoubleAttribute(o1.getIdentifier(), attribute);
				Double v2 = data.getDoubleAttribute(o2.getIdentifier(), attribute);

				if ((v1 != null) && (v2 != null))
					return v1.compareTo(v2);
				else if ((v1 == null) && (v2 != null))
					return -1;
				else if ((v1 == null) && (v2 == null))
					return 0;
				else if ((v1 != null) && (v2 == null))
					return 1;
			} else if (type == CyAttributes.TYPE_INTEGER) {
				Integer v1 = data.getIntegerAttribute(o1.getIdentifier(), attribute);
				Integer v2 = data.getIntegerAttribute(o2.getIdentifier(), attribute);

				if ((v1 != null) && (v2 != null))
					return v1.compareTo(v2);
				else if ((v1 == null) && (v2 != null))
					return -1;
				else if ((v1 == null) && (v2 == null))
					return 0;
				else if ((v1 != null) && (v2 == null))
					return 1;
			} else if (type == CyAttributes.TYPE_BOOLEAN) {
				Boolean v1 = data.getBooleanAttribute(o1.getIdentifier(), attribute);
				Boolean v2 = data.getBooleanAttribute(o2.getIdentifier(), attribute);

				if ((v1 != null) && (v2 != null)) {
					if ((v1.booleanValue() && v2.booleanValue())
					    || (!v1.booleanValue() && !v2.booleanValue()))
						return 0;
					else if (v1.booleanValue() && !v2.booleanValue())
						return 1;
					else if (!v1.booleanValue() && v2.booleanValue())
						return -1;
				} else if ((v1 == null) && (v2 != null))
					return -1;
				else if ((v1 == null) && (v2 == null))
					return 0;
				else if ((v1 != null) && (v2 == null))
					return 1;
			}

			return 0;
		}
	}
}
