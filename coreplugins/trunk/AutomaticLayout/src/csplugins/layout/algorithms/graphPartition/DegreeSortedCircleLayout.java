/* vim: set ts=2: */
package csplugins.layout.algorithms.graphPartition;

import cern.colt.list.*;

import csplugins.layout.LayoutNode;
import csplugins.layout.LayoutPartition;

import cytoscape.*;

import cytoscape.data.*;

import cytoscape.view.*;

import filter.cytoscape.*;

import giny.model.*;

import giny.view.*;

import java.util.*;


/**
 *
 */
public class DegreeSortedCircleLayout extends AbstractGraphPartition {
	private final static String DEGREE = "Degree";
	
	/**
	 * Creates a new DegreeSortedCircleLayout object.
	 */
	public DegreeSortedCircleLayout() {
		super();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		return "Degree Sorted Circle Layout";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName() {
		return "degree-circle";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param partition DOCUMENT ME!
	 */
	public void layoutPartion(LayoutPartition partition) {
		// get an iterator over all of the nodes
		Iterator nodeIter = partition.nodeIterator();

		// create a new array that is the Nodes corresponding to the node indices
		LayoutNode[] sortedNodes = new LayoutNode[partition.nodeCount()];
		int i = 0;

		while (nodeIter.hasNext()) {
			sortedNodes[i++] = (LayoutNode) nodeIter.next();
		}

		if (canceled)
			return;

		// sort the Nodes based on the degree
		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		Arrays.sort(sortedNodes,
		            new Comparator<LayoutNode>() {
				public int compare(LayoutNode o1, LayoutNode o2) {
					final Node node1 = o1.getNode();
					final Node node2 = o2.getNode();
					final int d1 = Cytoscape.getCurrentNetwork().getDegree(node1.getRootGraphIndex());
					final int d2 = Cytoscape.getCurrentNetwork().getDegree(node2.getRootGraphIndex());
					nodeAttr.setAttribute(node1.getIdentifier(), DEGREE, d1);
					nodeAttr.setAttribute(node2.getIdentifier(), DEGREE, d2);
					return (d2 - d1);
				}

				public boolean equals(Object o) {
					return false;
				}
			});

		if (canceled)
			return;

		// place each Node in a circle
		int r = 100 * (int) Math.sqrt(sortedNodes.length);
		double phi = (2 * Math.PI) / sortedNodes.length;
		partition.resetNodes(); // We want to figure out our mins & maxes anew

		for (i = 0; i < sortedNodes.length; i++) {
			LayoutNode node = sortedNodes[i];
			node.setX(r + (r * Math.sin(i * phi)));
			node.setY(r + (r * Math.cos(i * phi)));
			partition.moveNodeToLocation(node);
		}
	}
	
	public void construct() {
		super.construct();
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
}
