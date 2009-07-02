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
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import org.cytoscape.model.CyRow;
import org.cytoscape.tunable.ModuleProperties;
import org.cytoscape.tunable.Tunable;
import org.cytoscape.tunable.TunableFactory;
import org.cytoscape.model.CyDataTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


/**
 *
 */
public class AttributeCircleLayout extends AbstractGraphPartition {
	CyAttributes data;
	String attribute = null;
	private double spacing = 50.0;
	@Tunable(description="The attribute namespace to use for the layout")
	public String namespace = null;
	boolean supportNodeAttributes = true;
	ModuleProperties layoutProperties = null;

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
		layoutProperties = TunableFactory.getModuleProperties(getName(),"layout");
		initialize_properties();
	}

	// Required methods for AbstactLayout
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Set<Class<?>> supportsNodeAttributes() {
		Set<Class<?>> ret = new HashSet<Class<?>>();
		if (!supportNodeAttributes)
			return ret;

		ret.add(Integer.class);
		ret.add(Double.class);
		ret.add(String.class);
		ret.add(Boolean.class);
		ret.add(List.class);
		ret.add(Map.class);

		return ret;
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
		layoutProperties.add(TunableFactory.getTunable("spacing", "Circle size", Tunable.DOUBLE, new Double(100.0)));
		layoutProperties.add(TunableFactory.getTunable("attribute", "The attribute to use for the layout",
		                                 Tunable.NODEATTRIBUTE, "(none)",
		                                 (Object) getInitialAttributeList(), (Object) null, 0));
		// We've now set all of our tunables, so we can read the property 
		// file now and adjust as appropriate
		layoutProperties.initializeProperties(CytoscapeInit.getProperties());

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

	public ModuleProperties getSettings() {
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
		// just add the unlocked nodes
		List<LayoutNode> nodes = new ArrayList<LayoutNode>();
		for ( LayoutNode ln : partition.getNodeList() ) {
			if ( !ln.isLocked() ) {
				nodes.add(ln);
			}
		}

		int count = nodes.size(); 
		int r = (int) Math.sqrt(count);
		r *= spacing;

		if (this.attribute != null){
			CyDataTable dataTable = network.getCyDataTables("NODE").get(namespace);
			Class<?> klass = dataTable.getColumnTypeMap().get(attribute);
			if (Comparable.class.isAssignableFrom(klass)){
				// FIXME: I assume this would be better, but get type errors if I try:
				//Class<Comparable<?>> kasted = (Class<Comparable<?>>) klass;
				//Collections.sort(nodes, new AttributeComparator<Comparable<?>>(kasted));
				Collections.sort(nodes, new AttributeComparator(klass));
			} else {
				/* FIXME Error! */
			}
		}

		// Compute angle step
		double phi = (2 * Math.PI) / count; 

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
	private class AttributeComparator<T extends Comparable<T>> implements Comparator<LayoutNode> {
		Class<T> klass;
		private AttributeComparator(Class<T> klass) {
			this.klass = klass;
		}
		
		public int compare(LayoutNode o1, LayoutNode o2) {
			T v1 = o1.getNode().getCyRow(namespace).get(attribute, klass);
			T v2 = o2.getNode().getCyRow(namespace).get(attribute, klass);
			if (String.class.isAssignableFrom(klass)){ // i.e. if klass _is_ String.class
				String s1 = String.class.cast(v1);
				String s2 = String.class.cast(v2);
				if ((s1 != null) && (s2 != null))
					return s1.compareToIgnoreCase(s2);
				else if ((s1 == null) && (s2 != null))
					return -1;
				else if ((s1 == null) && (s2 == null))
					return 0;
				else if ((s1 != null) && (s2 == null))
					return 1;
				
			} else {
				return compareEvenIfNull(v1, v2);
			}

			return 0; // can't happen anyway
		}

		public int compareEvenIfNull(T v1, T v2){
			if ((v1 != null) && (v2 != null))
				return v1.compareTo(v2);
			else if ((v1 == null) && (v2 != null))
				return -1;
			else if ((v1 == null) && (v2 == null))
				return 0;
			else // if ((v1 != null) && (v2 == null)) // this is the only possibility
				return 1;
		}
	}
}
