package fing.model.test;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.fixed.FixedGraph;
import fing.model.FingRootGraphFactory;
import giny.model.GraphPerspective;
import giny.model.RootGraph;

public class ImplementsGraphTest
{

  public static void main(String[] args)
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
    final DynamicGraph dGraph = (DynamicGraph) root;
    dGraph.nodes();
    final GraphPerspective persp =
      root.createGraphPerspective((int[]) null, (int[]) null);
    final FixedGraph fGraph = (FixedGraph) persp;
    fGraph.edges();
  }

}
