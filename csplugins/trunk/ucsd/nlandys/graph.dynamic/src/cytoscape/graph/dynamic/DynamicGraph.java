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
   * @return an enumeration over all nodes currently in this graph; null
   *   is never returned.
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
   * @return an enumeration over all edges currently in this graph; null
   *   is never returned.
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
   * Creates a new edge in this graph, having source node, target node,
   * and directedness specified.  Returns the new edge, or -1 if either the
   * source or target node don't exist in this graph.<p>
   * Implementations should try to create edges with small values.
   * Implementation should try to prevent new edges from taking
   * ever-increasing values when edges are continually being removed and
   * created.
   * @param sourceNode the source node that the new edge is to have.
   * @param targetNode the target node that the new edge is to have.
   * @param directed the new edge will be directed if and only if this value
   *   is true.
   * @return the newly created edge or -1 if either the source or target node
   *   specified does not exist in this graph.
   * @exception IllegalArgumentException if either source or target node
   *   specified is not positive.
   */
  public int createEdge(int sourceNode, int targetNode, boolean directed);

  /**
   * Determines whether or not a node exists in this graph.
   * Returns true if and only if the node specified exists.
   * @param node the [potentially existing] node in this graph whose existence
   *   we're querying.
   * @return the existence of specified node in this graph.
   * @exception IllegalArgumentException if node is not positive.
   */
  public boolean containsNode(int node);

  /**
   * Determines whether or not an edge exists in this graph.
   * Returns true if and only if the edge specified exists.
   * @param edge the [potentially existing] edge in this graph whose existence
   *   we're querying.
   * @return the existence of specified edge in this graph.
   * @exception IllegalArgumentException if edge is not positive.
   */
  public boolean containsEdge(int edge);

  /**
   * Returns an enumeration of edges adjacent to a node.
   * The three boolean input parameters define what is meant by "adjacent
   * edge".  Notice that the three boolean input parameters define three
   * disjoint sets of edges.  Notice also that if all three boolean input
   * parameters are false, then an "adjacent edge" cannot possibly be in
   * the returned enumeration.
   * @param node the node in this graph whose adjacent edges we're seeking.
   * @param undirected all undirected edges touching the specified node
   *   are included in the returned enumeration if this value is true;
   *   otherwise, not a single such edge is included in the returned
   *   enumeration.
   * @param incoming all directed edges whose target is the node specified
   *   are included in the returned enumeration if this value is true;
   *   otherwise, not a single such edge is included in the returned
   *   enumeration.
   * @param outgoing all directed edges whose source is the node specified
   *   are included in the returned enumeration if ths value is true;
   *   otherwise, not a single such edge is included in the returned
   *   enumeration.
   * @return an enumeration of edges adjacent to the node specified
   *   or null if specified node does not exist in this graph.
   * @exception IllegalArgumentException if node is not positive.
   */
  public IntEnumerator adjacentEdges(int node, boolean undirected,
                                     boolean incoming, boolean outgoing);

  /**
   * Determines the source node of an edge.
   * Returns the source node of specified edge or -1 if specified edge does
   * not exist in this graph.
   * @param edge the edge in this graph whose source node we're seeking.
   * @return the source node of specified edge or -1 if specified edge does
   *   not exist in this graph.
   * @exception IllegalArgumentException if edge is not positive.
   */
  public int sourceNode(int edge);

  /**
   * Determines the target node of an edge.
   * Returns the target node of specified edge or -1 if specified edge does
   * not exist in this graph.
   * @param edge the edge in this graph whose target node we're seeking.
   * @return the target node of specified edge or -1 if specified edge does
   *   not exist in this graph.
   * @exception IllegalArgumentException if edge is not positive.
   */
  public int targetNode(int edge);

  /**
   * Determines the directedness of and edge.
   * Returns 1 if specified edge is directed, returns 0 if specified edge
   * is undirected, and returns -1 if specified edge does not exist in this
   * graph.
   * @param edge the edge in this graph whose directedness we're seeking.
   * @return 1 if specified edge is directed, 0 if specified edge is
   *   undirected, and -1 if specified edge does not exist in this graph.
   * @exception IllegalArgumentException if edge is not positive.
   */
  public byte isDirectedEdge(int edge);

}
