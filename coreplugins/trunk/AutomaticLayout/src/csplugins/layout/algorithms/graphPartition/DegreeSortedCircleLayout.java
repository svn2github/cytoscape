/* vim: set ts=2: */
package csplugins.layout.algorithms.graphPartition;

import java.util.*;

import cern.colt.list.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.*;
import giny.view.*;
import giny.model.*;

import filter.cytoscape.*;

import csplugins.layout.LayoutPartition;
import csplugins.layout.LayoutNode;

public class DegreeSortedCircleLayout extends AbstractGraphPartition
{
  public DegreeSortedCircleLayout()
  {
    super();
  }

	public String toString () { return "Degree Sorted Circle Layout"; }
	public String getName () { return "degree-circle"; }

  public void layoutPartion(LayoutPartition partition)
  {
    // get an iterator over all of the nodes
    Iterator nodeIter = partition.nodeIterator();

    // create a new array that is the Nodes corresponding to the node indices
    LayoutNode sortedNodes[] = new LayoutNode[partition.nodeCount()];
    int i = 0;
    while (nodeIter.hasNext())
    {
      sortedNodes[i++] = (LayoutNode)nodeIter.next();
    }
		if (canceled) return;

    // sort the Nodes based on the degree
    Arrays.sort(sortedNodes, new Comparator()
      {
        public int compare(Object o1, Object o2)
        {
          Node node1 = ((LayoutNode)o1).getNode();
          Node node2 = ((LayoutNode)o2).getNode();

          return (Cytoscape.getCurrentNetwork().getDegree(
                                    node2.getRootGraphIndex()) -
                  Cytoscape.getCurrentNetwork().getDegree(
                                    node1.getRootGraphIndex()));
        }

        public boolean equals(Object o)
        {
          return false;
        }
      }
    );
		if (canceled) return;

    // place each Node in a circle
    int r = 100 * (int) Math.sqrt(sortedNodes.length);
    double phi = 2 * Math.PI / sortedNodes.length;
		partition.resetNodes();  // We want to figure out our mins & maxes anew
    for (i = 0; i < sortedNodes.length; i++)
    {
      LayoutNode node = sortedNodes[i];
      node.setX(r + r * Math.sin(i * phi));
      node.setY(r + r * Math.cos(i * phi));
			partition.moveNodeToLocation(node);
    }
  }
}
