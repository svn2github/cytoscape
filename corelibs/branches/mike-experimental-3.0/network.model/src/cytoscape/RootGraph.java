package cytoscape;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.NoSuchElementException;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

/**
 * <h2>GINY Architecture</h2>
 * A graph model consisting of nodes and the edges between them.  GINY graph
 * models are separated into RootGraphs and GraphPerspectives.  RootGraphs
 * contain all Nodes and Edges in a graph, while GraphPerspectives contain some
 * subset of them (any given GraphPerspective may include all Nodes and Edges
 * from its RootGraph, but it does not need to).  Every GraphPerspective has
 * exactly one RootGraph.  RootGraphs may have any number of GraphPerspectives.

 * <h2>Indices</h2>
 * Every Node and Edge has a unique and unchanging index in its RootGraph;
 * these indices are always negative integers and are not guaranteed to be
 * consecutive.  Note that if you would
 * like to associate additional data with a Node or an Edge, you can associate
 * the data with the Node's or the Edge's RootGraph index, which is guaranteed
 * to be unique for the lifetime of the RootGraph.

 * <h2>MetaNodes</h2>
 * Nodes and Edges comprise an additional directed graph through the
 * contains-a relationship, in which a MetaParent Node contains each of its
 * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren, and
 * a Node or Edge may have any number of MetaParents.  This is how GINY
 * supports graphs-within-graphs.
 **/
public interface RootGraph {

 /**
   * Create a new GraphPerspective with just the given Nodes and Edges (and all
   * Nodes incident on the given Edges).<p>
   * TECHNICAL DETAIL: Each Node in the Node array input parameter
   * should be a Node that was previously returned by a method of this RootGraph or by
   * a method of another component that this RootGraph system defines.
   * Likewise, each Edge in the Edge array input parameter
   * should be an Edge that was previously returned by a method of this RootGraph or by
   * a method of another component that this RootGraph system defines.
   * If this is not the case, results of calling this method are undefined.
   * @param nodes A [possibly null] array of Nodes in this RootGraph to include in the new
   * GraphPerspective
   * @param edges A [possibly null] array of Edges in this RootGraph to include in the new
   * GraphPerspective
   * @return a new GraphPerspective on this RootGraph containing only the given
   * Nodes and Edges, plus any Nodes incident on the given Edges array but
   * omitted from the given Nodes array; returns null if any of the specified Nodes
   * or Edges are not in this RootGraph.
   */
  public GraphPerspective createGraphPerspective ( Node[] nodes, Edge[] edges);

  public GraphPerspective createGraphPerspective ( Collection<Node> nodes, Collection<Edge> edges);

  /**
   * Create a new GraphPerspective with just the given Nodes and Edges (and all
   * Nodes incident on the given Edges).
   * @param node_indices A [possibly null] array of indices of Nodes in this RootGraph to
   * include in the new GraphPerspective
   * @param edge_indices A [possibly null] array of indices of Edges in this RootGraph to
   * include in the new GraphPerspective
   * @return a new GraphPerspective on this RootGraph containing only Nodes and
   * Edges with the given indices, including all Nodes incident on those Edges;
   * returns null if any of the specified Node or Edge indices do not correspond
   * to Nodes or Edges existing in this RootGraph.
   */
  public GraphPerspective createGraphPerspective (
    int[] node_indices,
    int[] edge_indices
    );

  /**
   * Returns number of nodes in this RootGraph.  A call to nodesIterator()
   * will return an Iterator containing exactly getNodeCount() elements
   * (unless nodes are created or removed in the meantime).
   * @return the number of nodes in this RootGraph.
   */
  public int getNodeCount ();

  /**
   * Returns number of edges in this RootGraph.  A call to edgesIterator()
   * will return an iterator containing exactly getEdgeCount() elements
   * (unless nodes or edges are created or removed in the meantime).
   * @return the number of edges in this RootGraph.
   */
  public int getEdgeCount ();

