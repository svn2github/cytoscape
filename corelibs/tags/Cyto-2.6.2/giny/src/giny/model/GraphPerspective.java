package giny.model;
import giny.filter.Filter;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

public interface GraphPerspective {

 
  public void addGraphPerspectiveChangeListener ( GraphPerspectiveChangeListener listener );
  
  public void removeGraphPerspectiveChangeListener ( GraphPerspectiveChangeListener listener );

  public Object clone () ;

 
  /**
   * Return the root Graph for this GraphPerspective
   */
   public RootGraph getRootGraph () ;

 
  /**
   * Returns number of active nodes in this perspective.
   */
  public int getNodeCount () ;
  
 
  /**
   * Returns number of active edges in this perspective.
   */
  public int getEdgeCount () ;
  
 
  /**
   * Returns an Iterator over all giny.model.Node objects in this GraphPerspective.<p>
   * TECHNICAL DETAIL: Iterating over the set of all nodes in a GraphPerspective and
   * manipulating a GraphPerspective's topology (by calling hideXXX() and restoreXXX()
   * methods) concurrently will have undefined effects on the returned Iterator.
   * @return an Iterator over the Nodes in this graph; each Object in the
   *   returned Iterator is of type giny.model.Node.
   */
   public Iterator nodesIterator () ;
   
 
  /**
   * Returns a list of Node objects.
   * @see #nodesIterator()
   */
   public List nodesList () ;
 
  /**
   * Returns an array of length getNodeCount(); the array contains
   * RootGraph indices of Node objects in this GraphPerspective, in some
   * undefined order.
   * @see #nodesIterator()
   * @see Node#getRootGraphIndex()
   */
  public int[] getNodeIndicesArray();
 
  /**
   * @return an Iterator over the Edges in this graph.
   */
   public Iterator edgesIterator () ;
   
  /**
   * Returns a list of Edge objects.
   * @see #edgesIterator()
   */
   public List edgesList () ;
 
  /**
   * Returns an array of length getEdgeCount(); the array contains
   * RootGraph indices of Edge objects in this GraphPerspective, in some
   * undefined order.
   * @see #edgesIterator()
   * @see Edge#getRootGraphIndex()
   */
  public int[] getEdgeIndicesArray();
 
  /**
   * @return null is returned if either of the specified Nodes is not in this
   *   GraphPerspective.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public int[] getEdgeIndicesArray ( 
                                    int from_node_index,
                                    int to_node_index,
                                    boolean include_undirected_edges,
                                    boolean include_both_directions
                                    ) ;
  /**
   * If this GraphPerspective does not hide the given Node, change it so that
   * it does hide the node and all of its incident edges.
   * @param node The Node to hide.
   * @return The given node, unless it was already hidden, in which case
   * null.
   */
   public Node hideNode ( Node node ) ;
 
  /**
   * If this GraphPerspective does not hide the Node with the given index in
   * the underlying RootGraph, change it so that it does hide the node and all of
   * its incident edges.
   * @param node_index The index in the underlying RootGraph of the Node to hide.
   * @return The given index, unless the corresponding Node was already hidden or
   *   does not exist in the underlying RootGraph, in which case 0.
   */
   public int hideNode ( int node_index ) ;
 
  /**
   * @deprecated Use hideNode(Node) or hideNodes(int[]) instead.
   * @see #hideNode(Node)
   * @see #hideNodes(int[])
   */
   public List hideNodes ( List nodes ) ;
 
  /**
   * If this GraphPerspective does not hide any of the Nodes corresponding to
   * the indices in the given array, change it so that it does hide those nodes
   * and all Edges incident on them.
   * Returns an array of equal length to the one given, in which each
   * corresponding position is either the same as in the argument array or is
   * 0, indicating that the node with that index was already hidden.
   * @param node_indices The int array of indices in the underlying RootGraph of
   * the Nodes to hide.
   * @return An int array of equal length to the argument array, and with equal
   * values except at positions that in the input array contain indices
   * corresponding to Nodes that were already hidden; at these positions the
   * result array will contain the value 0.
   */
   public int[] hideNodes ( int[] node_indices ) ;
 
  /**
   * If this GraphPerspective hides the given Node, change it so that it does
   * not hide the node.
   * @param node The Node to restore.
   * @return The given node, unless it was not hidden or it doesn't exist
   *   in the RootGraph, in which case null.
   */
   public Node restoreNode ( Node node ) ;
 
  /**
   * If this GraphPerspective hides the Node with the given index in the
   * underlying RootGraph, change it so that it does not hide the node.
   * @param node_index The index in the underlying RootGraph of the Node to
   *   restore.
   * @return The given index, unless the corresponding Node was already
   *   restored or does not exist in the RootGraph, in which case 0.
   */
   public int restoreNode ( int node_index ) ;
 
  /**
   * @deprecated Use restoreNode(Node) restoreNodes(int[]) instead.
   * @see #restoreNode(Node)
   * @see #restoreNodes(int[])
   */
  public List restoreNodes ( List nodes ) ;
  
  /**
   * @deprecated Use restoreNodes(int[]) and restoreEdges(int[]) instead; to
   *   get edges incident to specified nodes, use
   *   RootGraph.getConnectingEdgeIndicesArray(int[]).
   * @see #restoreNodes(int[])
   * @see #restoreEdges(int[])
   * @see RootGraph#getConnectingEdgeIndicesArray(int[])
   */
  public List restoreNodes (List nodes, boolean restore_incident_edges);
  
