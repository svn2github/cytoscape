package oiler;

import oiler.util.IntIterator;

/**
 * An interface for classes that implement the graph mathematical object.
 * For a definition of a graph, see: http://en.wikipedia.org/wiki/Graph_(mathematics)
 *
 * <p><h3>Fundamentals of <code>Graph</code></h3></p>
 * <p>
 * <ol>
 * <li>
 *     Every node has an associated, unique int value called the node index.
 *     Every edge has an associated, unique int value called the edge index.
 *     When a node or an edge is created, its associated index is returned.
 *     This index is used to modify or query the graph.<br>
 *     <b>Things to remember when dealing with indices:</b>
 *     <ul>
 *     <li>
 *         Do not mix node and edge indices. For example, when a method
 *         parameter is a node index, passing in an edge index will
 *         produce undefined results.
 *     </li>
 *     <li>
 *         Do not mix indices from another <code>Graph</code> instance. For example,
 *         if there are two instances <code>g1</code> and <code>g2</code>, do not use an
 *         edge index from <code>g2</code> for a method with a parameter for
 *         an edge index for <code>g1</code>.
 *     </li>
 *     <li>
 *         It is a good idea to call <code>edgeExists()</code> or <code>nodeExists()</code>
 *         to check if an index is valid.
 *     </li>
 *     </ul>
 * </li>
 * <li>
 *     Every node and every edge has its own associated object. The type of object
 *     associated with nodes is defined by the generic parameter <code>N</code>; the
 *     type for edges is defined by <code>E</code>. This allows generic definition
 *     of the information that can be stored with nodes and edges.
 * </li>
 * <li>
 *     <code>Graph</code> supports graph implementations with undirected edges, directed edges,
 *     or both types of edges in the same graph. Because of this, many methods have an
 *     <code>edgeType</code> parameter. This parameter specifies what type of edge ought to be
 *     inspected. For example, if one only wants directed edges, one would pass
 *     <code>DIRECTED_EDGE</code> to the <code>edgeType</code> parameter. Moreover, the
 *     values for the parameter can be logically or'ed together. For example, if one wants
 *     to inspect undirected edges and incoming, directed edges, one would pass
 *     <code>UNDIRECTED_EDGE | INCOMING_EDGE</code> to the <code>edgeType</code> parameter.
 * </li>
 * </ol>
 * </p>
 *
 * @author Samad Lotia
 */
public interface Graph<N,E> extends Comparable<Graph<N,E>>
{
	/**
	 * <code>edgeType</code> parameter: do not inspect any edges
	 */
	public static final byte NO_EDGE		= 0;

	/**
	 * <code>edgeType</code> parameter: inspect only undirected edges
	 */
	public static final byte UNDIRECTED_EDGE	= (1 << 0);

	/**
	 * <code>edgeType</code> parameter: inspect only incoming, directed edges.
	 */
	public static final byte INCOMING_EDGE		= (1 << 1);

	/**
	 * <code>edgeType</code> parameter: inspect only outgoing, directed edges
	 */
	public static final byte OUTGOING_EDGE		= (1 << 2);
	
	/**
	 * <code>edgeType</code> parameter: inspect all directed edges
	 */
	public static final byte DIRECTED_EDGE		= INCOMING_EDGE | OUTGOING_EDGE;

	/**
	 * <code>edgeType</code> parameter: inspect all edges
	 */
	public static final byte ANY_EDGE		= UNDIRECTED_EDGE | DIRECTED_EDGE;

	/**
	 * This index is always invalid for any method
	 */
	public static final int INVALID_INDEX		= Integer.MIN_VALUE;
	
	/**
	 * Returns the score of the graph
	 */
	public double score();

	/**
	 * Set the score of the graph
	 * (operation optional for immutable implementations).
	 * @throws UnsupportedOperationException if the
	 * implementation is immutable.
	 */
	public void setScore(double score);
	
	/**
	 * Returns <code>true</code> if the edge index is valid.
	 */
	public boolean edgeExists(int edgeIndex);
	
	/**
	 * Returns <code>true</code> if the node index is valid.
	 */
	public boolean nodeExists(int nodeIndex);