  /**
   * Returns an Iterator over all cytoscape.Node objects in this RootGraph.<p>
   * TECHNICAL DETAIL:  Iterating over the set of all nodes in a RootGraph and
   * manipulating a RootGraph's topology (by calling removeXXX() and createXXX() methods)
   * concurrently will have undefined effects on the returned Iterator.
   * @return an Iterator over the Nodes in this graph; each Object in the
   *   returned Iterator is of type cytoscape.Node.
   */
  public Iterator<Node> nodesIterator ();

  /**
   * Returns a list of Node objects.
   * @see #nodesIterator()
   */
  public List<Node> nodesList ();

  /**
   * Returns an array of node indices.
   * @see #nodesIterator()
   * @see Node#getRootGraphIndex()
   */
  public int[] getNodeIndicesArray ();

  /**
   * Returns an Iterator over all cytoscape.Edge objects in this RootGraph.<p>
   * TECHNICAL DETAIL:  Iterating over the set of all edges in a RootGraph and
   * manipulating a RootGraph's topology (by calling removeXXX() and createXXX()
   * methods) concurrently will have undefined effects on the returned Iterator.
   * @return an Iterator over the Edges in this graph; each Object in the
   *   returned Iterator is of type cytoscape.Edge.
   */
  public Iterator<Edge> edgesIterator ();

  /**
   * Returns a list of Edge objects.
   * @see #edgesIterator()
   */
  public List<Edge> edgesList ();

  /**
   * Returns an array of edge indices.
   * @see #edgesIterator()
   * @see Edge#getRootGraphIndex()
   */
  public int[] getEdgeIndicesArray ();

  /**
   * Remove the given Node and all Edges incident on it from this RootGraph and
   * all of its GraphPerspectives.<p>
   * TECHNICAL DETAIL: The Node input parameter should be a Node that was
   * previously returned by a method of this RootGraph or by a method of another
   * component that this RootGraph system defines.  If this is not the case,
   * results of calling this method are undefined.<p>
   * IMPORTANT! The returned Node object, if not null, should not be used
   * to query fields by calling methods on that returned Node object - the returned
   * Node's behavior is completely undefined.  For
   * example, an implementation of RootGraph may choose to return a Node object
   * whose getRootGraphIndex() method returns a positive value.  The purpose
   * of the return value of this method is purely to mark "success" or
   * "not success" - in fact, if this API were redesigned, the return value of
   * this operation would become a boolean value.
   * @param node The Node to remove.
   * @return A non-null Node marking a successful removal,
   *   or null if specified Node does not belong to this RootGraph.
   */
  public Node removeNode ( Node node );

  /**
   * Remove the Node with the given index (and all of that Node's incident
   * Edges) from this RootGraph and all of its GraphPerspectives.
   * @param node_index The index in this RootGraph of the Node to remove.
   * @return The index of the removed Node, or 0 if the given index does not
   *   correspond to an existing Node in this RootGraph.
   */
  public int removeNode(int node_index);

  /**
   * @see #removeNode(Node)
   * @see #removeNodes(int[])
   */
  public List<Node> removeNodes ( List<Node> nodes );

  /**
   * Remove the Nodes with the given indices (and all of those Nodes' incident
   * Edges) from this RootGraph and all of its GraphPerspectives.
   * @param node_indices An non-null array of the indices in this RootGraph of the
   * Nodes to remove.
   * @return An int array of equal length to the argument array, and with equal
   * values except at positions that in the input array contain indices
   * corresponding to Nodes that don't exist in this RootGraph; at these positions the
   * result array will contain the value 0.
   * @see #removeNode(int)
   */
  public int[] removeNodes ( int[] node_indices );

  /**
   * Create a new Node in this RootGraph, and return its index.
   * @return the index of the newly created Node.
   */
  public int createNode();

  /**
   * Create a new Node in this RootGraph, and return its index.  The new Node
   * will be a MetaParent to the Nodes and Edges given, and also to any Nodes
   * incident on the given Edges but omitted from the array.<p>
   * TECHNICAL DETAIL: The Node and Edge input parameters should be objects
   * that were previously returned by methods of this RootGraph or by methods
   * of another component that this RootGraph system defines.  If this is
   * not the case, results of calling this method are undefined.
   * @param nodes a [possibly null] array of Nodes that will be MetaChildren of
   * the newly created Node.
   * @param edges a [possibly null] array of Edges that will be MetaChildren of
   * the newly created Node.
   * @return the index of the newly created Node, or 0 if any of the given
   *   Nodes or Edges are not in this RootGraph.
   */
  public int createNode ( Node[] nodes, Edge[] edges );

