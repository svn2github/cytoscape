package cytoscape.giny;

import cytoscape.*;
import giny.model.*;
import fing.model.*;

import giny.model.Node;
import giny.model.Edge;

import cern.colt.map.*;

import java.util.*;

public class CytoscapeRootGraph 
  implements 
    RootGraph {

  
  // no doubt there is a better way, do it later
  private OpenIntObjectHashMap nodes;
  private OpenIntObjectHashMap edges;
  
  // this is the RootGraph that we are wrapping
  private RootGraph root;

  public CytoscapeRootGraph () {

    // this is where we get the RootGraph
    root = FingRootGraphFactory.instantiateRootGraph();

    // instantiate the Storage for node and edges
    nodes = new OpenIntObjectHashMap();
    edges = new OpenIntObjectHashMap();
  }


  public CyNode replaceNode ( int index, CyNode new_node ) {
    CyNode old_node = ( CyNode )nodes.get( index );
    if ( old_node.getRootGraphIndex() == index ) {
      nodes.put( index, new_node );
      return new_node;
    } else {
      return old_node;
    }
  }

  public int createNode ( CyNetwork network ) {
    return createNode( ( GraphPerspective )network );
  }

  /**
   * Uses Code copied from ColtRootGraph to create a new CyNetwork.
   */
  public CyNetwork createNetwork ( Collection nodes, Collection edges ) {
    return ( CyNetwork )createGraphPerspective( cyNodeList2intArray( nodes ), cyEdgeList2intArray( edges ) );
  }

  /**
   * Uses Code copied from ColtRootGraph to create a new Network.
   */
  public CyNetwork createNetwork ( int[] node_indices, int[] edge_indices ) {
    return ( CyNetwork )createGraphPerspective( node_indices, edge_indices );
  }


  ////////////////////
  // Root Graph Methods

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
  public GraphPerspective createGraphPerspective (
    Node[] nodes,
    Edge[] edges
    ) {
    
    List node_list = Arrays.asList( nodes );
    List edge_list = Arrays.asList( edges );

    return createGraphPerspective( cyNodeList2intArray( node_list ), cyEdgeList2intArray( edge_list ) );
  }
  
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
    ) {
    GraphPerspective gp = root.createGraphPerspective( node_indices,
                                                       edge_indices );
    CyNetwork network = new CyNetwork( gp, this );
    return network;
  }


  /**
   * @deprecated Please don't call this silly method; implementors: I suggest
   *   creating an easter egg and having it take effect when this method is called.
   */
  public void ensureCapacity( int nodes, int edges ) {
    root.ensureCapacity( nodes, edges );
  }

  /**
   * Returns number of nodes in this RootGraph.  A call to nodesIterator()
   * will return an Iterator containing exactly getNodeCount() elements
   * (unless nodes are created or removed in the meantime).
   * @return the number of nodes in this RootGraph.
   */
  public int getNodeCount () {
    return root.getNodeCount();
  }
 
  /**
   * Returns number of edges in this RootGraph.  A call to edgesIterator()
   * will return an iterator containing exactly getEdgeCount() elements
   * (unless nodes or edges are created or removed in the meantime).
   * @return the number of edges in this RootGraph.
   */
  public int getEdgeCount () {
    return root.getEdgeCount();
  }
 
  /**
   * Returns an Iterator over all giny.model.Node objects in this RootGraph.<p>
   * TECHNICAL DETAIL:  Iterating over the set of all nodes in a RootGraph and
   * manipulating a RootGraph's topology (by calling removeXXX() and createXXX() methods)
   * concurrently will have undefined effects on the returned Iterator.
   * @return an Iterator over the Nodes in this graph; each Object in the
   *   returned Iterator is of type giny.model.Node.
   */
  public Iterator nodesIterator () {
    //MOD
    int[] node_indices = getNodeIndicesArray();
    return intArray2CyNodeList( node_indices ).iterator();
  }
 
  /**
   * @deprecated Use nodesIterator() instead.
   * @see #nodesIterator()
   */
  public List nodesList () {
    int[] node_indices = getNodeIndicesArray();
    return intArray2CyNodeList( node_indices );
  }
 
  /**
   * @deprecated Use nodesIterator() together with Node.getRootGraphIndex().
   * @see #nodesIterator()
   * @see Node#getRootGraphIndex()
   */
  public int[] getNodeIndicesArray () {
    return root.getNodeIndicesArray();
  }
 
  /**
   * Returns an Iterator over all giny.model.Edge objects in this RootGraph.<p>
   * TECHNICAL DETAIL:  Iterating over the set of all edges in a RootGraph and
   * manipulating a RootGraph's topology (by calling removeXXX() and createXXX()
   * methods) concurrently will have undefined effects on the returned Iterator.
   * @return an Iterator over the Edges in this graph; each Object in the
   *   returned Iterator is of type giny.model.Edge.
   */
  public Iterator edgesIterator () {
    int[] edge_indices = getEdgeIndicesArray();
    return intArray2CyEdgeList( edge_indices ).iterator();
  }
 
  /**
   * @deprecated Use edgesIterator() instead.
   * @see #edgesIterator()
   */
  public List edgesList () {
    int[] edge_indices = getEdgeIndicesArray();
    return intArray2CyEdgeList( edge_indices );
  }
 
  /**
   * @deprecated Use edgesIterator() together with Edge.getRootGraphIndex().
   * @see #edgesIterator()
   * @see Edge#getRootGraphIndex()
   */
  public int[] getEdgeIndicesArray () {
    return root.getEdgeIndicesArray();
  }
 
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
  public Node removeNode ( Node node ) {
    
    CyNode cynode = ( CyNode )nodes.get( node.getRootGraphIndex() );
    nodes.removeKey( node.getRootGraphIndex() );
    root.removeNode( node.getRootGraphIndex() );
    return cynode;
  }
 
  /**
   * Remove the Node with the given index (and all of that Node's incident
   * Edges) from this RootGraph and all of its GraphPerspectives.
   * @param node_index The index in this RootGraph of the Node to remove.
   * @return The index of the removed Node, or 0 if the given index does not
   *   correspond to an existing Node in this RootGraph.
   */
  public int removeNode ( int node_index ) {
    nodes.removeKey( node_index );
    return root.removeNode( node_index );
  }
 
  /**
   * @deprecated Use removeNode(Node) or removeNodes(int[]) instead.
   * @see #removeNode(Node)
   * @see #removeNodes(int[])
   */
  public List removeNodes ( List nodes ) {
    for ( Iterator i = nodes.iterator(); i.hasNext(); ) {
      removeNode( ( Node )i.next() ); 
    }
    return nodes;
  }
 
  /**
   * Remove the Nodes with the given indices (and all of those Nodes' incident
   * Edges) from this RootGraph and all of its GraphPerspectives.
   * @param node_indices An non-null array of the indices in this RootGraph of the
   * Nodes to remove.
   * @return An int array of equal length to the argument array, and with equal
   * values except at positions that in the input array contain indices
   * corresponding to Nodes that don't exist in this RootGraph; at these positions the
   * result array will contain the value 0.
   */
  public int[] removeNodes ( int[] node_indices ) {
    for ( int i = 0; i < node_indices.length; ++i ) {
      nodes.removeKey( node_indices[i] );
    }
    return root.removeNodes( node_indices );
  }
 
  /**
   * Create a new Node in this RootGraph, and return its index.
   * @return the index of the newly created Node.
   */
  public int createNode () {
    return root.createNode();
  }
 
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
  public int createNode ( Node[] nodes, Edge[] edges ) {
    return 0;
  }
 
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
  public int createNode ( GraphPerspective perspective ) {
    return root.createNode( perspective );
  }
 
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
  public int createNode ( int[] node_indices, int[] edge_indices ) {
    return root.createNode( node_indices,
                            edge_indices );
  }
 
  /**
   * Create <tt>new_node_count</tt> new Nodes in this RootGraph, and return
   * their indices.
   * @param new_node_count the number of new nodes to create.
   * @return an array of length new_node_count containing the indices of the
   * newly created Nodes.
   */
  public int[] createNodes ( int new_node_count ) {
    return root.createNodes( new_node_count );
  }
 
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
  public Edge removeEdge ( Edge edge ) {
    CyEdge cyedge = ( CyEdge )edges.get( edge.getRootGraphIndex() );
    edges.removeKey( edge.getRootGraphIndex() );
    root.removeEdge( edge.getRootGraphIndex() );
    return cyedge;
  }
 
  /**
   * Remove the Edge with the given index from this RootGraph and all of its
   * GraphPerspectives.
   * @param edge_index The index in this RootGraph of the Edge to remove.
   * @return The index of the removed Edge, or 0 if the given index does not
   *   correspond to an existing Edge in this RootGraph.
   */
  public int removeEdge ( int edge_index ) {
    edges.removeKey( edge_index );
    return root.removeEdge( edge_index );
  }
 
  /**
   * @deprecated Use removeEdge(Edge) or removeEdges(int[]) instead.
   * @see #removeEdge(Edge)
   * @see #removeEdges(int[])
   */
  public List removeEdges ( List edges ) {
    for ( Iterator i = edges.iterator(); i.hasNext(); ) {
      removeEdge( ( Edge )i.next() );
    }
    return edges;
  }
 
  /**
   * Remove the Edges with the given indices from this RootGraph and all of its
   * GraphPerspectives.
   * @param edge_indices A non-null array of the indices in this RootGraph of the
   * Edges to remove.
   * @return An array of equal length to the argument array, and with equal
   * values except at positions that in the input array contain indices
   * corresponding to Edges that don't exist in this RootGraph; at these positions the
   * result array will contain the value 0.
   */
  public int[] removeEdges ( int[] edge_indices ) {
    for ( int i = 0; i < edge_indices.length; ++i ) {
      edges.removeKey( edge_indices[i] );
    }
    return root.removeEdges( edge_indices );
  }
 
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
  public int createEdge ( Node source, Node target ) {
    return root.createEdge( source.getRootGraphIndex(), target.getRootGraphIndex() );
  }
 
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
  public int createEdge ( Node source, Node target, boolean directed ) {
    return root.createEdge( source.getRootGraphIndex(), target.getRootGraphIndex(), directed );
  }
 
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
  public int createEdge ( int source_index, int target_index ) {
    return root.createEdge( source_index, target_index );
  }
 
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
  public int createEdge (int source_index,
                         int target_index,
                         boolean directed
                         ) {
    return root.createEdge( source_index,
                            target_index,
                            directed );
  }

  /**
   * Create some Edges.  The indices of the sources and the targets are given
   * in two arrays, which must be of equal length, and the indices of the
   * created edges are returned in an array of the same length.  The newly
   * created Edges will be directed iff the boolean argument is true.<p>
   * If returnArr represents the int[] that is returned by this method, then
   * returnArr[i] defines the index of newly created edge whose source node index
   * is source_indices[i] and whose target node index is
   * target_indices[i].
   * @param source_indices a non-null array of the indices of the source Nodes for the
   * new Edges.
   * @param target_indices a non-null array of length <tt>source_indices.length</tt> of
   * the indices of the target Nodes for the new Edges.
   * @param directed The new Edges will be directed iff this argument is true.
   * @return an array of length <tt>source_indices.length</tt> of the indices
   * of the newly created Edges; an entry of 0 in the returned array corresponds
   * an ill-defined source or target node index within the input parameters, at the same
   * array offset.
   */
  public int[] createEdges (int[] source_indices,
                            int[] target_indices,
                            boolean directed
                            ) {
    return root.createEdges( source_indices,
                             target_indices,
                             directed );
  }
 
  /**
   * Return true if the given Node is in this RootGraph.  False
   * otherwise.<p>
   * TECHNICAL DETAIL: The Node input parameter should be an object
   * that was previously returned by a method of this RootGraph or by
   * a method of another component that this RootGraph system defines.
   * If this is not the case, results of calling this method are undefined.
   * @return true iff the given Node is in this RootGraph.
   */
  public boolean containsNode ( Node node ) {
    return true;
  }
 
  /**
   * Return true if the given Edge is in this RootGraph.  False
   * otherwise.<p>
   * TECHNICAL DETAIL: The Edge input parameter should be an object
   * that was previously returned by a method of this RootGraph or by
   * a method of another component that this RootGraph system defines.
   * If this is not the case, results of calling this method are undefined.
   * @return true iff the given Edge is in this RootGraph.
   */
  public boolean containsEdge ( Edge edge ) {
    return true;
  }
 
  /**
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead;
   *   if you decide to use this method anyways, please note that the definition
   *   of "node neighbor" is such: Node A is a "node neighbor" of node B if and only
   *   if there exists an edge [directed or undirected] E such that A is E's target and
   *   B is E's source, or
   *   A is E's source and B is E's target; this method then returns a non-repeating list
   *   of all nodes N in this
   *   RootGraph such that N is a "node neighbor" of node, the input parameter.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public List neighborsList ( Node node ) {
    List n = root.neighborsList( root.getNode( node.getRootGraphIndex() ) );
    int[] array = new int[ n.size() ];
    int i = 0;
    for ( Iterator it = n.iterator(); it.hasNext(); ) {
      array[i] = ( ( Node )it.next() ).getRootGraphIndex();
      i++;
    }
    return intArray2CyNodeList( array );
  }
 
  /**
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead;
   *   if you decide to use this method anyways, please note that the definition
   *   of "node neighbor" is such: Node A is a "node neighbor" of node B if and only
   *   if there exists an edge [directed or undirected] E such that A is E's target and
   *   B is E's source, or A is E's source and B is E's target; this method then returns
   *   true if and only if a_node is a "node neighbor" of another_node in this RootGraph.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public boolean isNeighbor ( Node a_node, Node another_node ) {
    return root.isNeighbor( a_node.getRootGraphIndex(), another_node.getRootGraphIndex() );
  }
 
  /**
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead;
   *   if you decide to use this method anyways, please note that the definition
   *   of "node neighbor" is such: Node A is a "node neighbor" of node B if and only
   *   if there exists an edge [directed or undirected] E such that A is E's target and
   *   B is E's source, or A is E's source and B is E's target; this method then returns
   *   true if and only if node at index a_node_index is a "node neighbor" of node at
   *   index another_node_index in this RootGraph.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public boolean isNeighbor ( int a_node_index, int another_node_index ) {
    return root.isNeighbor( a_node_index, another_node_index );
  }
 
  /**
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead;
   *   if you decide to use this method anyways: this method returns true if and only if
   *   either 1) there exists a directed edge E in this RootGraph such that the from
   *   node specified is E's source node and the target node specified is E's target node
   *   or 2) there exists an undirected edge E in this RootGraph such that E's endpoints
   *   are the from and to nodes specified.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public boolean edgeExists ( Node from, Node to ) {
    return root.edgeExists( from.getRootGraphIndex(), to.getRootGraphIndex() );
  }
 
  /**
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead;
   *   if you decide to use this method anyways: this method returns true if and only if
   *   either 1) there exists a directed edge E in this RootGraph such that
   *   from_node_index is E's source node's index and to_node_index is E's target node's
   *   index or 2) there exists an undirected edge E in this RootGraph such that E's
   *   endpoint nodes have indices from_node_index and to_node_index.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public boolean edgeExists ( int from_node_index, int to_node_index ) {
    return root.edgeExists( from_node_index, to_node_index );
  }
 
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
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public int getEdgeCount ( Node from,
                            Node to,
                            boolean count_undirected_edges
                            ) {
    return root.getEdgeCount( from.getRootGraphIndex(),
                              to.getRootGraphIndex(),
                              count_undirected_edges );
  }
  
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
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public int getEdgeCount (int from_node_index,
                           int to_node_index,
                           boolean count_undirected_edges
                           ) {
    return root.getEdgeCount( from_node_index,
                         to_node_index,
                         count_undirected_edges );
  }

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
     boolean outgoing_directed_edges) {
    return root.getAdjacentEdgeIndicesArray( node_index,
                                             undirected_edges,
                                             incoming_directed_edges,
                                             outgoing_directed_edges );
  }

  /**
   * This will return an array of edge indices that are the edges between nodes.
   */
  public int[] getConnectingEdgeIndicesArray ( int[] node_indices ) {
    return root.getConnectingEdgeIndicesArray( node_indices );
  }

  /**
   * Return the nodes that connect the given edges.
   * @deprecated Use getEdgeSourceIndex(int) and getEdgeTargetIndex(int) instead.
   * @see #getEdgeSourceIndex(int)
   * @see #getEdgeTargetIndex(int)
   */
  public int[] getConnectingNodeIndicesArray ( int[] edge_indices ) {
    return root.getConnectingNodeIndicesArray( edge_indices );
  }

  /**
   * @return null is returned if either of the specified Nodes is not in this RootGraph.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public int[] getEdgeIndicesArray ( 
                                    int from_node_index,
                                    int to_node_index,
                                    boolean include_undirected_edges,
                                    boolean include_both_directions
                                    ) {
    return root.getEdgeIndicesArray( from_node_index,
                                     to_node_index,
                                     include_undirected_edges,
                                     include_both_directions );
  }

  /**
   * Return a new List of the Edges from the first given Node to the second
   * given Node.
   * @param from the Node that is the source of the Edges to be returned.
   * @param to the Node that is the target of the Edges to be returned.
   * @return a new List of the Edges from the <tt>from</tt> Node to the
   * <tt>to</tt> Node, or the empty List if none exist; null is returned if either
   * of the specified nodes is not in this RootGraph.
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public List edgesList ( Node from, Node to ) {
  
    int[] edges = root.getEdgeIndicesArray( from.getRootGraphIndex(), to.getRootGraphIndex(), true, true );
    return intArray2CyEdgeList( edges );
  }
 
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
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public List edgesList (int from_node_index,
                         int to_node_index,
                         boolean include_undirected_edges
                         ) {
    return intArray2CyEdgeList( root.getEdgeIndicesArray( from_node_index,
                                                          to_node_index,
                                                          include_undirected_edges,
                                                          false ) );
  }

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
   * @deprecated Use getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean) instead.
   * @see #getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean)
   */
  public int[] getEdgeIndicesArray (int from_node_index,
                                    int to_node_index,
                                    boolean include_undirected_edges
                                    ) {
    return root.getEdgeIndicesArray( from_node_index,
                                     to_node_index,
                                     include_undirected_edges );
  }

  /**
   * Return the number of Edges <tt><i>e</i></tt> such that
   * <tt><i>e</i>.getTarget().equals( node )</tt>.  Note that this includes
   * undirected edges, so it will not always be the case that <tt>getInDegree(
   * node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node the Node to count in-edges of.
   * @return the in-degree of the given Node, or -1 if the specified Node is not
   *   in this RootGraph.
   */
  public int getInDegree ( Node node ) {
    return root.getInDegree( node.getRootGraphIndex() );
  }
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> such that
   * <tt><i>e</i>.getTarget().equals( node )</tt>.  Note that this includes
   * undirected edges, so it will not always be the case that <tt>getInDegree(
   * node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count in-edges of.
   * @return the in-degree of the Node with the given index, or -1 if this
   *   RootGraph has no Node with specified index.
   */
  public int getInDegree ( int node_index ) {
    return root.getInDegree( node_index );
  }
 
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
  public int getInDegree ( Node node, boolean count_undirected_edges ) {
    return root.getInDegree( node.getRootGraphIndex(), count_undirected_edges );
  }
 
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
  public int getInDegree ( int node_index, boolean count_undirected_edges ) {
    return root.getInDegree( node_index, count_undirected_edges );
  }
 
  /**
   * Return the number of Edges <tt><i>e</i></tt> such
   * that <tt><i>e</i>.getSource().equals( node )</tt>.  Note that this
   * includes undirected edges, so it will not always be the case that
   * <tt>getInDegree( node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node the Node to count out-edges of.
   * @return the out-degree of the given Node, or -1 if specified Node is not
   *   in this RootGraph.
   */
  public int getOutDegree ( Node node ) {
    return root.getOutDegree( node.getRootGraphIndex() );
  }
 
  /**
   * Return the number of Edges <tt><i>e</i></tt>such that
   * <tt><i>e</i>.getSource().equals( node )</tt>.  Note that this includes
   * undirected edges, so it will not always be the case that <tt>getInDegree(
   * node ) + getOutDegree( node ) == getDegree( node )</tt>.
   * @param node_index the index of the Node to count out-edges of.
   * @return the out-degree of the Node with the given index or -1 if index
   *   specified does not correspond to a Node in this RootGraph.
   */
  public int getOutDegree ( int node_index ) {
    return root.getOutDegree( node_index );
  }
 
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
  public int getOutDegree ( Node node, boolean count_undirected_edges ) {
    return root.getOutDegree( node.getRootGraphIndex(), count_undirected_edges );
  }
 
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
  public int getOutDegree ( int node_index, boolean count_undirected_edges ) {
    return root.getOutDegree( node_index,
                              count_undirected_edges );
  }
 
  /**
   * Return the number of distinct Edges incident on the given Node.  By
   * 'distinct' we mean that no Edge will be counted twice, even if it is
   * undirected.
   * @return the degree of the given Node or -1 if specified Node is not in
   *   this RootGraph.
   */
  public int getDegree ( Node node ) {
    return root.getDegree( node.getRootGraphIndex() );
  }
 
  /**
   * Return the number of distinct Edges incident on the Node with the given
   * index.  By 'distinct' we mean that no Edge will be counted twice, even if
   * it is undirected.
   * @return the degree of the Node with the given index or -1 if this
   *   RootGraph has no Node with specified index.
   */
  public int getDegree ( int node_index ) {
    return root.getDegree( node_index );
  }

  /**
   * Return the index of the given Node.  Each Node has a unique index which is
   * guaranteed to remain the same throughout the lifetime of the Node and its
   * RootGraph.  Node indices are always <= -1, and are not guaranteed to be
   * contiguous.  This method simply returns node.getRootGraphIndex() after
   * checking that it is in this ColtRootGraph.<p>
   * TECHNICAL DETAIL: The Node input parameter should be an object
   * that was previously returned by a method of this RootGraph or by
   * a method of another component that this RootGraph system defines.
   * If this is not the case, results of calling this method are undefined.
   * @param node the Node to find a corresponding index for.
   * @return the index of the given Node in this RootGraph or 0 if it is not in
   * this RootGraph.
   */
  public int getIndex ( Node node ) {
    return node.getRootGraphIndex();
  }
 
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
  public Node getNode ( int node_index ) {
    //GINY_MOD
    
    // only use the CyNodes that are defiend in the Cytoscape API
    CyNode cynode;
    cynode = ( CyNode )nodes.get( node_index );
    if ( cynode == null ) {
      cynode = new CyNode( node_index, this );
      nodes.put( node_index, cynode );
    }
    return cynode;
  }
 
  /**
   * Return the index of the given Edge.  Each Edge has a unique index which is
   * guaranteed to remain the same throughout the lifetime of the Edge and its
   * RootGraph.  Edge indices are always <= -1, and are not guaranteed to be
   * contiguous.  This method simply returns edge.getRootGraphIndex() after
   * checking that it is in this ColtRootGraph.<p>
   * TECHNICAL DETAIL: The Edge input parameter should be an object
   * that was previously returned by a method of this RootGraph or by
   * a method of another component that this RootGraph system defines.
   * If this is not the case, results of calling this method are undefined.
   * @param edge the Edge to find a corresponding index for.
   * @return the index of the given Edge in this RootGraph or 0 if it is not in
   * this RootGraph.
   */
  public int getIndex ( Edge edge ) {
    return root.getIndex( edge );
  }
 
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
  public Edge getEdge ( int edge_index ) {
    //GINY_MOD

    // only use the CyEdges that are defiend in the Cytoscape API
    CyEdge cyedge;
    cyedge = ( CyEdge )edges.get( edge_index );
    if ( cyedge == null ) {
      cyedge = new CyEdge( edge_index, this );
      edges.put( edge_index, cyedge );
    }
    return cyedge;
  }
 
  /**
   * Retrieve the index of the Node that is the source of the Edge with the
   * given index.  Note that if the edge is undirected, the edge also connects
   * the target to the source.
   * @param edge_index the index in this RootGraph of the Edge
   * @return the index in this RootGraph of the Edge's source Node or 0
   *   if there's no Edge at specified index in this RootGraph.
   */
  public int getEdgeSourceIndex ( int edge_index ) {
    return root.getEdgeSourceIndex( edge_index );
  }
 
  /**
   * Retrieve the index of the Node that is the target of the Edge with the
   * given index.  Note that if the edge is undirected, the edge also connects
   * the target to the source.
   * @param edge_index the index in this RootGraph of the Edge
   * @return the index in this RootGraph of the Edge's target Node or 0
   *   if there's no Edge at specified index in this RootGraph.
   */
  public int getEdgeTargetIndex ( int edge_index ) {
    return root.getEdgeTargetIndex( edge_index );
  }
 
  /**
   * Retrieve the directedness of the Edge with the
   * given index.  Note that if the edge is undirected, the edge also connects
   * the target to the source.
   * @param edge_index the index in this RootGraph of the Edge
   * @return true iff the edge is directed; if no Edge at specified index exists
   *   in this RootGraph, the result of this method is undefined.
   */
  public boolean isEdgeDirected ( int edge_index ) {
    return root.isEdgeDirected( edge_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * addMetaChild establishes a contains-a relationship between the first
   * argument (the <tt>parent</tt>) and the second argument (the
   * <tt>child</tt>).  It returns true unless the meta relationship already
   * existed.
   * @param parent the Node that is to be the parent (the contain<i>er</i>) in
   * the contains-a relationship that we are creating.
   * @param child the Node that is to be the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are creating.
   * @return true unless they were already meta-parent, meta-child to each
   * other.
   */
  public boolean addMetaChild ( Node parent, Node child ) {
    return root.addNodeMetaChild( parent.getRootGraphIndex(), child.getRootGraphIndex() );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * addMetaChild establishes a contains-a relationship between the Node
   * corresponding to the first argument (<tt>parent_index</tt>) and the Node
   * corresponding to the second argument (<tt>child_index</tt>).  It returns
   * true unless the meta relationship already existed.
   * @param parent_index the index in this RootGraph of the Node that is to be
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are creating.
   * @param child_index the index in this RootGraph of the Node that is to be
   * the child (the contain<i>ee</i>) in the contains-a relationship that we
   * are creating.
   * @return true unless they were already meta-parent, meta-child to each
   * other.
   */
  public boolean addNodeMetaChild ( int parent_index, int child_node_index ) {
    return root.addNodeMetaChild( parent_index, child_node_index );
  }
 
  public boolean removeNodeMetaChild ( int parent_index, int child_node_index ) {
    return root.removeNodeMetaChild( parent_index, child_node_index );
  }

  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * isMetaParent returns true iff the second argument (<tt>parent</tt>) is an
   * MetaParent of the first argument (<tt>child</tt>).  Calls {@link
   * #isNodeMetaParent( int, int )}.
   * <br>
   * Note the inverse relationship between this method and {@link #isMetaChild(
   * Node, Node )}: <tt>isMetaChild( parent, child ) == isMetaParent( child, parent
   * )</tt>.
   * @param child the Node that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaParent of the former argument.
   */
  public boolean isMetaParent ( Node child, Node parent ) {
    return root.isNodeMetaParent( child.getRootGraphIndex(), parent.getRootGraphIndex() );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * isMetaParent returns true iff the Node corresponding to the second argument
   * (<tt>parent_index</tt>) is a MetaParent of the Node corresponding to the
   * first argument (<tt>child_index</tt>).
   * <br>
   * Note the inverse relationship between this method and {@link
   * #isNodeMetaChild( int, int )}: <tt>isNodeMetaChild( parent_index, child_index )
   * == isNodeMetaParent( child_index, parent_index )</tt>.
   * @param child_node_index the index of the Node that is the child (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @param parent_index the index of the Node that is the parent (the
   * contain<i>er</i>) in the contains-a relationship that we are querying.
   * @return true iff the Node corresponding to the latter argument is an
   * MetaParent of the Node corresponding to the former argument.
   */
  // TODO: Add to interface
  public boolean isNodeMetaParent ( int child_node_index, int parent_index ) {
    return root.isNodeMetaParent( child_node_index, parent_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * metaParentsList returns a new List of the MetaParents of the given Node.  If
   * there are no MetaParents then the result will be null.  Calls {@link
   * #nodeMetaParentsList( int )}.
   * @param node the Node that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Nodes that contain the given Node, or null if
   * there are none.
   */
  public List metaParentsList ( Node node ) {
    int[] parents = root.getNodeMetaParentIndicesArray( node.getRootGraphIndex() );
    return intArray2CyNodeList( parents );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * nodeMetaParentsList returns a new List of the MetaParents of the Node with the
   * given index.  If there are no MetaParents then the result will be null.
   * @param node_index the index of the Node that is the child (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @return a new List of the Nodes that contain the Node with the given
   * index, or null if the index is 0 or if there are none.
   */
  public List nodeMetaParentsList ( int node_index ) {
    int[] parents = root.getNodeMetaParentIndicesArray( node_index );
    return intArray2CyNodeList( parents );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaParents.
   * getNodeMetaParentIndicesArray returns an array of the MetaParents of the Node
   * with the given index.  If there are no MetaParents then the result will be
   * null.
   * @param node_index the index of the Node that is the child (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @return an array of the indices of the Nodes that contain the Node with
   * the given index, or null if the index is 0 or if there are none.
   */
  public int[] getNodeMetaParentIndicesArray ( int node_index ) {
    return root.getNodeMetaParentIndicesArray( node_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the second argument (<tt>child</tt>) is an
   * MetaChild of the first argument (<tt>parent</tt>).  Calls {@link
   * #isNodeMetaChild( int, int )}.
   * <br>
   * Note the inverse relationship between this method and {@link #isMetaParent(
   * Node, Node )}: <tt>isMetaChild( parent, child ) == isMetaParent( child, parent
   * )</tt>.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @param child the Node that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaChild of the former argument.
   */
  public boolean isMetaChild ( Node parent, Node child ) {
    return root.isNodeMetaChild( parent.getRootGraphIndex(), child.getRootGraphIndex() );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the Node corresponding to the second argument
   * (<tt>child_index</tt>) is a MetaChild of the Node corresponding to the first
   * argument (<tt>parent_index</tt>).
   * <br>
   * Note the inverse relationship between this method and {@link
   * #isNodeMetaParent( int, int )}: <tt>isNodeMetaChild( parent_index, child_index )
   * == isNodeMetaParent( child_index, parent_index )</tt>.
   * @param parent_index the index of the Node that is the parent (the
   * contain<i>er</i>) in the contains-a relationship that we are querying.
   * @param child_node_index the index of the Node that is the child (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @return true iff the Node corresponding to the latter argument is an
   * MetaChild of the Node corresponding to the former argument.
    */
  // TODO: Add to interface
  public boolean isNodeMetaChild ( int parent_index, int child_node_index ) {
    return root.isNodeMetaChild( parent_index, child_node_index );
  }

  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the Node corresponding to the second argument
   * (<tt>child_index</tt>) is a MetaChild of the Node corresponding to the first
   * argument (<tt>parent_index</tt>).
   * <br>
   * Note the inverse relationship between this method and {@link
   * #isNodeMetaParent( int, int )}: <tt>isNodeMetaChild( parent_index, child_index )
   * == isNodeMetaParent( child_index, parent_index )</tt>.
   * @param parent_index the index of the Node that is the parent (the
   * contain<i>er</i>) in the contains-a relationship that we are querying.
   * @param child_node_index the index of the Node that is the child (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @param recursive if true, the method will look for child_node_index recursively inside
   * descendants of parent_index
   * @return true iff the Node corresponding to the latter argument is an
   * MetaChild of the Node corresponding to the former argument.
   */
  public boolean isNodeMetaChild ( int parent_index, int child_node_index, boolean recursive) {
    return root.isNodeMetaChild( parent_index, child_node_index, recursive );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * nodeMetaChildrenList returns a new List of the MetaChildren of the given Node.
   * If there are no MetaChildren then the result will be null.  Calls {@link
   * #nodeMetaChildrenList( int )}.
   * @param node the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Nodes that are contained by the given Node, or
   * null if there are none.
   */
  public List nodeMetaChildrenList ( Node node ) {
    int[] children = root.getNodeMetaChildIndicesArray( node.getRootGraphIndex() );
    return intArray2CyNodeList( children );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * nodeMetaChildrenList returns a new List of the Node MetaChildren of the Node
   * with the given index.  If there are no MetaChildren then the result will be
   * null.
   * @param node_index the index of the Node that is the parent (the
   * contain<i>er</i>) in the contains-a relationship that we are querying.
   * @return a new List of the Nodes that are contained by the Node with the
   * given index, or null if the index is 0 or if there are none.
   */
  public List nodeMetaChildrenList ( int parent_index ) {
     int[] children = root.getNodeMetaChildIndicesArray( parent_index );
     return intArray2CyNodeList( children );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * getNodeMetaChildIndicesArray returns an array of the MetaChildren of the Node
   * with the given index.  If there are no MetaChildren then the result will be
   * null.
   * @param node_index the index of the Node that is the parent (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @return an array of the indices of the Nodes in this GraphPerspective that
   * are contained by the Node with the given index, or null if the index is 0
   * or if there are none.
   */
  public int[] getNodeMetaChildIndicesArray ( int node_index ) {
    return root.getNodeMetaChildIndicesArray( node_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * getNodeMetaChildIndicesArray returns an array of the MetaChildren of the Node
   * with the given index.  If there are no MetaChildren then the result will be
   * null.
   * @param node_index the index of the Node that is the parent (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @param recursive if true, then this method returns all descendants of node_index by
   * recursively searching inside node_index's children
   * @return an array of the indices of the Nodes in this GraphPerspective that
   * are contained by the Node with the given index, or null if the index is 0
   * or if there are none.
   */
  public int[] getNodeMetaChildIndicesArray ( int node_index, boolean recursive ) {
    return root.getNodeMetaChildIndicesArray( node_index, recursive );
  }

  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges. A Node may have any number of MetaChildren.  
   * This method returns an array of node RootGraph indices that belong to Nodes
   * that are descendants of Node with node_index and that do not have any children of their
   * own (one could call them leaves, except that this is not a tree).
   *
   * @param node_index the RootGraph index of the node for which childless descendants will
   * be returned
   * @return an array of node RootGraph indices that belong to Nodes
   * that are descendants of Node with node_index and that do not have any children of their
   * own (one could call them leaves, except that this is not a tree
   */
  public int[] getChildlessMetaDescendants ( int node_index ) {
    return root.getChildlessMetaDescendants( node_index );
  }


  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * addMetaChild establishes a contains-a relationship between the first
   * argument (the <tt>parent</tt>) and the second argument (the
   * <tt>child</tt>).  It returns true unless the meta relationship already
   * existed.
   * @param parent the Node that is to be the parent (the contain<i>er</i>) in
   * the contains-a relationship that we are creating.
   * @param child the Edge that is to be the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are creating.
   * @return true unless they were already meta-parent, meta-child to each
   * other.
   */
  public boolean addMetaChild ( Node parent, Edge child ) {
    return root.addEdgeMetaChild( parent.getRootGraphIndex(), child.getRootGraphIndex() );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * addMetaChild establishes a contains-a relationship between the Node
   * corresponding to the first argument (<tt>parent_index</tt>) and the Edge
   * corresponding to the second argument (<tt>child_index</tt>).  It returns
   * true unless the meta relationship already existed.
   * @param parent_index the index in this RootGraph of the Node that is to be
   * the parent (the contain<i>er</i>) in the contains-a relationship that we
   * are creating.
   * @param child_index the index in this RootGraph of the Edge that is to be
   * the child (the contain<i>ee</i>) in the contains-a relationship that we
   * are creating.
   * @return true unless they were already meta-parent, meta-child to each
   * other.
   */
  public boolean addEdgeMetaChild ( int parent_index, int child_edge_index ) {
    return root.addEdgeMetaChild( parent_index, child_edge_index );
  }
 
  public boolean removeEdgeMetaChild ( int parent_index, int child_edge_index ) {
    return root.removeEdgeMetaChild( parent_index, child_edge_index );
  }

  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * isMetaParent returns true iff the second argument (<tt>parent</tt>) is an
   * MetaParent of the first argument (<tt>child</tt>).  Calls {@link
   * #isEdgeMetaParent( int, int )}.
   * <br>
   * Note the inverse relationship between this method and {@link #isMetaChild(
   * Node, Edge )}: <tt>isMetaChild( parent, child ) == isMetaParent( child, parent
   * )</tt>.
   * @param child the Edge that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaParent of the former argument.
   */
  public boolean isMetaParent ( Edge child, Node parent ) {
    return root.isEdgeMetaParent( child.getRootGraphIndex(),
                                  parent.getRootGraphIndex() );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * isMetaParent returns true iff the Node corresponding to the second argument
   * (<tt>parent_index</tt>) is a MetaParent of the Edge corresponding to the
   * first argument (<tt>child_index</tt>).
   * <br>
   * Note the inverse relationship between this method and {@link
   * #isEdgeMetaChild( int, int )}: <tt>isEdgeMetaChild( parent_index, child_index )
   * == isEdgeMetaParent( child_index, parent_index )</tt>.
   * @param child_edge_index the index of the Edge that is the child (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @param parent_index the index of the Node that is the parent (the
   * contain<i>er</i>) in the contains-a relationship that we are querying.
   * @return true iff the Node corresponding to the latter argument is an
   * MetaParent of the Edge corresponding to the former argument.
   */
  // TODO: Add to interface
  public boolean isEdgeMetaParent ( int child_edge_index, int parent_index ) {
    return root.isEdgeMetaParent( child_edge_index, parent_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * metaParentsList returns a new List of the MetaParents of the given Edge.  If
   * there are no MetaParents then the result will be null.  Calls {@link
   * #edgeMetaParentsList( int )}.
   * @param edge the Edge that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Nodes that contain the given Edge, or null if
   * there are none.
   */
  public List metaParentsList ( Edge edge ) {
    int[] parents = root.getEdgeMetaParentIndicesArray( edge.getRootGraphIndex() );
    return intArray2CyEdgeList( parents );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * EdgeMetaParentsList returns a new List of the MetaParents of the Edge with the
   * given index.  If there are no MetaParents then the result will be null.
   * @param edge_index the index of the Edge that is the child (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @return a new List of the Nodes that contain the Edge with the given
   * index, or null if the index is 0 or if there are none.
   */
  public List edgeMetaParentsList ( int edge_index ) {
    return root.edgeMetaParentsList( edge_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  An Edge may have any number of MetaParents.
   * getEdgeMetaParentIndicesArray returns an array of the MetaParents of the Edge
   * with the given index.  If there are no MetaParents then the result will be
   * null.
   * @param edge_index the index of the Edge that is the child (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @return an array of the indices of the Nodes that contain the Edge with
   * the given index, or null if the index is 0 or if there are none.
   */
  public int[] getEdgeMetaParentIndicesArray ( int edge_index ) {
    return root.getEdgeMetaParentIndicesArray( edge_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the second argument (<tt>child</tt>) is an
   * MetaChild of the first argument (<tt>parent</tt>).  Calls {@link
   * #isEdgeMetaChild( int, int )}.
   * <br>
   * Note the inverse relationship between this method and {@link #isMetaParent(
   * Edge, Node )}: <tt>isMetaChild( parent, child ) == isMetaParent( child, parent
   * )</tt>.
   * @param parent the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @param child the Edge that is the child (the contain<i>ee</i>) in the
   * contains-a relationship that we are querying.
   * @return true iff the latter argument is a MetaChild of the former argument.
   */
  public boolean isMetaChild ( Node parent, Edge child ) {
    return root.isEdgeMetaChild( parent.getRootGraphIndex(), child.getRootGraphIndex() );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * isMetaChild returns true iff the Edge corresponding to the second argument
   * (<tt>child_index</tt>) is a MetaChild of the Node corresponding to the first
   * argument (<tt>parent_index</tt>).
   * <br>
   * Note the inverse relationship between this method and {@link
   * #isEdgeMetaParent( int, int )}: <tt>isEdgeMetaChild( parent_index, child_index )
   * == isEdgeMetaParent( child_index, parent_index )</tt>.
   * @param parent_index the index of the Node that is the parent (the
   * contain<i>er</i>) in the contains-a relationship that we are querying.
   * @param child_edge_index the index of the Edge that is the child (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @return true iff the Edge corresponding to the latter argument is an
   * MetaChild of the Node corresponding to the former argument.
    */
  // TODO: Add to interface
  public boolean isEdgeMetaChild ( int parent_index, int child_edge_index ) {
    return root.isEdgeMetaChild( parent_index, child_edge_index );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * edgeMetaChildrenList returns a new List of the Edge MetaChildren of the given
   * Node.  If there are no Edge MetaChildren then the result will be null.  Calls
   * {@link #edgeMetaChildrenList( int )}.
   * @param node the Node that is the parent (the contain<i>er</i>) in the
   * contains-a relationship that we are querying.
   * @return a new List of the Edges that are contained by the given Node, or
   * null if there are none.
   */
  public List edgeMetaChildrenList ( Node node ) {
    int[] children = root.getEdgeMetaChildIndicesArray( node.getRootGraphIndex() );
    return intArray2CyEdgeList( children );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * edgeMetaChildrenList returns a new List of the Edge MetaChildren of the Node
   * with the given index.  If there are no Edge MetaChildren then the result will
   * be null.
   * @param node_index the index of the Node that is the parent (the
   * contain<i>er</i>) in the contains-a relationship that we are querying.
   * @return a new List of the Edges that are contained by the Node with the
   * given index, or null if the index is 0 or if there are none.
   */
  public List edgeMetaChildrenList ( int node_index ) {
    int[] children = root.getEdgeMetaChildIndicesArray( node_index );
    return intArray2CyEdgeList( children );
  }
 
  /**
   * Nodes and Edges comprise an additional directed-acyclic-graph through the
   * contains-a relationship, in which a MetaParent Node contains each of its
   * MetaChild Nodes and Edges.  A Node may have any number of MetaChildren.
   * getEdgeMetaChildIndicesArray returns an array of the MetaChildren of the Node
   * with the given index.  If there are no MetaChildren then the result will be
   * null.
   * @param node_index the index of the Node that is the parent (the
   * contain<i>ee</i>) in the contains-a relationship that we are querying.
   * @return an array of the indices of the Edges that are contained by the
   * Node with the given index, or null if the index is 0 or if there are none.
   */
  public int[] getEdgeMetaChildIndicesArray ( int node_index ) {
    return root.getEdgeMetaChildIndicesArray( node_index );
  }


  private List intArray2CyNodeList ( int[] indices ) {
    List nodes = new ArrayList ( indices.length );
    for ( int i = 0; i < indices.length; ++i ) {
      nodes.add( getNode( indices[i] ) );
    }
    return nodes;
  }

  private int[] cyNodeList2intArray ( Collection nodes ) {
    int[] array = new int[ nodes.size() ];
    int i =0 ;
    for ( Iterator it  = nodes.iterator(); it.hasNext(); ) {
      array[i] = ( ( giny.model.Node )it.next() ).getRootGraphIndex();
      i++;
    }
    return array;
  }
     
  private List intArray2CyEdgeList ( int[] indices ) {
    List edges = new ArrayList ( indices.length );
    for ( int i = 0; i < indices.length; ++i ) {
      edges.add( getEdge( indices[i] ) );
    }
    return edges;
  }

  private int[] cyEdgeList2intArray ( Collection edges ) {
    int[] array = new int[ edges.size() ];
    int i =0 ;
    for ( Iterator it  = edges.iterator(); it.hasNext(); ) {
      array[i] = ( ( giny.model.Edge )it.next() ).getRootGraphIndex();
      i++;
    }
    return array;
  }
}
