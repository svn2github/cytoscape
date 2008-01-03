package org.cytoscape.model.graph;

/**
 * A Graph is a low-level representation of a vertices and the
 * connection between them.
 */
public interface Graph {

	/**
	 * Return the identifier for this graph
	 *
	 * @return graph identifier
	 */
	public int getGraphIdentifier();

	/**
	 * Add a new node to the graph
	 *
	 * @return node identifier
	 */
	public int addNode();

	/**
	 * Remove a node from the graph.  This will also remove
	 * any connecting edges.
	 *
	 * @param nodeID node to be removed
	 * @return true if the node was successfully removed
	 */
	public boolean removeNode(int nodeID);

	/**
	 * Add a new edge to the graph
	 *
	 * @param source the node that is the start of the edge
	 * @param type the type of the edge
	 * @param target the node that is the end of the edge
	 * @param isDirected if "true" this is a directed edge
	 * @return edge identifier
	 */
	public int addEdge(int source, String type, int target, boolean isDirected);

	/**
	 * Remove an edge from the graph
	 *
	 * @param edgeID edge to be removed
	 * @return true if the edge was successfully removed
	 */
	public boolean removeEdge(int edgeID);

	/**
	 * Get the source node for an edge
	 *
	 * @param edgeID the edge identifier
	 * @return the node identifier for the source node
	 */
	public int getEdgeSource(int edgeID);

	/**
	 * Get the target node for an edge
	 *
	 * @param edgeID the edge identifier
	 * @return the node identifier for the target node
	 */
	public int getEdgeTarget(int edgeID);

	/**
	 * Get the type for this edge
	 *
	 * @param edgeID the edge identifier
	 * @return the edge type
	 */
	public String getEdgeType(int edgeID);

	/**
	 * See if this edge is directed or not
	 *
	 * @param edgeID the edge identifier
	 * @return 'true' if this is a directed edge
	 */
	public boolean getEdgeIsDirected(int edgeID);

	/**
	 * Return the number of nodes in the graph.
	 *
	 * @return node count
	 */
	public int getNodeCount();

	/**
	 * Return all nodes in the graph.
	 *
	 * @return array node identifiers
	 */
	public int[] getNodeArray();

	/**
	 * Return the number of edges in the graph.
	 *
	 * @return edge count
	 */
	public int getEdgeCount();

	/**
	 * Return all edges in the graph.
	 *
	 * @return array edge identifiers
	 */
	public int[] getEdgeArray();

	/**
	 * See if a node with this identifier is in this graph
	 *
	 * @param nodeID the node identifier
	 * @return 'true' if the node is in this graph
	 */
	public boolean containsNode (int nodeID);

	/**
	 * See if an edge with this identifier is in this graph
	 *
	 * @param edgeID the edge identifier
	 * @return 'true' if the edge is in this graph
	 */
	public boolean containsEdge (int edgeID);

	/**
	 * See if an edge connecting these nodes is in this graph
	 *
	 * @param source the node identifier of the edge source
	 * @param target the node identifier of the edge target
	 * @param type a (possibly null) edge type to restrict the test to
	 * @return 'true' if the edge is in this graph
	 */
	public boolean containsEdge (int source, int target, String type);

	/**
	 * Get the nodes who are neighbors of this node
	 *
	 * @param nodeID the identifier of the node to get the neighbors of
	 * @param type a (possibly null) edge type to restrict the search to
	 * @param inComing include all nodes who are sources of edges connected to this node
	 * @param outGoing include all nodes that are targets of edges connected to this node
	 * @return array of neighbor node identifiers
	 */
	public int[] getNeighborNodes(int nodeID, String type, boolean inComing, boolean outGoing);

	/**
	 * Get the edges that are adjacent to this node
	 *
	 * @param nodeID the identifier of the node
	 * @param type a (possibly null) edge type to restrict the result to
	 * @param inComing include all edges that have this node as a target
	 * @param outGoing include all edges that have this node as a source
	 * @return array of edge identifiers
	 */
	public int[] getAdjacentEdges(int nodeID, String type, boolean inComing, boolean outGoing);

	/**
	 * Get the edges that connect two nodes.
	 *
	 * @param source the node identifier of the source node
	 * @param target the node identifier of the target node
	 * @param type a (possibly null) edge type to restrict the result to
	 * @param ignoreDirection ignore the direction of the edges and return all edges.  
	 *        If this flag is false, only those directed edges with 'source' as a source 
	 *        and 'target' as a target will be returned (undirected edges will always be returned).
	 * @return the connecting edge identifiers
	 */
	public int[] getConnectingEdgeList(int source, int target, String type, boolean ignoreDirection);

	/**
	 * Add a new listener
	 *
	 * @param changeListener the graph change listener to add
	 */
	public void addChangeListener(GraphChangeListener changeListener);

	/**
	 * Remove a change listener
	 *
	 * @param changeListener the graph change listener to remove
	 */
	public void removeChangeListener(GraphChangeListener changeListener);
}