  /**
   * Create a new Node in this RootGraph, and return its index.<p>
   * TECHNICAL DETAIL: The GraphPerspective input parameter should be an
   * object that was previously returned by this RootGraph or by a method
   * of another component that this RootGraph system defines.
   * If this is not the case, results of calling
   * this method are undefined.
   * @param perspective a [possibly null] GraphPerspective of this RootGraph,
   * containing the Nodes and Edges that will be MetaChildren of the newly created
   * Node.
   * @return the index of the newly created Node, or 0 if the given
   *   GraphPerspective is not a perspective on this RootGraph.
   */
  public int createNode ( GraphPerspective perspective );

  /**
   * Create a new Node in this RootGraph, and return its index.  The new Node
   * will be a MetaParent to the Nodes and Edges with the given indices, and also
   * to any Nodes incident on the given Edges but omitted from the array.
   * @param node_indices a [possibly null] array of the indices in this
   * RootGraph of the Nodes that will be MetaChildren of the newly created Node.
   * @param edge_indices a [possibly null] array of the indices in this
   * RootGraph of the Edges that will be MetaChildren of the newly created Node.
   * @return the index of the newly created Node, or 0 if any node or edge
   *   index specified is not a valid index of node or edge [respectively]
   *   in this RootGraph.
   */
  public int createNode ( int[] node_indices, int[] edge_indices );

  /**
   * Remove the given Edge from this RootGraph and all of its
   * GraphPerspectives.<p>
   * TECHNICAL DETAIL: The Edge input parameter should be an Edge that was
   * previously returned by a method of this RootGraph or by a method
   * of another component that this RootGraph system defines.  If this is not the case,
   * results of calling this method are undefined.<p>
   * IMPORTANT! The returned Edge object, if not null, should not be used
   * to query fields by calling methods on that returned Edge object - the returned
   * Edge's behavior is completely undefined.  For example,
   * an implementation of RootGraph may choose to return an Edge object
   * whose getRootIndex() methods returns a positive value.  The purpose of the
   * return value of this method is purely to mark "success" or "not success" - in
   * fact, if this API were redesigned, the return value of this operation would become
   * a boolean value.
   * @param edge The Edge to remove.
   * @return A non-null Edge marking a successful removal,
   *   or null if the specified Edge does not belong to this RootGraph.
   */
  public Edge removeEdge ( Edge edge );

  /**
   * Remove the Edge with the given index from this RootGraph and all of its
   * GraphPerspectives.
   * @param edge_index The index in this RootGraph of the Edge to remove.
   * @return The index of the removed Edge, or 0 if the given index does not
   *   correspond to an existing Edge in this RootGraph.
   */
  public int removeEdge(int edge_index);

  /**
   * @see #removeEdge(Edge)
   * @see #removeEdges(int[])
   */
  public List<Edge> removeEdges ( List<Edge> edges );

  /**
   * Remove the Edges with the given indices from this RootGraph and all of its
   * GraphPerspectives.
   * @param edge_indices A non-null array of the indices in this RootGraph of the
   * Edges to remove.
   * @return An array of equal length to the argument array, and with equal
   * values except at positions that in the input array contain indices
   * corresponding to Edges that don't exist in this RootGraph; at these positions the
   * result array will contain the value 0.
   * @see #removeEdge(int)
   */
  public int[] removeEdges ( int[] edge_indices );

  /**
   * Create a directed Edge from the given <tt>source</tt> Node to the given
   * <tt>target</tt> Node, and return its index.  This edge created will be
   * directed, except in the case where the source and target nodes are
   * the same node, in which case the created edge will be undirected.<p>
   * TECHNICAL DETAIL: Each of the two Node input parameters should be a Node that was
   * previously returned by a method of this RootGraph or by a method
   * of another component that this RootGraph system defines.  If this is not the case,
   * results of calling this method are undefined.
   * @param source the source of the new directed Edge
   * @param target the target of the new directed Edge
   * @return the index of the newly created Edge, or 0 if either the
   *   source or target Node is not in this RootGraph.
   */
  public int createEdge ( Node source, Node target );

