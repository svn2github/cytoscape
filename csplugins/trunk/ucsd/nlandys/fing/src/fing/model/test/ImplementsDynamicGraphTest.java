package fing.model.test;

import cytoscape.graph.dynamic.DynamicGraph;
import fing.model.FingRootGraphFactory;
import giny.model.RootGraph;

public class ImplementsDynamicGraphTest
{

  public static void main(String[] args)
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
    final DynamicGraph graph = (DynamicGraph) root;
    graph.nodes();
  }

}
