package cytoscape.graph.dynamic;

import cytoscape.util.intr.IntEnumerator;

// Edges and nodes are non-negative.
public interface DynamicGraph
{
  public IntEnumerator nodes();
  public IntEnumerator edges();
  public boolean removeNode(int node);
  public int createNode();
  public boolean removeEdge(int edge);
  // Returns -1 if nodes specified are invalid.
  public int createEdge(int sourceNode, int targetNode, boolean directed);
  public boolean containsNode(int node);
  public boolean containsEdge(int edge);
  // Throws IllegalArgumentException.
  public IntEnumerator adjacentEdges(int node, boolean undirected,
                                     boolean incoming, boolean outgoing);
  // Returns -1 if edge specified is invalid.
  public int sourceNode(int edge);
  public int targetNode(int edge);
  // Throws IllegalArgumentException.
  public boolean isDirectedEdge(int edge);
}