	/**
	 * Adds a new edge to the graph, going from the source node to
	 * the target node, and returns the edge index of the newly created
	 * edge
	 * (operation optional for immutable implementations).
	 *
	 * <p>The source and target nodes must exist in the graph before
	 * adding the edge.</p>
	 *
	 * @param sourceIndex The node index of the source node.
	 * @param targetIndex The node index of the target node.
	 * @param edgeObj     The edge object to be associated with the new edge.
	 * @param edgeType    A valid type for the new edge
	 *
	 * @return The edge-index of the new edge
	 *
	 * @throws InvalidIndexException Thrown if either sourceIndex or targetIndex is invalid
	 * @throws IllegalArgumentException Thrown if <code>edgeType</code> is not supported by the instance.
	 * @throws UnsupportedOperationException if the implementation is immutable.
	 */
	public int addEdge(int sourceIndex, int targetIndex, E edgeObj, byte edgeType);
	
	/**
	 * Adds a node with the specified node object to the graph
	 * (operation optional for immutable implementations).
	 *
	 * @param nodeObj The node object to be associated with the new node.
	 * @return The node index of the new node.
	 * @throws UnsupportedOperationException if the implementation is immutable.
	 */
	public int addNode(N nodeObj);

	/**
	 * Removes <code>edge</code> edge from the graph
	 * (operation optional for immutable implementations).
	 * Removing an edge will invalidate its edge index.
	 *
	 * @param edgeIndex The edge index to be removed.
	 * @throws InvalidIndexException Thrown if the edge index is invalid
	 * @throws UnsupportedOperationException if the implementation is immutable.
	 */
	public void removeEdge(int edgeIndex);
	
	/**
	 * Removes the node and all connecting edges from the graph
	 * (operation optional for immutable implementations).
	 * Removing a node will invalidate its node index and all edge indices
	 * connected to the removed node.
	 *
	 * @param nodeIndex The node-index to be removed.
	 * @throws InvalidIndexException Thrown if the node-index is invalid
	 * @throws UnsupportedOperationException if the implementation is immutable.
	 */
	public void removeNode(int nodeIndex);

	/**
	 * Returns the edge object associated with an edge.
	 * @param edgeIndex A valid edge index
	 * @throws InvalidIndexException Thrown if the edge index is invalid
	 */
	public E edgeObject(int edgeIndex);
	
	/**
	 * Returns a node object associated with a node.
	 * @param nodeIndex A valid node index
	 * @throws InvalidIndexException Thrown if the node index is invalid
	 */
	public N nodeObject(int nodeIndex);

	/**
	 * Associates an edge with an edge object
	 * (operation optional for immutable implementations).
	 * If another object is associated with the edge, it will be
	 * replaced by <code>edgeObj</code>
	 *
	 * @param edgeIndex An edge index
	 * @param edgeObj The object to be associated with the edge
	 * @throws InvalidIndexException Thrown if the edge index is invalid
	 * @throws UnsupportedOperationException if the implementation is immutable.
	 */
	public void setEdgeObject(int edgeIndex, E edgeObj);

	/**
	 * Associates an node index with a node object
	 * (operation optional for immutable implementations).
	 * If another object is associated with the node, it will be
	 * replaced by <code>nodeObj</code>
	 *
	 * @param nodeIndex An node index
	 * @param nodeObj The object to be associated with the node index
	 * @throws InvalidIndexException Thrown if the node index is invalid
	 * @throws UnsupportedOperationException if the implementation is immutable.
	 */
	public void setNodeObject(int nodeIndex, N nodeObj);
	
	/**
	 * Searches for the edge index associated with an edge object.
	 * @param edgeObj An edge object
	 * @return An edge index associated with <code>edgeObj</code>, or
	 *         <code>INVALID_INDEX</code> if there is not an edge with
	 *         <code>edgeObj</code>. If there are multiple edges with
	 *         the same edge object, it will return the edge index
	 *         it finds first.
	 */
	public int edgeIndex(E edgeObj);

	/**
	 * Searches for the node index associated with a node object.
	 * @param nodeObj An node object
	 * @return A node index associated with <code>nodeObj</code>, or
	 *         <code>INVALID_INDEX</code> if there is not an edge with
	 *         <code>nodeObj</code>. If there are multiple nodes with
	 *         the same node object, it will return the node index
	 *         it finds first.
	 */
	public int nodeIndex(N nodeObj);

