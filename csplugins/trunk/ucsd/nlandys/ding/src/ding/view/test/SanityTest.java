package ding.view.test;

import ding.view.DGraphView;
import fing.model.FingRootGraphFactory;
import giny.model.GraphPerspective;
import giny.model.RootGraph;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.view.EdgeView;

public class SanityTest
{

  public static void main(String[] args)
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
    final int node1 = root.createNode();
    final int node2 = root.createNode();
    final int node3 = root.createNode();
    final int edge1 = root.createEdge(node1, node2);
    final int edge2 = root.createEdge(node2, node3);
    final int edge3 = root.createEdge(node3, node1);
    final GraphPerspective persp = root.createGraphPerspective
      (new int[] { node1, node2, node3 },
       new int[] { edge1, edge2, edge3 });
  }

}