  /**
   * Create an Edge from the given <tt>source</tt> Node to the given
   * <tt>target</tt> Node, and return its index.  The newly created Edge will
   * be directed iff the boolean argument is true.<p>
   * TECHNICAL DETAIL: Each of the two Node input parameters should be a Node that was
   * previously returned by a method of this RootGraph or by a method
   * of another component that this RootGraph system defines.  If this is not the case,
   * results of calling this method are undefined.
   * @param source the source of the new Edge
   * @param target the target of the new Edge
   * @param directed The new Edge will be directed iff this argument is true.
   * @return the index of the newly created Edge, or 0 if either the source
   *   or target Node is not in this RootGraph.
   */
  public int createEdge ( Node source, Node target, boolean directed );

  /**
   * Create a directed Edge from the Node with the given <tt>source_index</tt>
   * to the Node with the given <tt>target_index</tt>, and return the new
   * Edge's index.  This edge created will be
   * directed, except in the case where the source and target nodes are
   * the same node, in which case the created edge will be undirected.
   * @param source_index the index in this RootGraph of the source of the new
   * directed Edge
   * @param target_index the index in this RootGraph of the target of the new
   * directed Edge
   * @return the index of the newly created Edge, or 0 if either the source
   *   node index or the target node index does not correspond to an existing
   *   Node in this RootGraph.
   */
  public int createEdge ( int source_index, int target_index );

  /**
   * Create an Edge from the Node with the given <tt>source_index</tt> to the
   * Node with the given <tt>target_index</tt>, and return the new Edge's
   * index.  The newly created Edge will be directed iff the boolean argument
   * is true.
   * @param source_index the index in this RootGraph of the source of the new
   * Edge
   * @param target_index the index in this RootGraph of the target of the new
   * Edge
   * @param directed The new Edge will be directed iff this argument is true.
   * @return the index of the newly created Edge, or 0 if either the source
   *   node index or the target node index does not correspond to an existing
   *   Node in this RootGraph.
   */
  public int createEdge(int source_index, int target_index, boolean directed);

  /**
   * Return true if the given Node is in this RootGraph.  False
   * otherwise.<p>
   * TECHNICAL DETAIL: The Node input parameter should be an object
   * that was previously returned by a method of this RootGraph or by
   * a method of another component that this RootGraph system defines.
   * If this is not the case, results of calling this method are undefined.
   * @return true iff the given Node is in this RootGraph.
   */
  public boolean containsNode ( Node node );

  /**
   * Return true if the given Edge is in this RootGraph.  False
   * otherwise.<p>
   * TECHNICAL DETAIL: The Edge input parameter should be an object
   * that was previously returned by a method of this RootGraph or by
   * a method of another component that this RootGraph system defines.
   * If this is not the case, results of calling this method are undefined.
   * @return true iff the given Edge is in this RootGraph.
   */
  public boolean containsEdge ( Edge edge );

  /**
   */
  public List<Node> neighborsList ( Node node );

  /**
   */
  public boolean isNeighbor ( Node a_node, Node another_node );

  /**
   */
  public boolean isNeighbor ( int a_node_index, int another_node_index );

  /**
   */
  public boolean edgeExists ( Node from, Node to );

  /**
   */
  public boolean edgeExists ( int from_node_index, int to_node_index );

  /**
   * Count the number of edges from the first Node to the second.  Note that if
   * count_undirected_edges is false, any Edge <tt><i>e</i></tt> such that
   * <tt><i>e</i>.isDirected() == false</tt> will not be included in the count.
   * @param from the Node that is the source of the edges to be counted.
   * @param to the Node that is the target of the edges to be counted.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the number of Edges from the <tt>from</tt> Node to the 
   * <tt>to</tt> Node; returns -1 if either the <tt>from</tt> or the
   *   <tt>to</tt> Node is not in this RootGraph.
   */
  public int getEdgeCount ( Node from,
                            Node to,
                            boolean count_undirected_edges
                            );

