package cytoscape.graph.dynamic.util;

import cytoscape.graph.dynamic.DynamicGraph;

public final class DynamicGraphFactory
{

  public static DynamicGraph instantiateDynamicGraph()
  {
    return new DynamicGraphRepresentation();
  }

}