  /**
   * If this GraphPerspective hides any of the Nodes with the given RootGraph
   * indices change it so that it does not hide those nodes.
   * @param node_indices The Node indices.
   * @param restore_incident_edges Whether or not the incident edges to the
   *   restored nodes should also be restored.
   * @return An array of length equal to the length of the argument array,
   *   and with equal values except at positions that in the input array
   *   contain indices corresponding to Nodes that were already restored or
   *   don't exist in the RootGraph; at these positions the result array will
   *   contain the value 0.
   * @deprecated Use restoreNodes(int[]) and restoreEdges(int[]) instead; to
   *   get edges incident to specified nodes, use
   *   RootGraph.getConnectingEdgeIndicesArray(int[]).
   * @see #restoreNodes(int[])
   * @see #restoreEdges(int[])
   * @see RootGraph#getConnectingEdgeIndicesArray(int[])
   */
  public int [] restoreNodes (int [] node_indices, boolean restore_incident_edges);
  
  /**
   * If this GraphPerspective hides any of the Nodes corresponding to the
   * indices in the given array, change it so that it does not hide those
   * nodes.  Returns an array of equal length
   * to the one given, in which each corresponding position is either the same
   * as in the argument array or is 0, indicating that the node with that
   * index was already restored or does not exist in the RootGraph.
   * @param node_indices The int array of indices in the underlying RootGraph
   *   of the Nodes to restore.
   * @return An int array of equal length to the argument array, and with equal
   *   values except at positions that in the input array contain indices
   *   corresponding to Nodes that were already restored or don't exist in
   *   the RootGraph; at these positions the
   *   result array will contain the value 0.
   */
   public int[] restoreNodes ( int[] node_indices ) ;
 
  /**
   * If this GraphPerspective does not hide the given Edge, change it so that
   * it does hide the edge.
   * @param edge The Edge to hide.
   * @return The given edge, unless it was already hidden, in which case
   * null.
   */
   public Edge hideEdge ( Edge edge ) ;
 
  /**
   * If this GraphPerspective does not hide the Edge with the given index in
   * the RootGraph, change it so that it does hide the edge.
   * @param edge_index The index in the underlying RootGraph of the Edge to
   *   hide.
   * @return The given index, unless the corresponding Edge was already hidden
   *   or does not exist in the underlying RootGraph, in which case 0.
   */
   public int hideEdge ( int edge_index ) ;
 
  /**
   * @deprecated Use hideEdge(Edge) or hideEdges(int[]) instead.
   * @see #hideEdge(Edge)
   * @see #hideEdges(int[])
   */
   public List hideEdges ( List edges ) ;
 
  /**
   * If this GraphPerspective does not hide any of the Edges corresponding to
   * the indices in the given array, change it so that it does hide those
   * edges.  Returns an array of equal length
   * to the one given, in which each corresponding position is either the same
   * as in the argument array or is 0, indicating that the edge with that
   * index was already hidden.
   * @param edge_indices The int array of indices in the underlying RootGraph of
   * the Edges to hide.
   * @return An int array of equal length to the argument array, and with equal
   * values except at positions that in the input array contain indices
   * corresponding to Edges that were already hidden; at these positions the
   * result array will contain the value 0.
   */
   public int[] hideEdges ( int[] edge_indices ) ;
 
  /**
   * If this GraphPerspective hides the given Edge, change it so that it does
   * not hide the edge or the Nodes on which the edge is incident.
   * @param edge The Edge to restore.
   * @return The given edge, unless it was not hidden or does not exist
   *   in the underlying RootGraph, in which case null.
   */
   public Edge restoreEdge ( Edge edge ) ;
 
  /**
   * If this GraphPerspective hides the Edge with the given index in the
   * underlying RootGraph, change it so that it does not hide the edge or the
   * Nodes on which the edge is incident.
   * @param edge_index The index in the underlying RootGraph of the Edge to
   *   restore.
   * @return The given index, unless the corresponding Edge was already
   *   restored or does not exist in the RootGraph, in which case 0.
   */
   public int restoreEdge ( int edge_index ) ;
 
  /**
   * @deprecated Use restoreEdges(int[]) instead.
   * @see #restoreEdges(int[])
   */
   public List restoreEdges ( List edges ) ;
 
  /**
   * If this GraphPerspective hides any of the Edges corresponding to the
   * indices in the given array, change it so that it does not hide those edges
   * or any of the Nodes on which they are incident.
   * @param edge_indices An array of indices in the underlying RootGraph of
   *   the Edges to restore.
   * @return An int array of equal length to the argument array, and with equal
   *   values except at positions that in the input array contain indices
   *   corresponding to Edges that were already restored or don't exist in the
   *   underlying RootGraph; at these positions the
   *   result array will contain the value 0.
   */
   public int[] restoreEdges ( int[] edge_indices ) ;
 
