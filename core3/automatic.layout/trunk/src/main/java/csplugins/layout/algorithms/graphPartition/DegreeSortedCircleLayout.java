/* vim: set ts=2: */
package csplugins.layout.algorithms.graphPartition;

import csplugins.layout.LayoutNode;
import csplugins.layout.LayoutPartition;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.work.UndoSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


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

    // just add the unlocked nodes
    List<LayoutNode> nodes = new ArrayList<LayoutNode>();
    for ( LayoutNode ln : partition.getNodeList() ) {
      if ( !ln.isLocked() ) {
        nodes.add(ln);
      }
    }
	
		if (canceled)
			return;

		// sort the Nodes based on the degree
		Collections.sort(nodes,
		            new Comparator<LayoutNode>() {
				public int compare(LayoutNode o1, LayoutNode o2) {
					final CyNode node1 = o1.getNode();
					final CyNode node2 = o2.getNode();
					// FIXME: should allow parametrization of edge type? (expose as tunable)
					final int d1 = network.getAdjacentEdgeList(node1, CyEdge.Type.ANY).size();
					final int d2 = network.getAdjacentEdgeList(node2, CyEdge.Type.ANY).size();
					return (d2 - d1);
				}

				public boolean equals(Object o) {
					return false;
				}
			});

		if (canceled)
			return;

		// place each Node in a circle
		int r = 100 * (int) Math.sqrt(nodes.size());
		double phi = (2 * Math.PI) / nodes.size();
		partition.resetNodes(); // We want to figure out our mins & maxes anew

		for (int i = 0; i < nodes.size(); i++) {
			LayoutNode node = nodes.get(i);
			node.setX(r + (r * Math.sin(i * phi)));
			node.setY(r + (r * Math.cos(i * phi)));
			partition.moveNodeToLocation(node);
		}
	}
	
	public void construct() {
		super.construct();
	}
}