  /**
   * Count the number of edges from the Node with index <tt>from_index</tt> to
   * the Node with index <tt>to_index</tt> (where this.getIndex( to_node ) ==
   * to_index).  Note that if count_undirected_edges is false, any Edge
   * <tt><i>e</i></tt> such that <tt><i>e</i>.isDirected() == false</tt> will
   * not be included in the count.
   * @param from_node_index the index of the Node to count edges from.
   * @param to_node_index the index of the Node to find edges to.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the number of Edges from the Node with index <tt>from_index</tt>
   * to the Node with index <tt>to_index</tt>; returns -1 if either one of the two
   * specified nodes is not in this RootGraph.
   */
  public int getEdgeCount (int from_node_index,
                           int to_node_index,
                           boolean count_undirected_edges
                           );

  /**
   * Returns indices of all edges adjacent to the node at
   * specified index.  See definitions of adjacency below.
   *
   * @param node_index the index of the node whose adjacent edge
   *   information we're seeking.
   * @param undirected_edges edge indices of all adjacent
   *   undirected edges are included in the return value of this
   *   method if this value is true, otherwise not a single
   *   index corresponding to an undirected edge is returned;
   *   undirected edge E is an adjacent undirected edge to node N
   *   [definition:] if and only if E's source is N or E's target
   *   is N.
   * @param incoming_directed_edges edge indices of all incoming
   *   directed edges are included in the return value of this
   *   method if this value is true, otherwise not a single index
   *   corresponding to an incoming directed edge is returned;
   *   directed edge E is an incoming directed edge to node N
   *   [definition:] if and only if N is E's target.
   * @param outgoing_directed_edges edge indices of all outgoing
   *   directed edges are included in the return value of this
   *   method if this value is true, otherwise not a single index
   *   corresponding to an outgoing directed edge is returned;
   *   directed edge E is an outgoing directed edge from node N
   *   [definition:] if and only if N is E's source.
   * @return a set of edge indices corresponding to
   *   edges matched by our query; if all three of the boolean
   *   query parameters are false, the empty array is returned;
   *   null is returned if and only if this RootGraph has no node
   *   at the specified index.
   */
  public int[] getAdjacentEdgeIndicesArray
    (int node_index,
     boolean undirected_edges,
     boolean incoming_directed_edges,
     boolean outgoing_directed_edges);

  /**
   * This will return an array of edge indices that are the edges between nodes.
   * The input node indices array need not be non-repeating.
   * Returns null if any one of the node indices specified does not correspond to
   * a node in this RootGraph.
   */
  public int[] getConnectingEdgeIndicesArray ( int[] node_indices );

  /**
   * @return null is returned if either of the specified Nodes is not in this RootGraph.
   */
  public int[] getEdgeIndicesArray ( 
                                    int from_node_index,
                                    int to_node_index,
                                    boolean include_undirected_edges,
                                    boolean include_both_directions
                                    ) ;

  /**
   * Return a new List of the Edges from the first given Node to the second
   * given Node.
   * @param from the Node that is the source of the Edges to be returned.
   * @param to the Node that is the target of the Edges to be returned.
   * @return a new List of the Edges from the <tt>from</tt> Node to the
   * <tt>to</tt> Node, or the empty List if none exist; null is returned if either
   * of the specified nodes is not in this RootGraph.
   */
  public List<Edge> edgesList ( Node from, Node to );

  /**
   * Return an array of the indices of all Edges from the Node with the first
   * given index to the Node with the second given index.
   * @param from_node_index the index of the Node to return edges from.
   * @param to_node_index the index of the Node to return edges to.
   * @param include_undirected_edges Undirected edges will be included in the
   * List iff include_undirected_edges is true.
   * @return a new List of the Edges from the Node corresponding to
   * <tt>from_node_index</tt> to the Node corresponding to
   * <tt>to_node_index</tt>, or the empty List if none exist; null is returned
   * if either of the specified nodes does not exist in this RootGraph.
   */
  public List<Edge> edgesList (int from_node_index,
                         int to_node_index,
                         boolean include_undirected_edges
                         );

