package cytoscape.graph.dynamic.util;

import cytoscape.graph.dynamic.DynamicGraph;

/**
 * A factory for getting cytoscape.graph.dynamic.DynamicGraph instances.
 * This DynamicGraph implementation requires a bare minimum of roughly 64
 * metabytes for a graph with one million edges and a hundred thousand nodes.
 * That is, the memory requirements are roughly 64 bytes per node and edge.
 */
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
