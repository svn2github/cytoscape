package cytoscape.graph.dynamic;

import cytoscape.util.intr.IntEnumerator;

/**
 * Edges and nodes are non-negative.
 */
public interface DynamicGraph
{

  /**
   * Returns an enumeration of all nodes currently in this graph.
   * Every node in this graph is a unique non-negative integer.  A given
   * node and a given edge in one graph may be the same integer.<p>
   * The returned enumeration becomes invalid as soon as any
   * graph-topology-modifying method on this graph is called.  Calling
   * methods on an invalid enumeration will result in undefined behavior
   * of that enumeration.  Enumerating through a graph's nodes will
   * never have any effect on the graph.
   * @return an enumeration over all nodes currently in this graph.
   */
  public IntEnumerator nodes();

  /**
   * Returns an enumeration of all edges currently in this graph.
   * Every edge in this graph is a unique non-negative integer.  A given
   * node and a given edge in one graph may be the same integer.<p>
   * The returned enumeration becomes invalid as soon as any
   * graph-topology-modifying method on this graph is called.  Calling
   * methods on an invalid enumeration will result in undefined behavior
   * of that enumeration.  Enumerating through a graph's edges will
   * never have any effect on the graph.
   * @return an enumeration over all edges currently in this graph.
   */
  public IntEnumerator edges();

  /**
   * Removes the specified node from this graph.  Returns true if and only
   * if the specified node was in this graph at the time this method was
   * called.  A return value of true implies that the specified node has
   * been successfully removed from this graph.<p>
   * Note that removal of a node necessitates the removal of any edge
   * touching that node.
   * @param node the node that is to be removed from this graph.
   * @return true if and only if the specified node existed in this graph
   *   at the time this operation was started.
   * @exception IllegalArgumentException if node is not positive.
   */
  public boolean removeNode(int node);

  /**
   * Creates a new node in this graph.  Returns the new node.<p>
   * Implementations should try to create nodes with small values.
   * Implementations should try to prevent new nodes from taking
   * ever-increasing values when nodes are continually being removed and
   * created.
   * @return the newly created node.
   */
  public int createNode();

  /**
   * Removes the specified edge from this graph.  Returns true if and only
   * if the specified edge was in this graph at the time this method was
   * called.  A return value of true implies that the specified edge has
   * been successfully removed from this graph.
   * @param edge the edge that is to be removed from this graph.
   * @return true if and only if the specified edge existed in this graph
   *   at the time this operation was started.
   * @exception IllegalArgumentException if edge is not positive.
   */
  public boolean removeEdge(int edge);

  /**
   * Creates a new edge in this graph, having source and target nodes
   * and directedness specified.  Returns the new edge, or -1 if either the
   * source or target node don't exist in this graph.<p>
   * Implementations should try to create edges with small values.
   * Implementation should try to prevent new edges from taking
   * ever-increasing values when edges are continually being removed and
   * created.
   * @param sourceNode the source node that the new edge is to have; if
   *   the new edge is undirected then swapping the source and target nodes
   *   result in the same operation.
   * @param targetNode the target node that the new edge is to have; if
   *   the new edge is undirected then swapping the source and target nodes
   *   result in the same operation.
   * @param directed the new edge will be directed if and only if this value
   *   is true.
   * @return the newly created edge or -1 if either the source or target node
   *   specified does not exist in this graph.
   * @exception IllegalArgumentException if either source or target node
   *   specified is not positive.
   */
  public int createEdge(int sourceNode, int targetNode, boolean directed);

  /**
   * Answers the question: Does the given node exist?
   * Returns true if and only if the node specified exists.
   * @param node the [potential] node in this graph whose existance we're
   *   querying.
   * @return the existance of specified node in this graph.
   * @exception IllegalArgumentException if node is not positive.
   */
  public boolean containsNode(int node);

  /**
   * Answers the question: Does the given edge exist?
   * Returns true if and only if the edge specified exists.
   * @param edge the [potential] edge in this graph whose existance we're
   *   querying.
   * @return the existance of specified edge in this graph.
   * @exception IllegalArgumentException if edge is not positive.
   */
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