  /**
   * Return true if the given Node is in this GraphPerspective.  False
   * otherwise.  This method is recursive, so even if this GraphPerspective
   * does hide the Node, this method will return true if the given Node is
   * contained within any non-hidden Node (via the MetaParent->MetaChild
   * relationship) at any depth.
   * This method is equivalent to calling containsNode(Node, boolean)
   * with a true boolean argument.
   * @return true iff the given Node is in this GraphPerspective.
   * @see #containsNode(Node, boolean)
   */
   public boolean containsNode ( Node node ) ;
 
  /**
   * Return true if the given Node is in this GraphPerspective.  False
   * otherwise.  If the <tt>recurse</tt> flag is true then this method will be
   * recursive, so even if this GraphPerspective does hide the Node, this
   * method will return true if the given Node is contained within any
   * non-hidden Node (via the MetaParent->MetaChild relationship) at any depth.  If
   * <tt>recurse</tt> is false then this method will return false iff the
   * given Node is hidden in this GraphPerspective.
   * @return true iff the given Node is in this GraphPerspective.
   */
  public boolean containsNode ( Node node, boolean recurse ) ;
 
  /**
   * Return true if the given Edge is in this GraphPerspective.  False
   * otherwise.  This method is recursive, so even if this GraphPerspective
   * does hide the Edge, this method will return true if the given Edge is
   * contained within any non-hidden Node (via the MetaParent->MetaChild
   * relationship) at any depth.  This method calls {@link #containsEdge( Edge,
   * boolean ) } with a true <tt>recurse</tt> boolean argument.
   * @return true iff the given Edge is in this GraphPerspective.
   */
   public boolean containsEdge ( Edge edge ) ;
 
  /**
   * Return true if the given Edge is in this GraphPerspective.  False
   * otherwise.  If the <tt>recurse</tt> flag is true then this method will be
   * recursive, so even if this GraphPerspective does hide the Edge, this
   * method will return true if the given Edge is contained within any
   * non-hidden Node (via the MetaParent->MetaChild relationship) at any depth.  If
   * <tt>recurse</tt> is false then this method will return false iff the
   * given Edge is hidden in this GraphPerspective.
   */
  public boolean containsEdge ( Edge edge, boolean recurse ) ;
 
  /**
   * Creates a union GraphPerspective.  The given GraphPerspective must have
   * the same RootGraph as this one.
   * @return a new GraphPerspective that contains the union of Nodes and Edges
   * from this GraphPerspective and the given GraphPerspective, or null if
   * the input GraphPerspective does not have the same RootGraph as this one.
   */
   public GraphPerspective join ( GraphPerspective peer ) ;
 
  /**
   * Create a new GraphPerspective with just the given Nodes and Edges (and all
   * Nodes incident on the given Edges).
   * @param nodes A [possibly null] array of Nodes in this GraphPerspective to
   *   include in the new GraphPerspective.
   * @param edges A [possibly null] array of Edges in this GraphPerspective to
   *   include in the new GraphPerspective.
   * @return a new GraphPerspective on this GraphPerspective containing only the
   *   given Nodes and Edges, plus any Nodes incident on the given Edges array but
   *   omitted from the given Nodes array; returns null if any of the specified Nodes
   *   or Edges are not in this GraphPerspective.
   * @deprecated Use RootGraph.createGraphPerspective(int[], int[]) instead.
   * @see RootGraph#createGraphPerspective(int[], int[])
   */
   public GraphPerspective createGraphPerspective ( Node[] nodes,
                                                    Edge[] edges
                                                    );
 
  /**
   * Create a new GraphPerspective with just the Nodes with the given
   * <tt>node_indices</tt> and just the Edges with the given
   * <tt>edge_indices</tt> (and all Nodes incident on the given Edges).
   * @param node_indices A [possibly null] array of [RootGraph] indices
   *   of Nodes in this GraphPerspective to include in the new GraphPerspective.
   * @param edge_indices A [possibly null] array of [RootGraph] indices
   *   of Edges in this GraphPerspective to include in the new GraphPerspective.
   * @return a new GraphPerspective on this GraphPerspective containing only Nodes
   *   and Edges with the given indices, including all Nodes incident on those Edges;
   *   returns null if any of the specified Node or Edge indices do not correspond
   *   to Nodes or Edges existing in this GraphPerspective.
   * @deprecated Use RootGraph.createGraphPerspective(int[], int[]) instead.
   * @see RootGraph#createGraphPerspective(int[], int[])
   */
  public GraphPerspective createGraphPerspective (int[] node_indices,
                                                  int[] edge_indices
                                                  );
 
  /**
   * Create a new GraphPerspective with all of the Nodes from this one that
   * pass the given filter and all of the Edges from this one that pass the
   * filter (and all Nodes incident on those edges).
   */
   public GraphPerspective createGraphPerspective ( Filter filter ) ;
 
  /**
   * @deprecated Use neighborsArray(int) instead;
   * @see #neighborsArray(int)
   */
   public List neighborsList ( Node node ) ;

  /**
   *   Please note that the definition
   *   of "node neighbor" is such: Node A is a "node neighbor" of node B if and only
   *   if there exists an edge [directed or undirected] E such that A is E's target and
   *   B is E's source, or
   *   A is E's source and B is E's target; this method then returns a non-repeating list
   *   of indices of all nodes N in this
   *   GraphPerspective such that N is a "node neighbor" of the node at specified index,
   *   or null if no node at specified index exists in this GraphPerspective.
   *   @param node_index The node whose neighbors you're looking for.
   */
  public int[] neighborsArray ( int node_index );
  