	/**
	 * Returns the highest possible node index.
	 * This function is useful if one wants to create an array whose indices
	 * are possible node indices.
	 */
	public int maxNodeIndex();

	/**
	 * Returns an iterator for all valid edge indices in the graph.
	 */
	public IntIterator edges();

	/**
	 * Returns nn iterator for all valid node indices in the graph.
	 */
	public IntIterator nodes();

	/**
	 * Returns the number of nodes in the graph.
	 */
	public int nodeCount();

	/**
	 * Returns the number of edges in the graph.
	 */
	public int edgeCount();

	/**
	 * Returns the index of the source node of <code>edgeIndex</code>
	 * @throws InvalidIndexException Thrown if the edge index is invalid
	 */
	public int edgeSource(int edgeIndex);

	/**
	 * Returns the index of the target node of <code>edgeIndex</code>
	 * @throws InvalidIndexException Thrown if the edge index is invalid
	 */
	public int edgeTarget(int edgeIndex);

	/**
	 * Returns the type of edge, either <code>UNDIRECTED_EDGE</code> or <code>DIRECTED_EDGE</code>.
	 * @throws InvalidIndexException Thrown if the edge index is invalid
	 */
	public byte edgeType(int edgeIndex);

	/**
	 * Returns the default value for the <code>edgeType</code> parameter.
	 * This value varies between different implementations of <code>Graph</code>.
	 */
	public byte defaultEdgeType();

	/**
	 * Returns the degree of <code>nodeIndex</code>, considering edges of default type.
         * @throws InvalidIndexException Thrown if the node index is invalid
	 */
	public int degree(int nodeIndex);
	
	/**
	 * Returns the degree of <code>nodeIndex</code>, considering edges of type <code>edgeType</code>.
         * @throws InvalidIndexException Thrown if the node index is invalid
	 */
	public int degree(int nodeIndex, byte edgeType);

	/**
	 * Returns an iterator of all valid node indices adjacent to <code>nodeIndex</code>.
	 *         The iterator will only return adjacent nodes that are connected by edges
	 *         of the default type.
	 * @throws InvalidIndexException Thrown if the node index is invalid
	 */
	public IntIterator adjacentNodes(int nodeIndex);

	/**
	 * Returns an iterator of all valid node indices adjacent to <code>nodeIndex</code>.
	 *         The iterator will only return adjacent nodes that are connected by edges
	 *         of the type <code>edgeType</code>.
	 * @throws InvalidIndexException Thrown if the node index is invalid
	 */
	public IntIterator adjacentNodes(int nodeIndex, byte edgeType);

	/**
	 * Returns an iterator of all valid edge indices adjacent to <code>nodeIndex</code>.
	 *         The iterator will only return adjacent edges that are of the default type.
	 * @throws InvalidIndexException Thrown if the node index is invalid
	 */
	public IntIterator adjacentEdges(int nodeIndex);

	/**
	 * Returns an iterator of all valid edge indices adjacent to <code>nodeIndex</code>.
	 *         The iterator will only return adjacent edges that are of the type
	 *         <code>edgeType</code>.
	 * @throws InvalidIndexException Thrown if the node index is invalid
	 */
	public IntIterator adjacentEdges(int nodeIndex, byte edgeType);

	/**
	 * Returns the edge index of the first edge that it finds connecting the source
	 *         node to the target node. It will only look through edges of the
	 *         default type. It will return <code>INVALID_INDEX</code> if it cannot find
	 *         an edge connecting the source and target.
	 * @throws InvalidIndexException Thrown if the source or target node index is invalid
	 */
	public int firstEdge(int sourceIndex, int targetIndex);

	/**
	 * Returns the edge index of the first edge that it finds connecting the source
	 *         node to the target node. It will only look through edges of the
	 *         type <code>edgeType</code>. It will return <code>INVALID_INDEX</code> if it cannot find
	 *         an edge connecting the source and target.
	 * @throws InvalidIndexException Thrown if the source or target node index is invalid
	 */
	public int firstEdge(int sourceIndex, int targetIndex, byte edgeType);
}