  /**
   * Return an array of the indices of all Edges from the Node with the first
   * given index to the Node with the second given index.
   * @param from_node_index the index of the Node to return edges from.
   * @param to_node_index the index of the Node to return edges to.
   * @param include_undirected_edges Undirected edges will be included in the
   * array iff include_undirected_edges is true.
   * @return an array of the Edges from the Node corresponding to
   * <tt>from_node_index</tt> to the Node corresponding to
   * <tt>to_node_index</tt>; the empty array is returned if none exist; null is
   * returned if either of the specified Nodes does not exist in this RootGraph.
   */
  public int[] getEdgeIndicesArray (int from_node_index,
                                    int to_node_index,
                                    boolean include_undirected_edges
                                    );

  /**
   * Return the number of Edges <tt><i>e</i></tt> such that
   * <tt><i>e</i>.getTarget().equals( node )</tt>.  Note that this includes
   * undirected edges, so it will not always be the case that <tt>getInDegree(
   * node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node the Node to count in-edges of.
   * @return the in-degree of the given Node, or -1 if the specified Node is not
   *   in this RootGraph.
   */
  public int getInDegree ( Node node );

  /**
   * Return the number of Edges <tt><i>e</i></tt> such that
   * <tt><i>e</i>.getTarget().equals( node )</tt>.  Note that this includes
   * undirected edges, so it will not always be the case that <tt>getInDegree(
   * node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count in-edges of.
   * @return the in-degree of the Node with the given index, or -1 if this
   *   RootGraph has no Node with specified index.
   */
  public int getInDegree ( int node_index );

  /**
   * Return the number of Edges <tt><i>e</i></tt> such that
   * <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected_edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node, <b>false</b>
   * ) == getDegree( node )</tt>.
   * @param node the Node to count in-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the in-degree of the given Node or -1 if specified Node is not
   *   in this RootGraph.
   */
  public int getInDegree ( Node node, boolean count_undirected_edges );

  /**
   * Return the number of Edges <tt><i>e</i></tt> such that
   * <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected_edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node, <b>false</b>
   * ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count in-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the in-degree of the Node with the given index or -1 if this
   *   RootGraph has no Node at specified index.
   */
  public int getInDegree ( int node_index, boolean count_undirected_edges );

  /**
   * Return the number of Edges <tt><i>e</i></tt> such
   * that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that this
   * includes undirected edges, so it will not always be the case that
   * <tt>getInDegree( node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node the Node to count out-edges of.
   * @return the out-degree of the given Node, or -1 if specified Node is not
   *   in this RootGraph.
   */
  public int getOutDegree ( Node node );

  /**
   * Return the number of Edges <tt><i>e</i></tt>such that
   * <tt><i>e</i>.getSource().equals( node )</tt>.  Note that this includes
   * undirected edges, so it will not always be the case that <tt>getInDegree(
   * node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count out-edges of.
   * @return the out-degree of the Node with the given index or -1 if index
   *   specified does not correspond to a Node in this RootGraph.
   */
  public int getOutDegree ( int node_index );

  /**
   * Return the number of Edges <tt><i>e</i></tt> such that
   * <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node, <b>false</b>
   * ) == getDegree( node )</tt>.
   * @param node the Node to count out-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the out-degree of the given Node or -1 if specified Node is not
   *   in this RootGraph.
   */
  public int getOutDegree ( Node node, boolean count_undirected_edges );

  /**
   * Return the number of Edges <tt><i>e</i></tt> such that
   * <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node, <b>false</b>
   * ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count out-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the out-degree of the Node with the given index or -1 if this
   *   RootGraph has no Node with specified index.
   */
  public int getOutDegree ( int node_index, boolean count_undirected_edges );

  /**
   * Return the number of distinct Edges incident on the given Node.  By
   * 'distinct' we mean that no Edge will be counted twice, even if it is
   * undirected.
   * @return the degree of the given Node or -1 if specified Node is not in
   *   this RootGraph.
   */
  public int getDegree ( Node node );

