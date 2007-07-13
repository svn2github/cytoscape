
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

package csplugins.layout.algorithms;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;

import giny.model.GraphPerspective;
import giny.model.Node;

import java.awt.GridLayout;
import java.awt.Rectangle;

import java.lang.Double;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;


/*
  This layout partitions the graph according to the selected node attribute's values.
  The nodes of the graph are broken into discrete partitions, where each partition has
  the same attribute value. For example, assume there are four nodes, where each node
  has the "IntAttr" attribute defined. Assume node 1 and 2 have the value "100" for
  the "IntAttr" attribute, and node 3 and 4 have the value "200." This will place nodes
  1 and 2 in the first partition and nodes 3 and 4 in the second partition.  Each
  partition is drawn in a circle.
*/
/**
 *
 */
public class GroupAttributesLayout extends AbstractLayout {
	/*
	  Layout parameters:
	    - spacingx: Horizontal spacing (on the x-axis) between two partitions in a row.
	    - spacingy: Vertical spacing (on the y-axis) between the largest partitions of two rows.
	    - maxwidth: Maximum width of a row
	    - minrad:   Minimum radius of a partition.
	    - radmult:  The scale of the radius of the partition. Increasing this value
	                will increase the size of the partition proportionally.
	 */
	private double spacingx = 400.0;
	private double spacingy = 400.0;
	private double maxwidth = 5000.0;
	private double minrad = 100.0;
	private double radmult = 50.0;
	private String attributeName;
	private byte attributeType;
	private CyAttributes nodeAttributes;
	private LayoutProperties layoutProperties;

	/**
	 * Creates a new GroupAttributesLayout object.
	 */
	public GroupAttributesLayout() {
		super();
		layoutProperties = new LayoutProperties(getName());
		initialize_properties();
	}

