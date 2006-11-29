package nct;

/**
 * This interface provides means for adding, modifying, and removing nodes
 * and edges from a graph.
 *
 * @author Samad Lotia
 */
public interface MutableGraph<N,E> extends Graph<N,E>
{
	/**
	 * Adds a new edge to the graph, going from the source node to
	 * the target node, and returns the edge-index of the newly created
	 * edge.
	 *
	 * <p>The source and target nodes must exist in the graph before
	 * adding the edge.</p>
	 *
	 * @param sourceIndex The node-index of the source node.
	 * @param targetIndex The node-index of the target node.
	 * @param edgeObj     The edge object to be associated with the new
	 *		      edge.
	 * @return The edge-index of the new edge, or <tt>Integer.MIN_VALUE</tt>
	 *         if the method failed.
	 */
	public int addEdge(int sourceIndex, int targetIndex, E edgeObj);
	
	/**
	 * Adds a node with the specified node object to the graph.
	 *
	 * @param nodeObj The node object to be associated with the new node.
	 *
	 * @return The node-index of the new node.
	 */
	public int addNode(N nodeObj);

	/**
	 * Removes <tt>edge</tt> edge from the graph.
	 *
	 * @param edgeIndex The edge-index to be removed.
	 *
	 * @return <tt>true</tt> if the edge existed and was removed.
	 */
	public boolean removeEdge(int edgeIndex);
	
	/**
	 * Removes the node and all connecting edges from the graph.
	 *
	 * @param nodeIndex The node-index to be removed.
	 *
	 * @return <tt>true</tt> if the node existed and was removed.
	 */
	public boolean removeNode(int nodeIndex);

	/**
	 * Associates an edge-index with an edge object.
	 *
	 * @param edgeIndex An edge-index
	 * @param edgeObj The object to be associated with the edge-index
	 */
	public boolean setEdgeObject(int edgeIndex, E edgeObj);

	/**
	 * Associates an node-index with a node object.
	 *
	 * @param nodeIndex An node-index
	 * @param nodeObj The object to be associated with the node-index
	 */
	public boolean setNodeObject(int nodeIndex, N nodeObj);
}