  /**
   * Return the number of distinct Edges incident on the Node with the given
   * index.  By 'distinct' we mean that no Edge will be counted twice, even if
   * it is undirected.
   * @return the degree of the Node with the given index or -1 if this
   *   RootGraph has no Node with specified index.
   */
  public int getDegree ( int node_index );

  /**
   * Return the index of the given Node.  Each Node has a unique index which is
   * guaranteed to remain the same throughout the lifetime of the Node and its
   * RootGraph.  Node indices are always <= -1, and are not guaranteed to be
   * contiguous.  This method simply returns node.getRootGraphIndex() after
   * checking that it is in this RootGraph.<p>
   * TECHNICAL DETAIL: The Node input parameter should be an object
   * that was previously returned by a method of this RootGraph or by
   * a method of another component that this RootGraph system defines.
   * If this is not the case, results of calling this method are undefined.
   * @param node the Node to find a corresponding index for.
   * @return the index of the given Node in this RootGraph or 0 if it is not in
   * this RootGraph.
   */
  public int getIndex ( Node node );

  /**
   * Return the Node with the given index in this RootGraph.  All indices are
   * <= -1.  Some indices may correspond to no node, but no index may
   * correspond to multiple nodes.  The index of a Node will not change for the
   * lifetime of the Node and its RootGraph.
   * @param node_index the index in this RootGraph of to find a corresponding
   * Node for.
   * @return the Node with the given index in this RootGraph, or null if there
   *   is no Node with the given index.
   */
  public Node getNode ( int node_index );

  /**
   * Return the index of the given Edge.  Each Edge has a unique index which is
   * guaranteed to remain the same throughout the lifetime of the Edge and its
   * RootGraph.  Edge indices are always <= -1, and are not guaranteed to be
   * contiguous.  This method simply returns edge.getRootGraphIndex() after
   * checking that it is in this RootGraph.<p>
   * TECHNICAL DETAIL: The Edge input parameter should be an object
   * that was previously returned by a method of this RootGraph or by
   * a method of another component that this RootGraph system defines.
   * If this is not the case, results of calling this method are undefined.
   * @param edge the Edge to find a corresponding index for.
   * @return the index of the given Edge in this RootGraph or 0 if it is not in
   * this RootGraph.
   */
  public int getIndex ( Edge edge );

  /**
   * Return the Edge with the given index in this RootGraph.  All indices are
   * <= -1.  Some indices may correspond to no edge, but no index may
   * correspond to multiple edges.  The index of a Edge will not change for the
   * lifetime of the Edge and its RootGraph.
   * @param edge_index the index in this RootGraph of to find a corresponding
   * Edge for.
   * @return the Edge with the given index in this RootGraph, or null if there
   *   if there is no Edge with the given index.
   */
  public Edge getEdge ( int edge_index );

  /**
   * Retrieve the index of the Node that is the source of the Edge with the
   * given index.  Note that if the edge is undirected, the edge also connects
   * the target to the source.
   * @param edge_index the index in this RootGraph of the Edge
   * @return the index in this RootGraph of the Edge's source Node or 0
   *   if there's no Edge at specified index in this RootGraph.
   */
  public int getEdgeSourceIndex ( int edge_index );

  /**
   * Retrieve the index of the Node that is the target of the Edge with the
   * given index.  Note that if the edge is undirected, the edge also connects
   * the target to the source.
   * @param edge_index the index in this RootGraph of the Edge
   * @return the index in this RootGraph of the Edge's target Node or 0
   *   if there's no Edge at specified index in this RootGraph.
   */
  public int getEdgeTargetIndex ( int edge_index );

  /**
   * Retrieve the directedness of the Edge with the
   * given index.  Note that if the edge is undirected, the edge also connects
   * the target to the source.
   * @param edge_index the index in this RootGraph of the Edge
   * @return true iff the edge is directed; if no Edge at specified index exists
   *   in this RootGraph, the result of this method is undefined.
   */
  public boolean isEdgeDirected ( int edge_index );



/*
  File: CytoscapeRootGraph.java
*/

	public Node getNode(String identifier);

	public Edge getEdge(String identifier);

	public void setNodeIdentifier(String identifier, int index);

	public void setEdgeIdentifier(String identifier, int index);
}  
