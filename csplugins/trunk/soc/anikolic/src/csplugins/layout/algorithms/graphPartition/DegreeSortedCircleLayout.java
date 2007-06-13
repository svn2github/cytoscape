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
		Arrays.sort(sortedNodes,
		            new Comparator() {
				public int compare(Object o1, Object o2) {
					Node node1 = ((LayoutNode) o1).getNode();
					Node node2 = ((LayoutNode) o2).getNode();

					return (Cytoscape.getCurrentNetwork().getDegree(node2.getRootGraphIndex())
					       - Cytoscape.getCurrentNetwork().getDegree(node1.getRootGraphIndex()));
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
}