  /**
   *   Please note that the definition
   *   of "node neighbor" is such: Node A is a "node neighbor" of node B if and only
   *   if there exists an edge [directed or undirected] E such that A is E's target and
   *   B is E's source, or A is E's source and B is E's target; this method then returns
   *   true if and only if a_node is a "node neighbor" of another_node in this
   *   GraphPerspective.
   * @param a_node Source node.
   * @param another_node Possible target node.
   */
  public boolean isNeighbor ( Node a_node, Node another_node ) ;
  
  /**
   *   Please note that the definition
   *   of "node neighbor" is such: Node A is a "node neighbor" of node B if and only
   *   if there exists an edge [directed or undirected] E such that A is E's target and
   *   B is E's source, or A is E's source and B is E's target; this method then returns
   *   true if and only if node at index a_node_index is a "node neighbor" of node at
   *   index another_node_index in this GraphPerspective.
   * @param a_node_index Source node index.
   * @param another_node_index Possible target node index.
   */
   public boolean isNeighbor ( int a_node_index, int another_node_index ) ;
 
  /**
   *   This method returns true if and only if
   *   either 1) there exists a directed edge E in this GraphPerspective such that the from
   *   node specified is E's source node and the target node specified is E's target node
   *   or 2) there exists an undirected edge E in this GraphPerspective such that E's
   *   endpoints are the from and to nodes specified.
   * @param from Source node.
   * @param to Possible target node.
   */
  public boolean edgeExists ( Node from, Node to ) ;
 
  /**
   *   This method returns true if and only if
   *   either 1) there exists a directed edge E in this GraphPerspective such that
   *   from_node_index is E's source node's index and to_node_index is E's target node's
   *   index or 2) there exists an undirected edge E in this GraphPerspective such that E's
   *   endpoint nodes have indices from_node_index and to_node_index.
   * @param from_node_index Source node index.
   * @param to_node_index Possible target node index.
   */
  public boolean edgeExists ( int from_node_index, int to_node_index ) ;
 
  /**
   * Count the number of edges from the first Node to the second.  Note that if
   * count_undirected_edges is false, any Edge <tt><i>e</i></tt> such that
   * <tt><i>e</i>.isDirected() == false</tt> will not be included in the count.
   * @param from the Node in this GraphPerspective that is the source of the
   * edges to be counted.
   * @param to the Node in this GraphPerspective that is the target of the
   * edges to be counted.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the number of Edges from the <tt>from</tt> Node to the 
   * <tt>to</tt> Node; returns -1 if either the <tt>from</tt> or the
   * <tt>to</tt> Node is not in this GraphPerspective.
   */
  public int getEdgeCount (Node from,
                           Node to,
                           boolean count_undirected_edges
                           );
 
  /**
   * Count the number of edges from the Node with index <tt>from_index</tt> to
   * the Node with index <tt>to_index</tt> (where this.getIndex( to_node ) ==
   * to_index).  Note that if count_undirected_edges is false, any Edge
   * <tt><i>e</i></tt> such that <tt><i>e</i>.isDirected() == false</tt> will
   * not be included in the count.
   * @param from_node_index the index in this GraphPerspective of the Node to
   * count edges from.
   * @param to_node_index the index in this GraphPerspective of the Node to
   * find edges to.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the number of Edges from the Node with index <tt>from_index</tt>
   * to the Node with index <tt>to_index</tt>; returns -1 if either one of the two
   * specified nodes is not in this GraphPerspective.
   */
  public int getEdgeCount ( int from_node_index,
                            int to_node_index,
                            boolean count_undirected_edges
                            ) ;
 
  /**
   * Return a new List of the Edges in this GraphPerspective from the first
   * given Node to the second given Node.
   * @param from the Node in this GraphPerspective that is the source of the
   * Edges to be returned.
   * @param to the Node in this GraphPerspective that is the target of the
   * Edges to be returned.
   * @return a new List of the Edges from the <tt>from</tt> Node to the
   * <tt>to</tt> Node, or the empty List if none exist; null is returned if either
   * of the specified nodes is not in this GraphPerspective.
   */
   public List edgesList ( Node from, Node to ) ;
 
