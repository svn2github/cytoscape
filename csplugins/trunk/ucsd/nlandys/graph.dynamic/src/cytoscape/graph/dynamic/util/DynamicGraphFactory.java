package cytoscape.graph.dynamic.util;

import cytoscape.graph.dynamic.DynamicGraph;

public final class DynamicGraphFactory
{

  // "No constructor".
  private DynamicGraphFactory() { }

  /**
   * Nodes and edges created by the returned DynamicGraph are strictly less
   * than Integer.MAX_VALUE.
   */
  public static DynamicGraph instantiateDynamicGraph()
  {
    return new DynamicGraphRepresentation();
  }

}