	/**
	 * Overrides for CyLayoutAlgorithm support
	 */
	public String getName() {
		return "attributes-layout";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		return "Group Attributes Layout";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte[] supportsNodeAttributes() {
		byte[] attrs = {
		                   CyAttributes.TYPE_INTEGER, CyAttributes.TYPE_STRING,
		                   CyAttributes.TYPE_FLOATING, CyAttributes.TYPE_BOOLEAN
		               };

		return attrs;
	}

	/**
	 * Sets the attribute to use for the weights
	 *
	 * @param value the name of the attribute
	 */
	public void setLayoutAttribute(String value) {
		if (value == null) {
			attributeName = null;
		} else {
			attributeName = value;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<String> getInitialAttributeList() {
		return null;
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
		layoutProperties.add(new Tunable("spacingx",
		                                 "Horizontal spacing between two partitions in a row",
		                                 Tunable.DOUBLE, new Double(400.0)));
		layoutProperties.add(new Tunable("spacingy",
		                                 "Vertical spacing between the largest partitions of two rows",
		                                 Tunable.DOUBLE, new Double(400.0)));
		layoutProperties.add(new Tunable("maxwidth", "Maximum width of a row", Tunable.DOUBLE,
		                                 new Double(5000.0)));
		layoutProperties.add(new Tunable("minrad", "Minimum width of a partition", Tunable.DOUBLE,
		                                 new Double(100.0)));
		layoutProperties.add(new Tunable("radmult", "Scale of the radius of the partition",
		                                 Tunable.DOUBLE, new Double(50.0)));
		layoutProperties.add(new Tunable("attributeName", "The attribute to use for the layout",
		                                 Tunable.NODEATTRIBUTE, ""));
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

	/**
	 *  DOCUMENT ME!
	 *
	 * @param force DOCUMENT ME!
	 */
	public void updateSettings(boolean force) {
		layoutProperties.updateValues();

		Tunable t = layoutProperties.get("spacingx");

		if ((t != null) && (t.valueChanged() || force))
			spacingx = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("spacingy");

		if ((t != null) && (t.valueChanged() || force))
			spacingy = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("maxwidth");

		if ((t != null) && (t.valueChanged() || force))
			maxwidth = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("minrad");

		if ((t != null) && (t.valueChanged() || force))
			minrad = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("radmult");

		if ((t != null) && (t.valueChanged() || force))
			radmult = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("attributeName");

		if ((t != null) && (t.valueChanged() || force))
			attributeName = (String) t.getValue();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void revertSettings() {
		layoutProperties.revertProperties();
	}

	public LayoutProperties getSettings() {
		return layoutProperties;
	}

	/*
	  Psuedo-procedure:
	  1. Call makeDiscrete(). This will create a map for each value of the
	     node attribute to the list of nodes with that attribute value.
	     Each of these lists will become a partition in the graph.
	     makeDiscrete() will also add nodes to the invalidNodes list
	     that do not have a value associated with the attribute.
	  2. Call sort(). This will return a list of partitions that is
	     sorted based on the value of the attribute. Add the invalid
	     nodes to the end of the sorted list. All the invalid nodes
	     will be grouped together in the last partition of the layout.
	  3. Begin plotting each partition.
	     a. Call encircle(). This will plot the partition in a circle.
	     b. Store the diameter of the last circle plotted.
	     c. Update maxheight. This stores the height of the largest circle
	        in a row.
	     d. Update offsetx. If we've reached the end of the row,
	        reset offsetx and maxheight; update offsety so that
	    it will store the y-axis location of the next row.
	*/
	/**
	 *  DOCUMENT ME!
	 */
	public void construct() {
		taskMonitor.setStatus("Initializing");
		initialize(); // Calls initialize_local

		nodeAttributes = Cytoscape.getNodeAttributes();

		attributeType = nodeAttributes.getType(attributeName);

		Map<Comparable, List<Node>> partitionMap = new TreeMap<Comparable, List<Node>>();
		List<Node> invalidNodes = new ArrayList<Node>();
		makeDiscrete(partitionMap, invalidNodes);

		List<List<Node>> partitionList = sort(partitionMap);
		partitionList.add(invalidNodes);

		double offsetx = 0.0;
		double offsety = 0.0;
		double maxheight = 0.0;

		for (List<Node> partition : partitionList) {
			if (canceled)
				return;

			double radius = encircle(partition, offsetx, offsety);

			double diameter = 2.0 * radius;

			if (diameter > maxheight)
				maxheight = diameter;

			offsetx += diameter;

			if (offsetx > maxwidth) {
				offsety += (maxheight + spacingy);
				offsetx = 0.0;
				maxheight = 0.0;
			} else
				offsetx += spacingx;
		}
	}

	private void makeDiscrete(Map<Comparable, List<Node>> map, List<Node> invalidNodes) {
		if (map == null)
			return;

		Iterator iterator = network.nodesIterator();

		while (iterator.hasNext()) {
			Node node = (Node) iterator.next();

			Comparable key = null;

			switch (attributeType) {
				case CyAttributes.TYPE_INTEGER:
					key = nodeAttributes.getIntegerAttribute(node.getIdentifier(), attributeName);

					break;

				case CyAttributes.TYPE_STRING:
					key = nodeAttributes.getStringAttribute(node.getIdentifier(), attributeName);

					break;

				case CyAttributes.TYPE_FLOATING:
					key = nodeAttributes.getDoubleAttribute(node.getIdentifier(), attributeName);

					break;

				case CyAttributes.TYPE_BOOLEAN:
					key = nodeAttributes.getBooleanAttribute(node.getIdentifier(), attributeName);

					break;
			}

			if (key == null) {
				if (invalidNodes != null)
					invalidNodes.add(node);
			} else {
				if (!map.containsKey(key))
					map.put(key, new ArrayList<Node>());

				map.get(key).add(node);
			}
		}
	}

	private List<List<Node>> sort(final Map<Comparable, List<Node>> map) {
		if (map == null)
			return null;

		List<Comparable> keys = new ArrayList<Comparable>(map.keySet());
		Collections.sort(keys);

		Comparator<Node> comparator = new Comparator<Node>() {
			public int compare(Node node1, Node node2) {
				String a = node1.getIdentifier();
				String b = node2.getIdentifier();

				return a.compareTo(b);
			}
		};

		List<List<Node>> sortedlist = new ArrayList<List<Node>>(map.keySet().size());

		for (Comparable key : keys) {
			List<Node> partition = map.get(key);
			Collections.sort(partition, comparator);
			sortedlist.add(partition);
		}

		return sortedlist;
	}

	private double encircle(List<Node> partition, double offsetx, double offsety) {
		if (partition == null)
			return 0.0;

		if (partition.size() == 1) {
			Node node = partition.get(0);
			networkView.getNodeView(node).setXPosition(offsetx);
			networkView.getNodeView(node).setYPosition(offsety);

			return 0.0;
		}

		double radius = radmult * Math.sqrt(partition.size());

		if (radius < minrad)
			radius = minrad;

		double phidelta = (2.0 * Math.PI) / partition.size();
		double phi = 0.0;

		for (Node node : partition) {
			double x = offsetx + radius + (radius * Math.cos(phi));
			double y = offsety + radius + (radius * Math.sin(phi));
			networkView.getNodeView(node).setXPosition(x);
			networkView.getNodeView(node).setYPosition(y);
			phi += phidelta;
		}

		return radius;
	}
}
