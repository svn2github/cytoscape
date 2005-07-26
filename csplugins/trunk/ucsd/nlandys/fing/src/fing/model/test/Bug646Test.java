package fing.model.test;

import fing.model.FingRootGraphFactory;
import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.RootGraph;

public class Bug646Test
{

  public static void main(String[] args)
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
    final int node1 = root.createNode();
    final int node2 = root.createNode();
    final int node3 = root.createNode();
    final int node4 = root.createNode();
    final int edge1 = root.createEdge(node1, node2, true);
    final int edge2 = root.createEdge(node1, node3, false);
    final int edge3 = root.createEdge(node3, node4, true);
    final int edge4 = root.createEdge(node2, node3, false);
    final int edge5 = root.createEdge(node4, node1, true);
    final int edge6 = root.createEdge(node4, node2, false);
    final int[] nodes = new int[] { node1, node2, node3, node4 };
    final int[] edges = new int[] { edge1, edge2, edge3, edge4, edge5, edge6 };
    final GraphPerspective persp = root.createGraphPerspective(nodes, edges);
    root.removeEdge(edge2);
    root.removeEdge(edge3);
    root.removeEdge(edge4);
    root.removeEdge(edge5);
    final int edge7 = root.createEdge(node4, node3, true);
    final int edge8 = root.createEdge(node3, node2, false);
    final Edge edge7Obj = root.getEdge(edge7);
    final Edge edge8Obj = root.getEdge(edge8);
    if (edge7 != edge7Obj.getRootGraphIndex() ||
        edge8 != edge8Obj.getRootGraphIndex()) {
      throw new IllegalStateException("the bug is here"); }
  }

}