  /**
   * Return an array of the indices in this GraphPerspective of all Edges from
   * the Node with the first given index to the Node with the second given
   * index.
   * @param from_node_index the index in this GraphPerspective of the Node to
   * return edges from.
   * @param to_node_index the index in this GraphPerspective of the Node to
   * return edges to.
   * @param include_undirected_edges Undirected edges will be included in the
   * List iff include_undirected_edges is true.
   * @return a new List of the Edges from the Node corresponding to
   * <tt>from_node_index</tt> to the Node corresponding to
   * <tt>to_node_index</tt>, or the empty List if none exist; null is returned
   * if either of the specified nodes does not exist in this GraphPerspective.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public List edgesList (int from_node_index,
                         int to_node_index,
                         boolean include_undirected_edges
                         );
 
  /**
   * Return an array of the indices in this GraphPerspective of all Edges from
   * the Node with the first given index to the Node with the second given
   * index.
   * <br>
   * The result should be considered final; it <b>must not</b> be modified by
   * the receiver.
   * @param from_node_index the index in this GraphPerspective of the Node to
   * return edges from.
   * @param to_node_index the index in this GraphPerspective of the Node to
   * return edges to.
   * @param include_undirected_edges Undirected edges will be included in the
   * array iff include_undirected_edges is true.
   * @return an array of indices corresponding to Edges from the Node at index
   * <tt>from_node_index</tt> to the Node at index
   * <tt>to_node_index</tt>; the empty array is returned if no such Edges exist;
   * null is returned if either of the specified Nodes does not exist in this
   * GraphPerspective.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
   public int[] getEdgeIndicesArray (int from_node_index,
                                     int to_node_index,
                                     boolean include_undirected_edges
                                     );
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective such
   * that <tt><i>e</i>.getTarget().equals( node )</tt>.  Note that this
   * includes undirected edges, so it will not always be the case that
   * <tt>getInDegree( node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node the Node to count in-edges of.
   * @return the in-degree of the given Node, or -1 if the specified Node is not
   *   in this GraphPerspective.
   */
   public int getInDegree ( Node node ) ;
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective such
   * that <tt><i>e</i>.getTarget().equals( node )</tt>.  Note that this
   * includes undirected edges, so it will not always be the case that
   * <tt>getInDegree( node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count in-edges of.
   * @return the in-degree of the Node with the given index, or -1 if this
   *   GraphPerspective has no Node with specified index.
   */
   public int getInDegree ( int node_index ) ;
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective
   * such that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected_edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node, <b>false</b>
   * ) == getDegree( node )</tt>.
   * @param node the Node to count in-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the in-degree of the given Node or -1 if specified Node is not
   *   in this GraphPerspective.
   */
   public int getInDegree ( Node node, boolean count_undirected_edges ) ;
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective
   * such that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected_edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node, <b>false</b>
   * ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count in-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the in-degree of the Node with the given index or -1 if this 
   *   GraphPerspective has no Node at specified index.
   */
   public int getInDegree ( int node_index, boolean count_undirected_edges ) ;
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective such
   * that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that this
   * includes undirected edges, so it will not always be the case that
   * <tt>getInDegree( node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node the Node to count out-edges of.
   * @return the out-degree of the given Node, or -1 if specified Node is not
   *   in this GraphPerspective.
   */
   public int getOutDegree ( Node node ) ;
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective such
   * that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that this
   * includes undirected edges, so it will not always be the case that
   * <tt>getInDegree( node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count out-edges of.
   * @return the out-degree of the Node with the given index or -1 if index
   *   specified does not correspond to a Node in this GraphPerspective.
   */
   public int getOutDegree ( int node_index ) ;
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective
   * such that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node,
   * <b>false</b> ) == getDegree( node )</tt>.
   * @param node the Node to count out-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the out-degree of the given Node or -1 if specified Node is not
   *   in this GraphPerspective.
   */
   public int getOutDegree ( Node node, boolean count_undirected_edges ) ;
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> in this GraphPerspective
   * such that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that if
   * count_undirected edges is true, this includes undirected edges, so it will
   * not always be the case that <tt>getInDegree( node, true ) + getOutDegree(
   * node, true ) == getDegree( node )</tt>, but it <i>will</i> always be the
   * case that <tt>getInDegree( node, true ) + getOutDegree( node,
   * <b>false</b> ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count out-edges of.
   * @param count_undirected_edges Undirected edges will be included in the
   * count iff count_undirected_edges is true.
   * @return the out-degree of the Node with the given index or -1 if this
   *   GraphPerspective has no Node with specified RootGraph index.
   */
   public int getOutDegree ( int node_index, boolean count_undirected_edges ) ;
 
  /**
   * Return the number of distinct Edges in this GraphPerspective incident on
   * the given Node.  By 'distinct' we mean that no Edge will be counted twice,
   * even if it is undirected.
   * @return the degree, in this GraphPerspective, of the given Node, or -1 if
   *   specified Node is not in this GraphPerspective.
   */
   public int getDegree ( Node node ) ;
 
  /**
   * Return the number of distinct Edges in this GraphPerspective incident on
   * the Node with the given index.  By 'distinct' we mean that no Edge will be
   * counted twice, even if it is undirected.
   * @return the degree, in this GraphPerspective, of the Node with the given
   * index, or -1 if this GraphPerspective has no Node with specified [RootGraph]
   * index.
   */
   public int getDegree ( int node_index ) ;
 
  /**
   * Return the index of the given Node in the underlying RootGraph.
   * If the Node is hidden in this perspective, the result will be 0.
   * @param node the Node to find a corresponding index for.
   * @return the index of the given Node in the RootGraph
   *   (node.getRootGraphIndex()), or 0 if it is hidden or does not exist
   *   in the underlying RootGraph.
   */
   public int getIndex ( Node node ) ;
 
  /**
   * @deprecated Use getRootGraphNodeIndex(int), whose functionality is
   *   identical.
   * @see #getRootGraphNodeIndex(int)
   */
   public int getNodeIndex ( int root_graph_node_index ) ;
 
  /**
   * This method returns the input parameter if and only if a Node at the
   * specified RootGraph index exists in this GraphPerspective; otherwise 0
   * is returned.
   */
   public int getRootGraphNodeIndex ( int root_graph_node_index ) ;
 
  /**
   * Return a Node which is in this GraphPerspective.
   * in this GraphPerspective.
   * @param index the index into the underlying RootGraph to find a
   *   corresponding GraphPerspective Node for.
   * @return the Node in this GraphPerspective, or null if
   *   no such Node exists in this GraphPerspective.
   */
   public Node getNode ( int index ) ;
 
  /**
   * Return the index of the given Edge in the underlying RootGraph.
   * If the Edge is hidden in this perspective, the result will be 0.
   * @param edge the Edge to find a corresponding index for.
   * @return the index of the given Edge in the RootGraph
   *   (edge.getRootGraphIndex()), or 0 if it is hidden or does not exist
   *   in the underlying RootGraph.
   */
   public int getIndex ( Edge edge ) ;
 
  /**
   * @deprecated Use getRootGraphEdgeIndex(int), whose functionality is
   *   identical.
   * @see #getRootGraphEdgeIndex(int)
   */
   public int getEdgeIndex ( int root_graph_edge_index ) ;
 
  /**
   * This method returns the input parameter if and only if an Edge at the
   * specified RootGraph index exists in this GraphPerspective; otherwise 0 is
   * returned.
   */
   public int getRootGraphEdgeIndex ( int root_graph_edge_index ) ;
 
  /**
   * Return an Edge which is in this GraphPerspective.
   * @param index the index into the underlying RootGraph to find a
   *   corresponding GraphPerspective Edge for.
   * @return the Edge in this GraphPerspective, or null if
   *   no such Edge exists in this GraphPerspective.
   */
   public Edge getEdge ( int index ) ;
 
  /**
   * Retrieve the index of the Node that is the source of the Edge in this
   * GraphPerspective with the given index.  Note that if the edge is
   * undirected, the edge also connects the target to the source.
   * @param edge_index the [RootGraph] index of the Edge in this GraphPerspective.
   * @return the index of the Edge's source Node, or 0
   * if the Edge is not in this GraphPerspective.
   */
  public int getEdgeSourceIndex ( int edge_index ) ;
 
  /**
   * Retrieve the index of the Node that is the target of the Edge in this
   * GraphPerspective with the given index.  Note that if the edge is
   * undirected, the edge also connects the target to the source.
   * @param edge_index the [RootGraph] index of the Edge in this GraphPerspective.
   * @return the index of the Edge's target Node, or 0
   * if the Edge is not in this GraphPerspective.
   */
  public int getEdgeTargetIndex ( int edge_index ) ;
 
  /**
   * Retrieve the directedness of the Edge in this GraphPerspective with the
   * given [RootGraph] index.  Note that if the edge is undirected, the edge also
   * connects the target to the source.
   * @param edge_index the [RootGraph] index of the Edge in this GraphPerspective.
   * @return true iff the edge is directed; if no Edge at specified index exists
   *   in this GraphPerspective, the result of this method is undefined.
   */
  public boolean isEdgeDirected ( int edge_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * isMetaParent returns true iff the second argument (<tt>parent</tt>) is an
   * MetaParent of the first argument (<tt>child</tt>) and both Nodes are in
   * this GraphPerspective.
   * @param child the Node that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaParent of the former argument
   * in this GraphPerspective.
   */
   public boolean isMetaParent ( Node child, Node parent ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * isMetaParent returns true iff the Node corresponding to the second argument
   * (<tt>parent_index</tt>) is a MetaParent of the Node corresponding to the
   * first argument (<tt>child_index</tt>) and both Nodes are in this GraphPerspective.
   * @param child_node_index the index in the RootGraph of the Node
   * that is the child (the contain<i>ee</i>) in the contains-a relationship
   * that we are querying.
   * @param parent_index the index in the RootGraph of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @return true iff the Node corresponding to the latter argument is an
   * MetaParent (in this GraphPerspective) of the Node corresponding to the former
   * argument.
   */
   public boolean isNodeMetaParent ( int child_node_index, int parent_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * metaParentsList returns a new List of the MetaParents (in this GraphPerspective)
   * of the given Node.  If there are no MetaParents then the result will be the empty
   * List - null is returned of the specified Node is not in this GraphPerspective.
   * @param node the Node that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Nodes in this GraphPerspective that contain the
   * given Node.
   */
   public List metaParentsList ( Node node ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * nodeMetaParentsList returns a new List of the MetaParents (in this
   * GraphPerspective) of the Node in this GraphPerspective with the given
   * index.  If there are no MetaParents then the result will be the empty
   * List; null is returned if there is no Node in this GraphPerspective with
   * the specified RootGraph index.
   * @param node_index the index in the RootGraph of the Node that is
   * the child (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return a new List of the Nodes in this GraphPerspective that contain the
   * Node with the given index.
   */
   public List nodeMetaParentsList ( int node_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * getNodeMetaParentIndicesArray returns an array of the MetaParents (in this
   * GraphPerspective) of the Node with the given index.  If there are no
   * MetaParents then the result will be the empty array; null is returned if
   * there is no Node in this GraphPerspective with specified RootGraph index.
   * @param node_index the index in the underlying RootGraph of the Node that is
   * the child (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return an array of the indices of the Nodes in this GraphPerspective that
   * contain the Node with the given index.
   */
   public int[] getNodeMetaParentIndicesArray ( int node_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the second argument (<tt>child</tt>) is an
   * MetaChild of the first argument (<tt>parent</tt>) and both Nodes are in this
   * GraphPerspective.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @param child the Node that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaChild of the former argument
   * in this GraphPerspective.
   */
   public boolean isMetaChild ( Node parent, Node child ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the Node corresponding to the second argument
   * (<tt>child_index</tt>) is a MetaChild of the Node corresponding to the first
   * argument (<tt>parent_index</tt>) in this GraphPerspective.
   * @param parent_index the index in the RootGraph of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @param child_node_index the index in the RootGraph of the Node
   * that is the child (the contain<i>ee</i>) in the contains-a relationship
   * that we are querying.
   * @return true iff the Node corresponding to the latter argument is an
   * MetaChild (in this GraphPerspective) of the Node corresponding to the former
   * argument.
    */
   public boolean isNodeMetaChild ( int parent_index, int child_node_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * nodeMetaChildrenList returns a new List of the MetaChildren (in this
   * GraphPerspective) of the given Node.  If there are no MetaChildren then the
   * result will be the empty List; null is returned if the specified Node does not
   * exist in this GraphPerspective.
   * @param node the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Nodes in this GraphPerspective that are
   * contained by the given Node.
   */
   public List nodeMetaChildrenList ( Node node ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * nodeMetaChildrenList returns a new List of the Node MetaChildren (in this
   * GraphPerspective) of the Node in this GraphPerspective with the given
   * index.  If there are no MetaChildren then the result will be the empty List;
   * null is returned if no Node at specified [RootGraph] index exists in this
   * GraphPerspective.
   * @param parent_index the index in the underlying RootGraph of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @return a new List of the Nodes in this GraphPerspective that are
   * contained by the Node with the given index.
   */
   public List nodeMetaChildrenList ( int parent_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * getNodeMetaChildIndicesArray returns an array of the MetaChildren (in this
   * GraphPerspective) of the Node with the given index.  If there are no
   * MetaChildren then the result will be the empty array; null is returned if
   * this GraphPerspetive has no Node at specified RootGraph index.
   * @param node_index the index in the RootGraph of the Node that is
   * the parent (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return an array of the indices of the Nodes in this GraphPerspective that
   * are contained by the Node with the given index.
   */
   public int[] getNodeMetaChildIndicesArray ( int node_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * isMetaParent returns true iff the second argument (<tt>parent</tt>) is an
   * MetaParent of the first argument (<tt>child</tt>) and both elements are in
   * this GraphPerspective.
   * @param child the Edge that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaParent of the former argument
   * in this GraphPerspective.
   */
   public boolean isMetaParent ( Edge child, Node parent ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * isMetaParent returns true iff the Node corresponding to the second argument
   * (<tt>parent_index</tt>) is a MetaParent of the Edge corresponding to the
   * first argument (<tt>child_index</tt>) and both Edge and Node are in this
   * GraphPerspective.
   * @param child_edge_index the index in the RootGraph of the Edge
   * that is the child (the contain<i>ee</i>) in the contains-a relationship
   * that we are querying.
   * @param parent_index the index in the RootGraph of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @return true iff the Node corresponding to the latter argument is an
   * MetaParent (in this GraphPerspective) of the Edge corresponding to the former
   * argument.
   */
   public boolean isEdgeMetaParent ( int child_edge_index, int parent_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * metaParentsList returns a new List of the MetaParents (in this GraphPerspective)
   * of the given Edge.  If there are no MetaParents then the result will be the
   * empty List; null is returned if specified Edge is not in this GraphPerspective.
   * @param edge the Edge that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Nodes in this GraphPerspective that contain the
   * given Edge.
   */
   public List metaParentsList ( Edge edge ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * EdgeMetaParentsList returns a new List of the MetaParents (in this
   * GraphPerspective) of the Edge in this GraphPerspective with the given
   * index.  If there are no MetaParents then the result will be the
   * empty List; null is returned if no Edge at specified RootGraph index exists
   * in this GraphPerspective.
   * @param edge_index the index in the RootGraph of the Edge that is
   * the child (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return a new List of the Nodes in this GraphPerspective that contain the
   * Edge with the given index.
   */
   public List edgeMetaParentsList ( int edge_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * getEdgeMetaParentIndicesArray returns an array of the MetaParents (in this
   * GraphPerspective) of the Edge with the given index.  If there are no
   * MetaParents then the result will be the empty array; null is returned if
   * no Edge at specified RootGraph index exists in this GraphPerspective.
   * @param edge_index the index in the RootGraph of the Edge that is
   * the child (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return an array of the indices of the Nodes in this GraphPerspective that
   * contain the Edge with the given index.
   */
  public int[] getEdgeMetaParentIndicesArray ( int edge_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the second argument (<tt>child</tt>) is an
   * MetaChild of the first argument (<tt>parent</tt>) and both Node and Edge
   * are in this GraphPerspective.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @param child the Edge that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaChild of the former argument
   * in this GraphPerspective.
   */
   public boolean isMetaChild ( Node parent, Edge child ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the Edge corresponding to the second argument
   * (<tt>child_index</tt>) is a MetaChild of the Node corresponding to the first
   * argument (<tt>parent_index</tt>) in this GraphPerspective.
   * @param parent_index the index in the RootGraph of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @param child_edge_index the index in the RootGraph of the Edge
   * that is the child (the contain<i>ee</i>) in the contains-a relationship
   * that we are querying.
   * @return true iff the Edge corresponding to the latter argument is an
   * MetaChild (in this GraphPerspective) of the Node corresponding to the former
   * argument.
   */
   public boolean isEdgeMetaChild ( int parent_index, int child_edge_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * edgeMetaChildrenList returns a new List of the Edge MetaChildren (in this
   * GraphPerspective) of the given Node.  If there are no Edge MetaChildren then
   * the result will be the empty List; returns null if specified Node is not in
   * this GraphPerspective.
   * @param node the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Edges in this GraphPerspective that are
   * contained by the given Node.
   */
   public List edgeMetaChildrenList ( Node node ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * edgeMetaChildrenList returns a new List of the Edge MetaChildren (in this
   * GraphPerspective) of the Node in this GraphPerspective with the given
   * index.  If there are no Edge MetaChildren then the result will be the
   * empty List; null is returned if no Node exists in this GraphPerspective at
   * specified RootGraph index.
   * @param node_index the index in the RootGraph of the Node that is
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are querying.
   * @return a new List of the Edges in this GraphPerspective that are
   * contained by the Node with the given index.
   */
   public List edgeMetaChildrenList ( int node_index ) ;
 
  /**
   * Nodes and Edges comprise an additional directed graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * getEdgeMetaChildIndicesArray returns an array of the MetaChildren (in this
   * GraphPerspective) of the Node with the given index.  If there are no
   * MetaChildren then the result will be the empty array; null is returned if no
   * Node exists in this GraphPerspective at the specified RootGraph index.
   * @param node_index the index in the RootGraph of the Node that is
   * the parent (the contain<i>ee</i>) in the contains-a relationship that we
   * are querying.
   * @return an array of the indices of the Edges in this GraphPerspective that
   * are contained by the Node with the given index.
   */
   public int[] getEdgeMetaChildIndicesArray ( int node_index ) ;


   /**
   * Returns all Adjacent Edges to the given node.
   * @param node the  node
   * @param include_undirected_edges should we include undirected edges
   * @param incoming_edges Include incoming edges
   * @param outgoing_edges Include outgoing edges
   * @return a List of giny.model.Edge objects; an empty List is returned if
   *   no adjacent Edges are found; null is returned if the specified Node does not
   *   exist in this GraphPerspective.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public List getAdjacentEdgesList ( Node node, boolean include_undirected_edges, boolean incoming_edges, boolean outgoing_edges );

  /**
   * Returns [RootGraph] indices of all Edges in this GraphPerspective
   * adjacent to the Node at specified [RootGraph] index.
   * See definitions of adjacency below.
   *
   * @param node_index the [RootGraph] index of the Node whose adjacent
   *   Edge information we're seeking.
   * @param undirected_edges Edge indices of all adjacent
   *   undirected Edges are included in the return value of this
   *   method if this value is true, otherwise not a single
   *   index corresponding to an undirected Edge is returned;
   *   undirected Edge E is an adjacent undirected Edge to Node N
   *   [definition:] if and only if E's source is N or E's target
   *   is N.
   * @param incoming_directed_edges Edge indices of all incoming
   *   directed Edges are included in the return value of this
   *   method if this value is true, otherwise not a single index
   *   corresponding to an incoming directed Edge is returned;
   *   directed Edge E is an incoming directed Edge to Node N
   *   [definition:] if and only if N is E's target.
   * @param outgoing_directed_edges Edge indices of all outgoing
   *   directed Edges are included in the return value of this
   *   method if this value is true, otherwise not a single index
   *   corresponding to an outgoing directed Edge is returned;
   *   directed Edge E is an outgoing directed Edge from Node N
   *   [definition:] if and only if N is E's source.
   * @return a set of Edge [RootGraph] indices corresponding to
   *   Edges matched by our query; if all three of the boolean
   *   query parameters are false, the empty array is returned;
   *   null is returned if and only if this GraphPerspective has no Node
   *   at the specified [RootGraph] index.
   */
  public int[] getAdjacentEdgeIndicesArray ( int node_index,
                                             boolean undirected_edges,
                                             boolean incoming_directed_edges,
                                             boolean outgoing_directed_edges );


  /**
   * This will return a List of giny.model.Edge objects that are the Edges between Nodes.
   */
  public List getConnectingEdges ( List nodes );

  /**
   * This will return an array of Edge indices that are the Edges between Nodes.
   */
  public int[] getConnectingEdgeIndicesArray ( int[] node_indices );
 
  /**
   * Return the Nodes that connect the given Edges in this GraphPerspective.
   * @deprecated Use getEdgeSourceIndex(int) and getEdgeTargetIndex(int) instead.
   * @see #getEdgeSourceIndex(int)
   * @see #getEdgeTargetIndex(int)
   */
  public int[] getConnectingNodeIndicesArray ( int[] edge_indices );
  
  /**
   * Create a new GraphPerspective given a list of Nodes.  This method
   * will automatically find all the interconnected Edges.
   * Returns null if any of the specified Nodes are not in this GraphPerspective.
   */
  public GraphPerspective createGraphPerspective( int[] node_indices );

}
